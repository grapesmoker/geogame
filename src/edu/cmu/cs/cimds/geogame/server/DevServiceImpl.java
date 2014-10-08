package edu.cmu.cs.cimds.geogame.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.cmu.cs.cimds.geogame.client.exception.AuthorizationException;
import edu.cmu.cs.cimds.geogame.client.exception.DBException;
import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;
import edu.cmu.cs.cimds.geogame.client.exception.InvalidArgsException;
import edu.cmu.cs.cimds.geogame.client.model.db.Action;
import edu.cmu.cs.cimds.geogame.client.model.db.Item;
import edu.cmu.cs.cimds.geogame.client.model.db.ItemType;
import edu.cmu.cs.cimds.geogame.client.model.db.Location;
import edu.cmu.cs.cimds.geogame.client.model.db.Road;
import edu.cmu.cs.cimds.geogame.client.model.db.RoadMovement;
import edu.cmu.cs.cimds.geogame.client.model.db.ServerSettingsStruct;
import edu.cmu.cs.cimds.geogame.client.model.db.User;
import edu.cmu.cs.cimds.geogame.client.model.dto.ServerSettingsStructDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.model.enums.ActionType;
import edu.cmu.cs.cimds.geogame.client.model.enums.LocationType;
import edu.cmu.cs.cimds.geogame.client.model.enums.RoadType;
import edu.cmu.cs.cimds.geogame.client.services.DevService;
import edu.cmu.cs.cimds.geogame.client.util.Pair;
import edu.cmu.cs.cimds.geogame.client.util.UserDegreeComparator;

public class DevServiceImpl extends RemoteServiceServlet implements DevService {

	protected PersistenceManager dbMgr = new PersistenceManager();

	private static final long serialVersionUID = -3825184337483171190L;
	
	private static Logger logger = Logger.getLogger(DevServiceImpl.class);
	public static long lastResetTime = 0;
	public static boolean resetFlag = false;
	
	private static final int MINIMUM_DURATION = 5000;
	private static final int MAXIMUM_DURATION = 20000;
	
	public static final int MIN_ITEM_DISTANCE = 2;
	public static final int MAX_ITEM_DISTANCE = 4;
	
	private static HashMap<User, Location> userLocations = new HashMap<User, Location>();
	private static HashMap<Location, List<Item> > itemLocations = new HashMap<Location, List<Item>>();
	
	static {
		MessageServiceImpl.initialize();
	}

