/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.maps.client.geom.LatLng;

import edu.cmu.cs.cimds.geogame.client.model.db.Item;
import edu.cmu.cs.cimds.geogame.client.model.db.ItemType;
import edu.cmu.cs.cimds.geogame.client.model.db.User;
import edu.cmu.cs.cimds.geogame.client.util.ItemSynMap;
import edu.cmu.cs.cimds.geogame.client.util.Pair;

/**
 * 
 * @author ajuarez
 */

public class UserDTO implements Serializable {

	private static final long serialVersionUID = -1598104533505599878L;

	private long id;
	private String username;
	private String firstName;
	private String lastName;
	private int score;
	private boolean admin;
	// private List<String> neighbors = new ArrayList<String>();
	private List<UserDTO> neighbors = new ArrayList<UserDTO>();
	private String authCode;
	private String iconFilename;
	
	private boolean AMTVisitor = false;
	private String AMTCode = "";
	
	private AcceptanceFormDTO currentAcceptanceFormDTO;
	private ItemTypeDTO currentGoalItemType;
	
	private LocationDTO currentLocation;
	private RoadDTO currentRoad;
	private boolean forward;
	
	private List<LocationDTO> accessibleLocations = new ArrayList<LocationDTO>();
	private List<ItemDTO> inventory = new ArrayList<ItemDTO>();
	private List<ItemTypeDTO> itemsToCollect = new ArrayList<ItemTypeDTO>();

	private boolean loggedIn;
	private boolean moving;
	
	
	// private GGLatLng mapPosition;
	private double latitude;
	private double longitude;

//TODO:RM
	private RoadMovementDTO currentRoadMovement;
	private Date lastRequest = new Date(0);
	private boolean seemsActive;
	private HashMap<String, String> itemNameSynPairs;
	
	private static final long USER_ACTIVE_TIMEOUT = 10000;
	
	public UserDTO() {}

	public UserDTO(User user, boolean includeNeighbors, Map<Long, UserDTO> userCacheMap) {
		this.updateWithUser(user, includeNeighbors, userCacheMap);

//		if (includeNeighbors) {
//			for (User neighbor : user.getNeighbors()) {
//				UserDTO neighborDTO;
//				if(userCacheMap.containsKey(neighbor.getId())) {
//					neighborDTO = userCacheMap.get(neighbor.getId());
//				} else {
//					neighborDTO = new UserDTO(user, false, userCacheMap);
//					userCacheMap.put(neighbor.getId(), neighborDTO);
//				}
//				this.neighbors.add(neighborDTO);
//			}
//		}
	}
	
	public void clear() {
		this.inventory.clear();
		this.itemsToCollect.clear();
//		this.neighbors.clear();
		this.id=0;
		this.username="";
		this.score=0;
		this.admin=false;
		this.latitude=0;
		this.longitude=0;
		this.moving=false;
		this.forward=false;
		this.authCode="";
		this.currentLocation=null;
		this.currentRoad=null;
		this.currentRoadMovement=null;
	}
	
	public void updateWithUser(User user, boolean includeNeighbors, Map<Long, UserDTO> userCacheMap) {
		this.clear();
		
		this.id = user.getId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.score = user.getScore();
		this.admin = user.isAdmin();
		this.iconFilename = user.getIconFilename();
		this.loggedIn = user.isLoggedIn();
		this.moving = user.isMoving();
		this.lastRequest = user.getLastRequest();
		this.authCode = user.getAuthCode();
		this.AMTVisitor = user.isAMTVisitor();
		this.AMTCode = user.getAMTCode();
		
		if(this.itemNameSynPairs != null)
			this.setItemNameSynPairs(user.itemNameSynPairs);
		
		//System.out.println(this.getItemNameSynPairs());
		
		this.updateSeemsActive();

		this.latitude = user.getLatitude();
		this.longitude = user.getLongitude();
		
		if (user.getCurrentRoadMovement() != null) {
			this.setCurrentRoadMovement(new RoadMovementDTO(user.getCurrentRoadMovement()));
		}
		
		if(user.getCurrentRoad()!=null) {
			this.currentRoad = new RoadDTO(user.getCurrentRoad());
		}
		if(user.getCurrentLocation()!=null) {
			this.currentLocation = new LocationDTO(user.getCurrentLocation());
		}
		this.updatePosition();
		
		this.forward = user.getForward();
		for (Item item : user.getInventory()) {
			this.inventory.add(new ItemDTO(item));
		}
		for (ItemType itemType : user.getItemsToCollect()) {
			this.itemsToCollect.add(new ItemTypeDTO(itemType));
		}
	}
	