	@Override
	public void createLocation(UserDTO player, String locationName, double latitude, double longitude) throws GeoGameException {
		logger.info("Adding location " + locationName + " at (" + latitude + "," + longitude + ")");
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
//			TransactionLog tl = new TransactionLog("createLocation", player.getUsername());
//			tl.startTime = System.currentTimeMillis();
			
			User user = AuthenticationUtil.authenticatePlayer(session, player);
			if(!user.isAdmin()) {
				tx.commit();
				throw new AuthorizationException(player, "User " + player.getUsername() + " is not authorized for Dev Operations");
			}
			
			Location location = new Location();
			location.setName(locationName);
			location.setLatitude(latitude);
			location.setLongitude(longitude);
			location.setLocationType(LocationType.AIRPORT);
			location.setIconFilename("puzzle.png");
			session.saveOrUpdate(location);

//			tl.endTime = System.currentTimeMillis();
//			GameServiceImpl.transactionLog.add(tl);
			tx.commit();
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage(), ex);
			throw new DBException(ex);
		} catch (Exception ex) {
			tx.rollback();
			logger.error(ex.getMessage(), ex);
		}
	}

	@Override
	public void createRoad(UserDTO player, String name, long location1Id, long location2Id, String roadPointsString) throws GeoGameException {
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
//			TransactionLog tl = new TransactionLog("createRoad", player.getUsername());
//			tl.startTime = System.currentTimeMillis();

			User user = AuthenticationUtil.authenticatePlayer(session, player);
			if(!user.isAdmin()) {
				tx.commit();
				throw new AuthorizationException(player, "User " + player.getUsername() + " is not authorized for Dev Operations");
			}

			Location location1 = (Location)session.createCriteria(Location.class)
					.add(Restrictions.idEq(location1Id))
					.uniqueResult();
			Location location2 = (Location)session.createCriteria(Location.class)
					.add(Restrictions.idEq(location2Id))
					.uniqueResult();
			if(location1==null || location2==null) {
				throw new InvalidArgsException("Location(s) not found");
			}
			if(location1==location2) {
				throw new InvalidArgsException("The road cannot connect a location to itself");
			}
			logger.info("Adding road between locations " + location1.getName() + " and " + location2.getName());
			
			Road road = new Road();
			road.setName(name);
			road.setLocation1(location1);
			road.setLocation2(location2);
			road.setRoadPointsString(roadPointsString);
			road.setRoadType(RoadType.HIGHWAY);
			
			session.saveOrUpdate(road);

//			tl.endTime = System.currentTimeMillis();
//			GameServiceImpl.transactionLog.add(tl);
			tx.commit();
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage(), ex);
			throw new DBException(ex);
		}
	}

	@SuppressWarnings("unused")
	private void itemDistributionFixed(Session session, List<User> users, List<User> activeUsers, List<Item> items, List<Location> locations, List<ItemType> itemTypes ) {
		int numActiveUsers = activeUsers.size();
		int numLocations = locations.size();
		int numItemTypes = itemTypes.size();
		int numUsers = users.size();
		
		Random randomizer = new Random();
		
		//Assigning non-repeating random goal items for each player
		int NUM_GOAL_ITEMS_PER_PLAYER = GameServiceImpl.NUM_QUEST_ITEMS;
		int NUM_RANDOM_EXTRA_ITEMS = 20;
		
		int ITEMS_TO_PLACE = 24;
		
		/*
		 * HOLY FUCKING HACK BATMAN!
		 * 
		 * So the idea here is that if we set the number of items to be constant at 24, and set all the items to be replenishable,
		 * we don't have to come up with a new item distribution algorithm for this particular set of runs. THIS IS NOT A GOOD WAY
		 * OF DOING THINGS but it's the way we're going to do it right now because there's no time to do it right. In general, this
		 * whole horrible reset function needs to be refactored into something that actually makes sense.
		 * 
		 * JV 11/04/11
		 */
		ITEMS_TO_PLACE = 24;
		
		// if we need to assign items to new locations for the first time, do so
			
		// create a list of items at every location
		itemLocations.clear();
		for (Location location : locations) {
			itemLocations.put(location, new ArrayList<Item>());
		}
		// assign items to locations
		Collections.shuffle(itemTypes);
		Collections.shuffle(locations);
		for(int i = 0; i < ITEMS_TO_PLACE; i++) {
			Location location = locations.get(i % numLocations);
			ItemType itemType = itemTypes.get(i % numItemTypes);
			Item newItem = GameServiceImpl.createItem(session, itemType, location);
			items.add(newItem);
			// keep track of what items are assigned to which locations
			itemLocations.get(location).add(newItem);
		}
		
		Collections.shuffle(locations);
		
		//Pool from which the "available" items are taken out.
		List<Item> itemPool = new ArrayList<Item>(items);
		
		for(int i = 0; i < numUsers; i++) {
			User user = users.get(i);
//			user.getNeighbors().clear();		//Clear neighbors
			user.getItemsToCollect().clear();	//Clear items to collect
			user.setScore(0);
			user.setCurrentLocation(locations.get(i%numLocations));
			user.setCurrentRoad(null);
			user.setMoving(false);
			RoadMovement roadMovement = user.getCurrentRoadMovement();
			if(roadMovement!=null) {
				user.setCurrentRoadMovement(null);
				roadMovement.setRoad(null);
				roadMovement.setUser(null);
				session.delete(roadMovement);
			}
			
			//Add items in the map only for active users
			if(!activeUsers.contains(user)) {
				continue;
			}
			
			List<Item> targetItems = getItemsAtMidRange(NUM_GOAL_ITEMS_PER_PLAYER, itemPool, user.getCurrentLocation(), MIN_ITEM_DISTANCE, MAX_ITEM_DISTANCE, locations, MessageServiceImpl.locationDistanceMap);
	
			for(Item item : targetItems) {
				GameServiceImpl.createGoalItem(session, user, item.getItemType());
				itemPool.remove(item);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void itemDistributionOld (Session session, List<User> users, List<User> activeUsers, List<Item> items, List<Location> locations, List<ItemType> itemTypes ) {
		
		int numActiveUsers = activeUsers.size();
		int numLocations = locations.size();
		int numItemTypes = itemTypes.size();
		int numUsers = users.size();
		
		Random randomizer = new Random();
		
		//Assigning non-repeating random goal items for each player
		int NUM_GOAL_ITEMS_PER_PLAYER = GameServiceImpl.NUM_QUEST_ITEMS;
		int NUM_RANDOM_EXTRA_ITEMS = 20;
		
		int ITEMS_TO_PLACE = (NUM_GOAL_ITEMS_PER_PLAYER * numActiveUsers) + NUM_RANDOM_EXTRA_ITEMS;
		
		// if we need to assign items to new locations for the first time, do so
		if (true) {
			
			// create a list of items at every location
			itemLocations.clear();
			for (Location location : locations) {
				itemLocations.put(location, new ArrayList<Item>());
			}
			// assign items to locations
			Collections.shuffle(itemTypes);
			Collections.shuffle(locations);
			for(int i = 0; i < ITEMS_TO_PLACE; i++) {
				Location location = locations.get(i % numLocations);
				ItemType itemType = itemTypes.get(i % numItemTypes);
				Item newItem = GameServiceImpl.createItem(session, itemType, location);
				items.add(newItem);
				// keep track of what items are assigned to which locations
				itemLocations.get(location).add(newItem);
			}
		}
		// otherwise we are simply rotating the item sets 
		else {
			/*List<Location> itemsAtLocations = (List<Location>) itemLocations.keySet();
			List<List<Item>> itemLists = (List<List<Item>>) itemLocations.values();
			
			// rotate the items
			Collections.rotate(itemLists, 3);
			Collections.rotate(itemsAtLocations, -2);
			
			// set up some iterators
			Iterator locationIterator = itemsAtLocations.iterator();
			Iterator itemListIterator = itemLists.iterator();
			
			// reassign the items to the new locations
			for ( ; locationIterator.hasNext() && itemListIterator.hasNext();  ) {
				List<Item> itemList = (List<Item>) itemListIterator.next();
				Location loc = (Location) locationIterator.next();
				itemLocations.put(loc, itemList);
			}*/
			
			for (Location location : locations) {
				List<Item> itemsHere = itemLocations.get(location);
				for (Item item : itemsHere) {
					GameServiceImpl.createItem(session, item.getItemType(), location);
					items.add(item);
				}
			}
		}
		
		Collections.shuffle(locations);
		
		//Pool from which the "available" items are taken out.
		List<Item> itemPool = new ArrayList<Item>(items);
		
		for(int i = 0; i < numUsers; i++) {
			User user = users.get(i);
//			user.getNeighbors().clear();		//Clear neighbors
			user.getItemsToCollect().clear();	//Clear items to collect
			user.setScore(0);
			user.setCurrentLocation(locations.get(i%numLocations));
			user.setCurrentRoad(null);
			user.setMoving(false);
			RoadMovement roadMovement = user.getCurrentRoadMovement();
			if(roadMovement!=null) {
				user.setCurrentRoadMovement(null);
				roadMovement.setRoad(null);
				roadMovement.setUser(null);
				session.delete(roadMovement);
			}
			
			//Add items in the map only for active users
			if(!activeUsers.contains(user)) {
				continue;
			}
			
			List<Item> targetItems = getItemsAtMidRange(NUM_GOAL_ITEMS_PER_PLAYER, itemPool, user.getCurrentLocation(), MIN_ITEM_DISTANCE, MAX_ITEM_DISTANCE, locations, MessageServiceImpl.locationDistanceMap);
	
			for(Item item : targetItems) {
				GameServiceImpl.createGoalItem(session, user, item.getItemType());
				itemPool.remove(item);
			}
		}
		
		//Create 1 item of each REPLENISHABLE itemType
		//Scattered across the world

		/*
		 * We don't need all these extra items where we're going. Shit should be refactored into its own function!
		 * 
		 * JV 11/04/11
		 */
		
		for(ItemType itemType : itemTypes) {
			if(itemType.isReplenishable()) {
				// get the initial location
				Location location = locations.get(randomizer.nextInt(numLocations));
				// if we have too many items here already find a different location
				
				GameServiceImpl.createItem(session, itemType, location);
			}
		}
		
		//Creating random extra items across the world			
		
		NUM_RANDOM_EXTRA_ITEMS = (int)(itemTypes.size() * (2 / 3));
		Collections.shuffle(itemTypes);
		for(int i = 0; i < NUM_RANDOM_EXTRA_ITEMS; i++) {
			// get the initial location
			Location location = locations.get(randomizer.nextInt(numLocations));
			// if we have too many items here already find a different location
			while (location.getItems().size() >= GameServiceImpl.MAXIMUM_ITEMS_PER_LOCATION) {
				location = locations.get(randomizer.nextInt(numLocations));
			}
			
			GameServiceImpl.createItem(session, itemTypes.get(i), location);
		}
	}
	
	public void test(ServerSettingsStructDTO serverSettings) throws GeoGameException {
		resetAllDB(serverSettings);
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Override
	public void resetAllDB(ServerSettingsStructDTO serverSettings) throws GeoGameException {
		
//	public void resetAllDB(UserDTO player, boolean rebuildUserNetwork, boolean rebuildRoadNetwork, boolean newItemAssignment, NetworkType networkType, Double graphDensity, Double minRoadTime, Double maxRoadTime) throws GeoGameException {
		//Boolean flag;
		//do {
		//System.out.println("Inside Reset DB");
		resetFlag=true;
		
		logger.info("Resetting Database");
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
//			TransactionLog tl = new TransactionLog("resetAllDB", "");
//			tl.startTime = System.currentTimeMillis();

			//Deletes transient catalogs
//			session.delete("FROM Message");
//			session.delete("FROM Action");
//			session.delete("FROM TradeOffer");
//			session.delete("FROM Item");
//			session.delete("FROM Combo");
			session.createQuery("DELETE FROM Action").executeUpdate();
			session.createSQLQuery("DELETE FROM syns_per_user").executeUpdate();
			
			List<Item> items = (List<Item>) session.createCriteria(Item.class).list();			
			
			for(Item item : items) {
				if(item.getOwner()!=null) {
					item.getOwner().removeFromInventory(item);
				}
				if(item.getLocation()!=null) {
					item.getLocation().removeItem(item);
				}
				session.delete(item);
			}
//			session.createSQLQuery("DELETE FROM item").executeUpdate();
			
			//Retrieving catalogs into memory
			List<User> users = (List<User>) session.createCriteria(User.class).list();
			List<ItemType> itemTypes = (List<ItemType>)session.createCriteria(ItemType.class).list();
			List<Location> locations = (List<Location>)session.createCriteria(Location.class).list();
			List<Road> roads = (List<Road>)session.createCriteria(Road.class).list();
			int numUsers = users.size();
			int numItemTypes = itemTypes.size();
			int numLocations = locations.size();
//			int numRoads = roads.size();

			
			List<User> activeUsers = new ArrayList<User>();
			for(User user : users) {
				if(MessageServiceImpl.userCacheMap.containsKey(user.getId())) {
					UserDTO userDTO = MessageServiceImpl.userCacheMap.get(user.getId());
					user.setLastRequest(userDTO.getLastRequest());
				}
//				AuthenticationUtil.updateLastRequestDate(session, user);
				Date curDate = new Date();
				if(user.seemsActive(curDate) && !user.isAdmin()) {
					activeUsers.add(user);
				}
			}
			
			logger.info("Found active users:");
			for(User activeUser : activeUsers) {
				logger.info(activeUser.getUsername());
			}

			Collections.shuffle(activeUsers);
			int numActiveUsers = activeUsers.size();

			if(MessageServiceImpl.locationDistanceMap==null || MessageServiceImpl.locationDistanceMap.isEmpty()) {
				MessageServiceImpl.locationDistanceMap = calculateLocationsMap(session, locations, roads);
			}
			
			Random randomizer = new Random();
			
			//Finding the "active" users
			if(serverSettings.isRebuildUserNetwork()) {
				GameServiceImpl.createUserNetwork(session, activeUsers, serverSettings.getNetworkType(), serverSettings.getGraphDensity());
			}
			
			itemDistributionFixed(session, users, activeUsers, items, locations, itemTypes);
			
			//Initializing road's danger levels
			double[] dangers = GameServiceImpl.dangerProbs;
			double[] dangerMaxes = GameServiceImpl.dangerMaxes;
			for(Road road : roads) {
				double d = randomizer.nextDouble();
				
				road.setDanger(0);
				double totalProb = 0;
				for(int i1=0;i1<3;i1++) {
					totalProb+=dangers[i1];
					if(d<=totalProb) {
						road.setDanger(randomizer.nextDouble()*dangerMaxes[i1]);
					}
				}
				if (serverSettings.isRebuildRoadNetwork()) {
					road.setTravelDuration((int)(serverSettings.getMinRoadTime() + randomizer.nextDouble()*(serverSettings.getMaxRoadTime() - serverSettings.getMinRoadTime())));
				}
//				session.update(road);
			}

			
			session.createQuery("DELETE FROM Message").executeUpdate();
			session.createQuery("DELETE FROM DeliveredMessage").executeUpdate();
			session.createQuery("DELETE FROM RoadMovement").executeUpdate();

//			session.createSQLQuery("DELETE FROM message").executeUpdate();
//			session.createSQLQuery("DELETE FROM message_graph").executeUpdate();
//			session.createSQLQuery("DELETE FROM action").executeUpdate();
//			session.createSQLQuery("DELETE FROM trade_offer").executeUpdate();
//			session.createSQLQuery("DELETE FROM item").executeUpdate();
//			session.createSQLQuery("DELETE FROM combo").executeUpdate();
//			session.createSQLQuery("DELETE FROM road_movement").executeUpdate();
			
			assignSynsToUsers(activeUsers, itemTypes);
			Action resetDbAction = new Action(null, ActionType.DB_RESET);
			session.save(resetDbAction);

			
			
			for (User user : activeUsers) {
				MessageServiceImpl.synMap.put(user.getId(), user.itemNameSynPairs);
				MessageServiceImpl.updateCacheMap(session, user, true);
				for (Entry<String, String> synset : user.itemNameSynPairs.entrySet()) {
					session.createSQLQuery("INSERT INTO syns_per_user (user_id, item_type_name, assigned_syn) VALUES (" + user.getId() + ", \"" + synset.getKey() + "\", \"" + synset.getValue() + "\")").executeUpdate();
				}
			}
			
			tx.commit();
		} catch (JDBCException ex) {
			tx.rollback();
			String error_sql = ex.getSQL();
			if (error_sql != null) {
				logger.error("The following SQL caused the exception: " + ex.getSQL());
			}
			logger.error(ex.getMessage(), ex);
			throw new DBException(ex);
		}
		
		catch(Exception ex) {
			tx.rollback();
			logger.error(ex.getMessage(), ex);
			throw new DBException(ex);
		}

		lastResetTime=new Date().getTime();
		GameServiceImpl.endTime = 0;
		GameServiceImpl.gameDuration = 0;
		GameServiceImpl.isGameStarted = false;
		GameServiceImpl.isGameFinished = false;
		resetFlag=false;

		MessageServiceImpl.clearAll();
		MessageServiceImpl.populateMessagesMaps();
		
		/*flag = check();
		if(flag)
			System.out.println("Check returned true");
		else
			System.out.println("Check returned false");
		}
		while(!flag);*/
		
	}
	
	
	public static boolean check() throws GeoGameException{
		System.out.println("inside check");
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		
		try {
			//Retrieving catalogs into memory
			@SuppressWarnings("unchecked")
			List<User> users = (List<User>) session.createCriteria(User.class).list();
			
			List<User> activeUsers = new ArrayList<User>();
			for(User user : users) {
				if(MessageServiceImpl.userCacheMap.containsKey(user.getId())) {
					UserDTO userDTO = MessageServiceImpl.userCacheMap.get(user.getId());
					user.setLastRequest(userDTO.getLastRequest());
				}
				Date curDate = new Date();
				if(user.seemsActive(curDate) && !user.isAdmin()) {
					activeUsers.add(user);
				}
			}
			System.out.println("Found active users:");
			for(User activeUser : activeUsers) {
				logger.info(activeUser.getUsername());
			}
			
			int numActiveUsers = activeUsers.size();
			System.out.println("Number of Active Users: " + numActiveUsers);
			
			for (User user : activeUsers) {
				@SuppressWarnings("rawtypes")
				List l =  session.createSQLQuery("SELECT user_id FROM syns_per_user WHERE user_id = " + user.getId()).list();
				//Object [] result = (Object [])query.uniqueResult();
				//System.out.println("result check : " + result[0]);
				if(l.isEmpty())
					throw new Exception();
				else
					System.out.println(l.toString());
			}
			
			tx.commit();
		} catch (JDBCException ex) {
			tx.rollback();
			String error_sql = ex.getSQL();
			if (error_sql != null) {
				logger.error("The following SQL caused the exception: " + ex.getSQL());
			}
			logger.error(ex.getMessage(), ex);
			System.out.println("check returned false");
			return false;
			//throw new DBException(ex);
		}
		
		catch(Exception ex) {
			System.out.println("Exception thrown - No entry in Database");
			tx.rollback();
			logger.error(ex.getMessage(), ex);
			System.out.println("check returned false");
			return false;
			//throw new DBException(ex);
		}
		System.out.println("check returned true");

		return true;
	}
	
	
//	public static Map<Pair<String,String>,Integer> calculateDistanceMap(List<User> users) {
//		Map<Pair<String,String>,Integer> distanceMap = new HashMap<Pair<String,String>,Integer>();
//		Stack<User> userStack = new Stack<User>();
//		for(User user : users) {
//			userStack.push(user);
//			searchDistance(user, user, 1, distanceMap, userStack);
//			userStack.pop();
//		}
//		return distanceMap;
//	}
	
	public void assignSynsToUsers(List<User> sortedUsers, List<ItemType> itemTypes) {
		//List<User> sortedUsers = (ArrayList<User>) userGraph.vertexSet();
		// sort the list by degree
		Collections.sort(sortedUsers, new UserDegreeComparator());
		Random r = new Random();
		
		for (ItemType itemType : itemTypes) {
			Set<User> assignedUsers = new HashSet<User>();
			List<String> availableSyns = itemType.getSynonymNames();
			availableSyns.add(0, itemType.getName());
			int maxColors = availableSyns.size();
			int currentColor = 0;
			
			sortedUsers.get(0).itemNameSynPairs.put(itemType.getName(), availableSyns.get(currentColor));
			while (assignedUsers.size() < sortedUsers.size()) {
				// get the user with the highest saturation index in the graph
				User maxSatUser = computeMaxSaturationUser(sortedUsers, assignedUsers, itemType);
				// keep track of which syns our neighbors use
				Set<String> synsUsed = new HashSet<String>();
				
				// if a neighbor has used a syn, add it to our list of syns used
				for (User neighbor : maxSatUser.getNeighbors()) {
					if (neighbor.itemNameSynPairs.containsKey(itemType.getName())) {
						synsUsed.add(neighbor.itemNameSynPairs.get(itemType.getName()));
					}
				}
				
				// get rid of any syns that are no longer available because our neighbors use them
				List<String> unusedSyns = new ArrayList<String>();
				
				for (String syn : availableSyns) {
					if (!synsUsed.contains(syn)) {
						unusedSyns.add(syn);
					}
				}
				// as long as at least one syn remains available, use the first one remaining
				if (!unusedSyns.isEmpty()) {
					maxSatUser.itemNameSynPairs.put(itemType.getName(), unusedSyns.get(0));
				}
				// if nothing is available, pick one at random
				else {
					maxSatUser.itemNameSynPairs.put(itemType.getName(), availableSyns.get(r.nextInt(maxColors)));
				}
				assignedUsers.add(maxSatUser);
			}
		}
		
		for (User user : sortedUsers) {
			logger.info("User " + user.getUsername() + " has been assigned synonyms: " + user.itemNameSynPairs);
		}
	}
	
	private User computeMaxSaturationUser(List<User> users, Set<User> assignedUsers, ItemType itemType) {
		
		int maxSat = 0;
		int maxSatIndex = 0;
		
		for (User user : users) {
			if (!assignedUsers.contains(user)) {
				List<User> neighbors = user.getNeighbors();
				Set<String> synsUsed = new HashSet<String>();
				
				for (User neighbor : neighbors) {
					if (neighbor.itemNameSynPairs.containsKey(itemType.getName())) {
						synsUsed.add(neighbor.itemNameSynPairs.get(itemType.getName()));
					}
				}
				
				int sat = synsUsed.size();
				if (sat > maxSat) {
					maxSat = sat;
					maxSatIndex = users.indexOf(user);
				}
			}
		}
		return users.get(maxSatIndex);
	}
	
	public static Map<Pair<String,String>,Integer> calculateDistanceMap(List<User> users) {
		Map<Pair<String,String>,Integer> distanceMap = new HashMap<Pair<String,String>,Integer>();
		Set<User> completeUsers = new HashSet<User>();
		Set<User> foundUsers = new HashSet<User>();
		Set<User> nextUsers = new HashSet<User>();
		Set<User> nextUsers2 = new HashSet<User>();
	//	Stack<User> userStack = new Stack<User>();
		for(User user : users) {
			int distance=0;
			nextUsers.add(user);
			
			while(!nextUsers.isEmpty()) {
				distance++;
				for(User nextUser : nextUsers) {
					for(User neighbor : nextUser.getNeighbors()) {
//						if(completeUsers.contains(neighbor) || foundUsers.contains(neighbor) || neighbor==user) {
//							continue;
//						}
//						Pair<String,String> pair = new Pair<String,String>(user.getUsername(), neighbor.getUsername());
//						distanceMap.put(pair, distance);
//						System.out.println(user.getUsername() + "->" + neighbor.getUsername() + " : " + distance);
//						foundUsers.add(neighbor);
//						nextUsers2.add(neighbor);

						if(foundUsers.contains(neighbor)) {
							continue;
						}
						if(!completeUsers.contains(neighbor) && neighbor!=user) {
							Pair<String,String> pair = new Pair<String,String>(user.getUsername(), neighbor.getUsername());
							distanceMap.put(pair, distance);
							System.out.println(user.getUsername() + "->" + neighbor.getUsername() + " : " + distance);
						}
						foundUsers.add(neighbor);
						nextUsers2.add(neighbor);
					}
				}
				Set<User> tempSet = nextUsers;
				nextUsers = nextUsers2;
				nextUsers2 = tempSet;
				nextUsers2.clear();
			}
	//		userStack.push(user);
	//		searchDistance(user, user, 1, distanceMap, userStack);
	//		userStack.pop();
			
			completeUsers.add(user);
			foundUsers.clear();
			nextUsers.clear();
			nextUsers.clear();
			nextUsers2.clear();
		}
		return Collections.unmodifiableMap(distanceMap);
	}

	public static Map<Pair<String,String>,Integer> calculateLocationsMap(Session session, List<Location> locations, List<Road> allRoads) {
		Map<Pair<String,String>,Integer> distanceMap = new HashMap<Pair<String,String>,Integer>();
		Set<Location> completeLocations = new HashSet<Location>();
		Set<Location> foundLocations = new HashSet<Location>();
		Set<Location> nextLocations = new HashSet<Location>();
		Set<Location> nextLocations2 = new HashSet<Location>();
	//	Stack<Location> userStack = new Stack<Location>();

		Map<Location, List<Location>> locationAdjacencyMap = new HashMap<Location,  List<Location>>();
		for(Road road : allRoads) {
			Location location1 = road.getLocation1();
			Location location2 = road.getLocation2();
			if(!locationAdjacencyMap.containsKey(location1)) {
				locationAdjacencyMap.put(location1, new ArrayList<Location>());
			}
			if(!locationAdjacencyMap.containsKey(location2)) {
				locationAdjacencyMap.put(location2, new ArrayList<Location>());
			}
			locationAdjacencyMap.get(location1).add(location2);
			locationAdjacencyMap.get(location2).add(location1);
		}

		for(Location location : locations) {
			int distance=0;
			nextLocations.add(location);
			
			while(!nextLocations.isEmpty()) {
				distance++;
				for(Location nextLocation : nextLocations) {
					for(Location neighbor : locationAdjacencyMap.get(nextLocation)) {
//					for(Location neighbor : GameServiceImpl.getAdjacentLocations(session, nextLocation, locationAdjacencyMap)) {
//						if(completeLocations.contains(neighbor) || foundLocations.contains(neighbor) || neighbor==location) {
//							continue;
//						}
						if(foundLocations.contains(neighbor)) {
							continue;
						}
						if(!completeLocations.contains(neighbor) && neighbor!=location) {
							Pair<String,String> pair = new Pair<String,String>(location.getName(), neighbor.getName());
							if(!distanceMap.containsKey(pair) || (distanceMap.containsKey(pair) && distance<distanceMap.get(pair))) {
								distanceMap.put(pair, distance);
								System.out.println(location.getName() + "->" + neighbor.getName() + " : " + distance);
							}
						}
						foundLocations.add(neighbor);
						nextLocations2.add(neighbor);
					}
				}
				Set<Location> tempSet = nextLocations;
				nextLocations = nextLocations2;
				nextLocations2 = tempSet;
				nextLocations2.clear();
			}
			completeLocations.add(location);
			foundLocations.clear();
			nextLocations.clear();
			nextLocations.clear();
			nextLocations2.clear();
		}
		return Collections.unmodifiableMap(distanceMap);
	}

	public static void searchDistance(User user, User positionUser, int distance, Map<Pair<String,String>,Integer> distanceMap, Stack<User> userStack) {
		for(User neighbor : positionUser.getNeighbors()) {
			if(userStack.contains(neighbor)) {
				continue;
			}
			
//			if(user.getUsername().equals(neighbor.getUsername())) {
//				continue;
//			}
			Pair<String,String> pair = new Pair<String,String>(user.getUsername(), neighbor.getUsername());
			
			if(!distanceMap.containsKey(pair)) {
				System.out.println(printStack(userStack, neighbor) + " : " + distance);
				distanceMap.put(pair, distance);
			} else if(distance<distanceMap.get(pair)) {
				System.out.println(printStack(userStack, neighbor) + " : " + distance);
				distanceMap.put(pair,distance);
			}
			userStack.push(neighbor);
			searchDistance(user,neighbor,distance+1,distanceMap,userStack);
			userStack.pop();
		}
	}
	
	private static String printStack(Stack<User> userStack, User finalUser) {
		StringBuilder sb = new StringBuilder();
		
		for(User user : userStack) {
			sb.append(user.getUsername() + "->");
		}
		sb.append(finalUser.getUsername());
		return sb.toString();
	}
	
	public static List<Item> findItemsAtDistances(List<Item> itemPool, Location sourceLocation, int minDistance, int maxDistance, List<Location> allLocations, Map<Pair<String, String>, Integer> locationMap) {
		
		List<ItemType> closeItemTypes = new ArrayList<ItemType>();
		List<Location> closeLocations = locationsAtDistances(sourceLocation, 0, minDistance-1, allLocations, locationMap);
		for(Location location : closeLocations) {
			for(Item item : location.getItems()) {
				closeItemTypes.add(item.getItemType());
			}
		}

		List<Item> midItems = new ArrayList<Item>();
		List<Location> midLocations = locationsAtDistances(sourceLocation, minDistance, maxDistance, allLocations, locationMap);
		for(Location location : midLocations) {
			for(Item item : location.getItems()) {
				if(itemPool.contains(item)) {
					midItems.add(item);
				}
			}
		}

		midItems.removeAll(closeItemTypes);
		return midItems;
	}
	
	public static List<Location> locationsAtDistances(Location sourceLocation, int minDistance, int maxDistance, List<Location> allLocations, Map<Pair<String, String>, Integer> locationMap) {
		List<Location> resultLocations = new ArrayList<Location>();
		if(minDistance <= 0) {
			resultLocations.add(sourceLocation);
		}
		
		for(Location location: allLocations) {
			Pair<String, String> locationPair = new Pair<String, String>(sourceLocation.getName(), location.getName());
			if(!locationMap.containsKey(locationPair)) {
				continue;
			}
			int distance = locationMap.get(locationPair);
			if(distance>=minDistance && distance<=maxDistance) {
				resultLocations.add(location);
			}
		}
		
		return resultLocations;
	}
	
	private static List<Item> getItemsAtMidRange(int numGoalItems, List<Item> itemPool, Location sourceLocation, int minDistance, int maxDistance, List<Location> allLocations, Map<Pair<String, String>, Integer> locationMap) {
		List<Item> distanceItems = findItemsAtDistances(itemPool, sourceLocation, MIN_ITEM_DISTANCE, MAX_ITEM_DISTANCE, allLocations, MessageServiceImpl.locationDistanceMap);
		if(distanceItems.size()<numGoalItems) {
			distanceItems = findItemsAtDistances(itemPool, sourceLocation, 0, Integer.MAX_VALUE, allLocations, MessageServiceImpl.locationDistanceMap);
		}
		if(distanceItems.size()<numGoalItems) {
			distanceItems = new ArrayList<Item>(itemPool);
		}

		Collections.shuffle(distanceItems);
		distanceItems = distanceItems.subList(0, numGoalItems);
		
		return distanceItems;
	}
}