	public void updateWithUser2(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.score = user.getScore();
		this.admin = user.isAdmin();
		this.iconFilename = user.getIconFilename();
		this.moving = user.isMoving();
		this.lastRequest = user.getLastRequest();
		this.seemsActive = (new Date().getTime()-this.lastRequest.getTime()) <= USER_ACTIVE_TIMEOUT;

		this.latitude = user.getLatitude();
		this.longitude = user.getLongitude();
		
		if (user.getCurrentRoadMovement() != null) {
			this.setCurrentRoadMovement(new RoadMovementDTO(user
					.getCurrentRoadMovement()));
		}
		
		if(user.getCurrentRoad()!=null) {
			this.currentRoad = new RoadDTO(user.getCurrentRoad());
		}
		if(user.getCurrentLocation()!=null) {
			this.currentLocation = new LocationDTO(user.getCurrentLocation());
		}
		this.updatePosition();
		
		this.forward = user.getForward();
		this.inventory.clear();
		for (Item item : user.getInventory()) {
			this.inventory.add(new ItemDTO(item));
		}
		this.getItemsToCollect().clear();
		for (ItemType itemType : user.getItemsToCollect()) {
			this.itemsToCollect.add(new ItemTypeDTO(itemType));
		}
	}

	public void updateSeemsActive() {
		long current_time = new Date().getTime();
		long last_request = this.lastRequest.getTime();
		boolean time_flag = (current_time - last_request <= USER_ACTIVE_TIMEOUT);
		this.seemsActive = this.loggedIn && (new Date().getTime()-this.lastRequest.getTime()) <= USER_ACTIVE_TIMEOUT;
	}
	
	public void updatePosition() {
//		if(this.moving && this.currentLocation==null) {
//			return;
//		}
		if (!this.moving && this.currentLocation!=null) {
//			if(this.username.equals("TestUser0")) {
//				System.out.println("TestUser0 was not moving");
//			}
			this.setLatitude(this.currentLocation.getLatitude());
			this.setLongitude(this.currentLocation.getLongitude());
		} else {
//			if(this.username.equals("TestUser0")) {
//				System.out.println("TestUser0 was moving");
//			}
//			this.getCurrentRoadMovement().update();

			if (this.currentRoadMovement != null) {
				GGLatLng roadPosition = getRoadPosition(this.currentRoadMovement);
				if(roadPosition.getLatitude()!=this.getLatitude()  && roadPosition.getLatitude()-this.getLatitude()>50) {
					System.out.println("Changing position - " + this.getUsername() + "... " + (roadPosition.getLatitude()-this.getLatitude()));
				}	
				this.setLatitude(roadPosition.getLatitude());
				this.setLongitude(roadPosition.getLongitude());
			}
		}
	}

	public double getLatitude() { return latitude; }
	public void setLatitude(double latitude) { this.latitude = latitude; }

	public double getLongitude() { return longitude; }
	public void setLongitude(double longitude) { this.longitude = longitude; }

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public String getAuthCode() { return authCode; }
	public void setAuthCode(String authCode) { this.authCode = authCode; }

	public String getIconFilename() { return iconFilename; }
	public void setIconFilename(String iconFilename) { this.iconFilename = iconFilename; }

	public AcceptanceFormDTO getCurrentAcceptanceFormDTO() { return currentAcceptanceFormDTO; }
	public void setCurrentAcceptanceFormDTO(AcceptanceFormDTO currentAcceptanceFormDTO) { this.currentAcceptanceFormDTO = currentAcceptanceFormDTO; }

	public ItemTypeDTO getCurrentGoalItemType() {  return currentGoalItemType; }
	public void setCurrentGoalItemType(ItemTypeDTO currentGoalItemType) { this.currentGoalItemType = currentGoalItemType; }
	public void setCurrentGoalItemType(ItemType currentGoalItemType) { this.currentGoalItemType = new ItemTypeDTO(currentGoalItemType); }

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }

	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }

	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }

	public int getScore() { return score; }
	public void setScore(int score) { this.score = score; }

	public boolean isAdmin() { return admin; }
	public void setAdmin(boolean admin) { this.admin = admin; }

	// public List<String> getNeighbors() { return neighbors; }
	// public void setNeighbors(List<String> neighbors) { this.neighbors =
	// neighbors; }
	public List<UserDTO> getNeighbors() { return neighbors; }
	public void setNeighbors(List<UserDTO> neighbors) { this.neighbors = neighbors; }

	public LocationDTO getCurrentLocation() { return currentLocation; }
	public void setCurrentLocation(LocationDTO currentLocation) { this.currentLocation = currentLocation; }

	public RoadDTO getCurrentRoad() { return currentRoad; }
	public void setCurrentRoad(RoadDTO currentRoad) { this.currentRoad = currentRoad; }

	public boolean getForward() { return forward; }
	public void setForward(boolean forward) { this.forward = forward; }

	public List<ItemDTO> getInventory() { return inventory; }
	public void setInventory(List<ItemDTO> inventory) { this.inventory = inventory; }
	public void addInventory(ItemDTO item) { this.inventory.add(item); }
	
	public List<ItemTypeDTO> getItemsToCollect() { return itemsToCollect; }
	public void setItemsToCollect(List<ItemTypeDTO> itemsToCollect) { this.itemsToCollect = itemsToCollect; }
	public void addItemToCollect(ItemTypeDTO itemToCollect) { this.itemsToCollect.add(itemToCollect); }

	//TODO:RM
	public RoadMovementDTO getCurrentRoadMovement() { return currentRoadMovement; }
	public void setCurrentRoadMovement(RoadMovementDTO currentRoadMovement) {
		this.currentRoadMovement = currentRoadMovement;
	}

	public boolean isLoggedIn() { return loggedIn; }
	public void setLoggedIn(boolean loggedIn) { this.loggedIn = loggedIn; }

	public boolean isMoving() { return moving; }
	public void setMoving(boolean moving) { this.moving = moving; }

	public Date getLastRequest() { return lastRequest; }
	public void setLastRequest(Date lastRequest) { this.lastRequest = lastRequest; }

	public boolean getAMTVisitor() { return AMTVisitor; }
	public void setAMTVisitor(boolean AMTVisitor) { this.AMTVisitor = AMTVisitor; }
	
	public String getAMTCode() { return AMTCode; }
	public void setAMTCode(String AMTCode) { this.AMTCode = AMTCode; }
	
	// public GGLatLng getMapPosition() { return mapPosition; }
	// public void setMapPosition(GGLatLng mapPosition) { this.mapPosition =
	// mapPosition; }

	 public LatLng getPosition() {
		 return LatLng.newInstance(this.latitude, this.longitude);
	 }
	 public void setPosition(LatLng position) {
		 this.latitude = position.getLatitude();
		 this.longitude = position.getLongitude();
	 }
	//
	// public double getLatitude() { return latitude; }
	// public void setLatitude(double latitude) { this.latitude = latitude; }
	//
	// public double getLongitude() { return longitude; }
	// public void setLongitude(double longitude) { this.longitude = longitude;
	// }

	public static class GGLatLng implements Serializable {

		private static final long serialVersionUID = 3547512160939799474L;

		private double latitude;
		private double longitude;

		public GGLatLng(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public GGLatLng(LocationDTO location) {
			this.latitude = location.getLatitude();
			this.longitude = location.getLongitude();
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}
	}

	public static GGLatLng getRoadPosition(RoadMovementDTO roadMovement) {
		
		if (roadMovement == null) {
			System.out.println("Holy shit, what happened?!");
		}
		
		try {
		
		long timeLeft = roadMovement.getTimeLeft();
		long timeElapsed = roadMovement.getDuration() - timeLeft;
		
		GGLatLng roadPosition;
		if (timeLeft <= 0) {
			// Warning, should never be here... right?
			if (roadMovement.isForward()) {
				roadPosition = new GGLatLng(roadMovement.getRoad().getLocation2());
			} else {
				roadPosition = new GGLatLng(roadMovement.getRoad().getLocation1());
			}
			return roadPosition;
		} else {
			List<GGLatLng> roadPoints = stringToRoadPoints(roadMovement.getRoad().getRoadPointsString());
			roadPoints.add(0, new GGLatLng(roadMovement.getRoad().getLocation1()));
			roadPoints.add(new GGLatLng(roadMovement.getRoad().getLocation2()));

			List<Double> distances = new ArrayList<Double>();
			double totalDistance = 0;
			for (int i = 0; i < roadPoints.size() - 1; i++) {
				GGLatLng roadPoint1 = roadPoints.get(i);
				GGLatLng roadPoint2 = roadPoints.get(i + 1);
				double latDiff = roadPoint2.getLatitude() - roadPoint1.getLatitude();
				double longDiff = roadPoint2.getLongitude() - roadPoint1.getLongitude();

				distances.add(Math.sqrt((latDiff * latDiff) + (longDiff * longDiff)));
				totalDistance += distances.get(i);
			}
			totalDistance *= ((double)timeElapsed / roadMovement.getDuration());
			if (!roadMovement.isForward()) {
				Collections.reverse(roadPoints);
				Collections.reverse(distances);
			}
			double distance = 0;
			for (int i = 0; i < distances.size(); i++) {
				if (distance + distances.get(i) >= totalDistance) {
					double miniRatio = (totalDistance - distance) / distances.get(i);

					GGLatLng roadPoint1 = roadPoints.get(i);
					GGLatLng roadPoint2 = roadPoints.get(i + 1);
					double latDiff = roadPoint2.getLatitude() - roadPoint1.getLatitude();
					double longDiff = roadPoint2.getLongitude() - roadPoint1.getLongitude();

					double newLat = roadPoint1.getLatitude() + (miniRatio * latDiff);
					double newLong = roadPoint1.getLongitude() + (miniRatio * longDiff);

					roadPosition = new GGLatLng(newLat, newLong);
					roadMovement.setSegment(i);
					roadMovement.setSpeed(totalDistance / (roadMovement.getDuration() / 1000));
					roadMovement.setSlope(latDiff / longDiff);
					return roadPosition;
				}
				distance += distances.get(i);
			}
			// It should never come here
			return null;
		}
		
		} catch (Exception ex) {
			System.out.println ("Caught an exception in GetRoadMovement");
		}
		
		return null;
	}

	public static String roadPointsToString(List<GGLatLng> roadPoints) {
		StringBuilder roadPointsString = new StringBuilder();
		for (GGLatLng roadPoint : roadPoints) {
			roadPointsString.append(roadPoint.getLatitude() + ","
					+ roadPoint.getLongitude() + ";");
		}
		return roadPointsString.toString();
	}

	public static List<GGLatLng> stringToRoadPoints(String roadPointsString) {
		List<GGLatLng> roadPoints = new ArrayList<GGLatLng>();
		if (roadPointsString == null || "".equals(roadPointsString)) {
			return roadPoints;
		}
		String[] roadPointsStrings = roadPointsString.split(";");

		for (String roadPointString : roadPointsStrings) {
			String[] coordStrings = roadPointString.split(",");
			roadPoints.add(new GGLatLng(Double.valueOf(coordStrings[0]), Double
					.valueOf(coordStrings[1])));
		}
		return roadPoints;
	}

	public LocationDTO getSource() {
		if(this.currentRoadMovement!=null) {
			return this.currentRoadMovement.getSource();
		} else {
			return null;
		}
	}

	public LocationDTO getDestination() {
		if(this.currentRoadMovement!=null) {
			return this.currentRoadMovement.getDestination();
		} else {
			return null;
		}
	}

	public boolean isSeemsActive() { return seemsActive; }
	public void setSeemsActive(boolean seemsActive) { this.seemsActive = seemsActive; }
	
	@Override
	public int hashCode() {
		return this.username.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if(o.getClass()!=UserDTO.class && o.getClass()!=String.class) {
			return false;
		}
		if(o.getClass()==String.class) {
			String s = (String)o;
			return this.username.equals(s);
		}
		
		UserDTO otherUser = (UserDTO)o;
		return this.username.equals(otherUser.getUsername());
	}

	public void setItemNameSynPairs(HashMap<String, String> itemNameSynPairs) {
		this.itemNameSynPairs = itemNameSynPairs;
	}

	public HashMap<String, String> getItemNameSynPairs() {
		return itemNameSynPairs;
	}
}