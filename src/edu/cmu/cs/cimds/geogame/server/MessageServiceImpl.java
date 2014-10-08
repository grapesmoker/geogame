package edu.cmu.cs.cimds.geogame.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.cmu.cs.cimds.geogame.client.exception.AuthorizationException;
import edu.cmu.cs.cimds.geogame.client.exception.DBException;
import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;
import edu.cmu.cs.cimds.geogame.client.exception.InvalidCommandException;
import edu.cmu.cs.cimds.geogame.client.exception.InvalidMessageException;
import edu.cmu.cs.cimds.geogame.client.model.db.Action;
import edu.cmu.cs.cimds.geogame.client.model.db.DeliveredMessage;
import edu.cmu.cs.cimds.geogame.client.model.db.GeoGameCommand;
import edu.cmu.cs.cimds.geogame.client.model.db.Item;
import edu.cmu.cs.cimds.geogame.client.model.db.ItemType;
import edu.cmu.cs.cimds.geogame.client.model.db.Location;
import edu.cmu.cs.cimds.geogame.client.model.db.Message;
import edu.cmu.cs.cimds.geogame.client.model.db.Road;
import edu.cmu.cs.cimds.geogame.client.model.db.RoadMovement;
import edu.cmu.cs.cimds.geogame.client.model.db.User;
import edu.cmu.cs.cimds.geogame.client.model.dto.CommStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.GeoGameCommandDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.MessageDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ScoreMessage;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.model.enums.ActionType;
import edu.cmu.cs.cimds.geogame.client.services.MessageService;
import edu.cmu.cs.cimds.geogame.client.util.Pair;

public class MessageServiceImpl extends RemoteServiceServlet implements MessageService {

	private static final long serialVersionUID = 4235275277384814432L;

	private static Logger logger = Logger.getLogger(MessageServiceImpl.class);
	
	/**************** MESSAGE SERVICE IMPL ***************/
	
	private static final long TIME_AFTER_RESET_LOGOFF = 3000;
	
	public static Map<Long, UserDTO> userCacheMap = Collections.synchronizedMap(new HashMap<Long, UserDTO>());
	public static ConcurrentHashMap <Long, ConcurrentLinkedQueue<MessageDTO>> messagesMap = new ConcurrentHashMap <Long, ConcurrentLinkedQueue<MessageDTO>>();
	public static Map<Long, Boolean> hasMessagesMap = Collections.synchronizedMap(new HashMap<Long,Boolean>());
	public static Map<Long, List<ScoreMessage>> scoreMessagesMap = Collections.synchronizedMap(new HashMap<Long, List<ScoreMessage>>());
	public static ConcurrentHashMap<Long, ConcurrentLinkedQueue<GeoGameCommandDTO>> commandMap = new ConcurrentHashMap<Long, ConcurrentLinkedQueue<GeoGameCommandDTO>>();
	public static Map<Long, HashMap<String, String>> synMap = Collections.synchronizedMap(new HashMap<Long, HashMap<String, String>>());
	
	static {
		populateMessagesMaps();
	}

	//Method placed to force initialization of MessageServiceImpl, to centralize execution of populateMessageMaps().
	public static void initialize() {
		userCacheMap.size();
	}
	
	public static void clearAll() {
		userCacheMap.clear();
		logger.info("userCacheMap cleared");
		
		for (Map.Entry<Long, Boolean> entry : hasMessagesMap.entrySet()) {
			hasMessagesMap.put(entry.getKey(), false);
		}
		
		for (Map.Entry<Long, List<ScoreMessage>> entry : scoreMessagesMap.entrySet()) {
			entry.getValue().clear();
		}
		
		for (Map.Entry<Long, ConcurrentLinkedQueue<MessageDTO>> entry : messagesMap.entrySet()) {
			entry.getValue().clear();
		}
		
		for (Map.Entry<Long, ConcurrentLinkedQueue<GeoGameCommandDTO>> entry : commandMap.entrySet()) {
			entry.getValue().clear();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void populateMessagesMaps() {
		logger.info("populating hasMessagesMap");
		synchronized(userCacheMap) {
			if(!userCacheMap.isEmpty()) {
				return;
			}

			Session session = PersistenceManager.getSession();
			Transaction tx = session.beginTransaction();
			try {
				List<User> allUsers = (List<User>)session.createCriteria(User.class).list();
//				synchronized(userCacheMap) {
					for(User user : allUsers) {
						hasMessagesMap.put(user.getId(), Boolean.TRUE);
						scoreMessagesMap.put(user.getId(), new ArrayList<ScoreMessage>());
						messagesMap.put(user.getId(), new ConcurrentLinkedQueue<MessageDTO>());
						commandMap.put(user.getId(), new ConcurrentLinkedQueue<GeoGameCommandDTO>());
//						updateCacheMap(session, user, true);
					}
//					linkUserNeighbors(allUsers);
					
					if(MessageServiceImpl.locationDistanceMap==null || MessageServiceImpl.locationDistanceMap.isEmpty()) {
						MessageServiceImpl.locationDistanceMap = DevServiceImpl.calculateLocationsMap(session, (List<Location>)session.createCriteria(Location.class).list(), (List<Road>)session.createCriteria(Road.class).list());
					}
	
					tx.commit();
//				}
			} catch(HibernateException ex) {
				tx.rollback();
				logger.error(ex.getMessage(), ex);
	//			throw new DBException(ex);
			}
		}
	}

	//	public static Map<Long,UserDTO> userCacheMap;
	public static Map<Pair<String,String>,Integer> locationDistanceMap;
	
	@Override
	public void sendAdminCommand(UserDTO admin, GeoGameCommandDTO command) throws GeoGameException {
		// right now we're sending the command to all players.
		logger.warn("Admin command: \"" + command.getCommandType().toString() + "\"");
		if(!(command.getSender() == admin.getId())) {
			// If enters here, then username does not match the message's sender.
			// Now throws an exception
			InvalidCommandException unmatchedUsers = new InvalidCommandException("Reported and actual senders do not match!\n " +
					"Reported: " + admin.getUsername() + "\n Actual: " + String.valueOf(command.getSender()));
			throw(unmatchedUsers);
		}
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			GeoGameCommand newCommand = new GeoGameCommand(command);
			
			// disseminate command to all logged-in users
			@SuppressWarnings("unchecked")
			List<User> receivers = (List<User>)session.createCriteria(User.class)
				.add(Restrictions.eq("loggedIn", true))
				.list();
			
			for (User receiver : receivers) {
				commandMap.get(receiver.getId()).add(command);
			}
			
			session.save(newCommand);
			tx.commit();
		}
		catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage(), ex);
			throw new DBException(ex);
		} catch(NullPointerException ex) {
			tx.rollback();
			logger.error(ex.getMessage(), ex);
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
			throw new DBException(ex);
		} 
	}
	
	@Override
	public GeoGameCommandDTO getAdminCommand(UserDTO player) {
		/*
		 * NOTE regarding this function:
		 * 
		 * You should probably not use this function, at least not yet. I am only putting it here for the sake of completeness
		 * in case we might want it later on. All it does is fetch the command from the user's queue. However, right now, there's
		 * no good reason why the same functionality cannot be handled through getMessages and the concomitant passing of the
		 * CommStruct. Ok, there's one good reason: it's a bit sloppy because it couples the command logic to the message logic.
		 * So maybe the right thing to do is to separate them, which is what this function would be for, but right now we're not.
		 * 
		 * JV 11/20/11
		 */
		
		return commandMap.get(player.getId()).poll();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void sendAdminMessage(UserDTO admin, MessageDTO newMessage) throws GeoGameException {
		logger.warn("Admin sends: \"" + newMessage.getContent() + "\"");
		if(!newMessage.getSender().equals(admin.getUsername())) {
			// If enters here, then username does not match the message's sender.
			// Now throws an exception
			InvalidMessageException unmatchedUsers = new InvalidMessageException("Reported and actual senders do not match!\n " +
					"Reported: " + admin.getUsername() + "\n Actual: " + newMessage.getSender());
			throw(unmatchedUsers);
		}
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			User user = (User)session.createCriteria(User.class)
			.add(Restrictions.eq("username", admin.getUsername()))
			.uniqueResult();
			
			Message message = new Message(newMessage);
			// this message is special so it's highlighted
			String adminMessage = "<font color=red><b>" + message.getContent() + "</b></font>"; 
			message.setContent(adminMessage);
			session.save(message);
			message.setSender(user);
			
			// disseminate message to all logged-in users
			List<User> receivers = (List<User>)session.createCriteria(User.class)
				.add(Restrictions.eq("loggedIn", true))
				.list();
			
			for (User receiver : receivers) {
				DeliveredMessage newDeliveredMessage = new DeliveredMessage();
				newDeliveredMessage.setMessage(message);
				newDeliveredMessage.setReceiver(receiver);
				session.save(newDeliveredMessage);
				hasMessagesMap.put(receiver.getId(), Boolean.TRUE);
			}
			tx.commit();
		}
		catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage(), ex);
			throw new DBException(ex);
		} catch(NullPointerException ex) {
			tx.rollback();
			logger.error(ex.getMessage(), ex);
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
			throw new DBException(ex);
		} 
	}
	
	public void sendMessageNew(UserDTO player, MessageDTO newMessage) throws GeoGameException {
		logger.warn("Player " + player.getUsername() + " sends: \"" + newMessage.getContent() + "\"");
		// Check to make sure this is the right sender.
		if (!newMessage.getSender().equals(player.getUsername())) {
			InvalidMessageException unmatchedUsers = new InvalidMessageException("Reported and actual senders do not match!\n " +
					"Reported: " + player.getUsername() + "\n Actual: " + newMessage.getSender());
			throw(unmatchedUsers);
		}
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		
		try {
			User user = AuthenticationUtil.authenticatePlayer(session, player);

			UserDTO cachePlayer = MessageServiceImpl.userCacheMap.get(player.getId());
			Message message = new Message(newMessage);
			session.save(message);
			
			message.setSender(user);

			DeliveredMessage autoDeliveredMessage = new DeliveredMessage();
			autoDeliveredMessage.setMessage(message);
			autoDeliveredMessage.setReceiver(user);
			autoDeliveredMessage.setTimeReceived(new Date());
			session.save(autoDeliveredMessage);
			

			List<User> receivers;
			List<String> receiverNames = new ArrayList<String>();
			
			if (newMessage.isBroadcast()) {
				receivers = user.getNeighbors();			
			}
			else {
				receivers = (List<User>)session.createCriteria(User.class)
					.add(Restrictions.in("username",newMessage.getReceivers()))
					.list();
			}

			for (User receiver : receivers) {
				receiverNames.add(receiver.getUsername());
			}
			// receive your own messages
			hasMessagesMap.put(user.getId(), Boolean.TRUE);
			messagesMap.get(user.getId()).add(new MessageDTO (newMessage));
			receiverNames.add(user.getUsername());
			
			for(User receiver : receivers) {
				DeliveredMessage newDeliveredMessage = new DeliveredMessage();
				newDeliveredMessage.setMessage(message);
				newDeliveredMessage.setReceiver(receiver);
				newDeliveredMessage.setTimeReceived(new Date());
				session.save(newDeliveredMessage);
				
				hasMessagesMap.put(receiver.getId(), Boolean.TRUE);
				// put the messages in the message map
				newMessage.setReceivers(receiverNames);
				synchronized(messagesMap.get(receiver.getId())) {
					messagesMap.get(receiver.getId()).add(new MessageDTO(newMessage));
				}
			}
			
			tx.commit();
		} catch (JDBCException ex) {
			tx.rollback();
			logger.error(player.getUsername() + ": SEND MESSAGE FAILED!");
			logger.error(ex.getMessage(), ex);
			String error_sql = ex.getSQL();
			if (error_sql != null) {
				logger.error("The following SQL caused the exception: " + ex.getSQL());
			}
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(player.getUsername() + ": SEND MESSAGE FAILED!");
			logger.error(ex.getMessage(), ex);
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
			throw new DBException(ex);
		} catch(NullPointerException ex) {
			tx.rollback();
			logger.error(player.getUsername() + ": SEND MESSAGE FAILED!");
			logger.error(ex.getMessage(), ex);
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
			throw new DBException(ex);
		}
	}
	
	public CommStruct getMessagesNew(UserDTO player, Date minTimestamp, boolean needsDBRefresh) throws GeoGameException {
		
		if(DevServiceImpl.resetFlag) {
			throw new AuthorizationException(null, "Game is resetting");
		}
		if(player.getUsername().equals("TestUser0")) {
			logger.trace("Inside MessageServiceImpl");
		}

		CommStruct commStruct = new CommStruct();
		synchronized (commStruct) {
		if(new Date().getTime()-DevServiceImpl.lastResetTime<TIME_AFTER_RESET_LOGOFF) {
			commStruct.logOffPlayer = true;
//			tl.endTime = System.currentTimeMillis();
//			GameServiceImpl.transactionLog.add(tl);

			commStruct.timestamp = new Date();
			commStruct.gameStarted = GameServiceImpl.isGameStarted;
			commStruct.gameFinished = GameServiceImpl.isGameFinished;
			commStruct.timeRemaining = GameServiceImpl.endTime - commStruct.timestamp.getTime();
			commStruct.gameDuration = GameServiceImpl.gameDuration;
			return commStruct;
		}
		
		if(!AuthenticationUtil.authenticatePlayer(player)) {
//			tl.endTime = System.currentTimeMillis();
//			GameServiceImpl.transactionLog.add(tl);

			commStruct.timestamp = new Date();
			commStruct.gameStarted = GameServiceImpl.isGameStarted;
			commStruct.gameFinished = GameServiceImpl.isGameFinished;
			commStruct.timeRemaining = GameServiceImpl.endTime - commStruct.timestamp.getTime();
			commStruct.gameDuration = GameServiceImpl.gameDuration;
			return commStruct;
		}
		
		// distribute any score messages
		if (scoreMessagesMap.get(player.getId()) != null) {
			commStruct.scoreMessages = new ArrayList<ScoreMessage>(scoreMessagesMap.get(player.getId()));
		} else {
			commStruct.scoreMessages = new ArrayList<ScoreMessage>();
		}
		// clear the score messages map
		if (!commStruct.scoreMessages.isEmpty()) {
			scoreMessagesMap.get(player.getId()).clear();
		}
		//distribute commands
		if (commandMap.get(player.getId()) != null) {
			commStruct.commands = new ArrayList<GeoGameCommandDTO>(commandMap.get(player.getId()));
		}
		else {
			commStruct.commands = new ArrayList<GeoGameCommandDTO>();
		}
		//clear the command map
		if (!commStruct.commands.isEmpty()) {
			commandMap.get(player.getId()).clear();
		}
		
		// distribute regular messages
		if (messagesMap.containsValue(player.getId())) {
			commStruct.messages = new ArrayList<MessageDTO>(messagesMap.get(player.getId()));
			messagesMap.get(player.getId()).clear();
		}
		
		boolean sessionFlag = needsDBRefresh;
		
		if(hasMessagesMap.get(player.getId()) || GameServiceImpl.hasUserArrived(player) || !commStruct.scoreMessages.isEmpty()) {
			sessionFlag = true;
		}
		
		if(sessionFlag) {
			Session session = PersistenceManager.getSession();
			Transaction tx = session.beginTransaction();
			try {
				synchronized(userCacheMap.get(player.getId())) {
					String authCode = player.getAuthCode();
					if (authCode == null) {
						System.out.println("AuthCode is null, bad things happened!");
					}
					
					User user = AuthenticationUtil.authenticatePlayer(session, player);

					UserDTO cachePlayer = MessageServiceImpl.userCacheMap.get(player.getId());
					cachePlayer.setLastRequest(new Date());
					
					// why are we querying the database for these messages?
					// idea: use synchronized cache map for messages
					// make sure list is synchronized - java.utils.concurrent
					// eliminate transaction mechanisms
					
					// if you use a queue you don't have to check this shit every time

					MessageDTO newMessage;
					ConcurrentLinkedQueue<MessageDTO> messageQueue;
					hasMessagesMap.put(player.getId(), Boolean.FALSE);
					// as long as I've got messages in my queue, read them out
					synchronized (messagesMap.get(user.getId())) {
						messageQueue = messagesMap.get(user.getId());
						while ((newMessage = messageQueue.poll()) != null) {
							//DeliveredMessage deliveredMessage = new DeliveredMessage();
							//deliveredMessage.setMessage(new Message(newMessage));
							//deliveredMessage.setReceiver(user);
							//deliveredMessage.setTimeReceived(new Date());
							//session.save(deliveredMessage);
							commStruct.messages.add(new MessageDTO(newMessage));
						}
					}
				
				
					int score = user.getScore();
					for(ScoreMessage scoreMessage : commStruct.scoreMessages) {
						score+=scoreMessage.getScoreObtained();
	
						Action scoreIncreaseAction = new Action(user, ActionType.SCORE_INCREASE);
						scoreIncreaseAction.setScoreIncrease(scoreMessage.getScoreObtained());
						scoreIncreaseAction.setNewScore(score);
						session.save(scoreIncreaseAction);
					}
					
					cachePlayer = GameServiceImpl.updateUserLocation(cachePlayer);
					cachePlayer.setLastRequest(new Date());
					// we need this line to make sure that scores aren't accidentally overwritten
					cachePlayer.setScore(user.getScore());
					commStruct.player = cachePlayer;
					updateCacheMapFromClient(cachePlayer);
	
					tx.commit();
				}
				
				} catch (JDBCException ex) {
					tx.rollback();
					logger.error(player.getUsername() + ": GET MESSAGES FAILED!");
					logger.error(ex.getMessage(), ex);
					String error_sql = ex.getSQL();
					if (error_sql != null) {
						logger.error("The following SQL caused the exception: " + ex.getSQL());
					}
				} catch(HibernateException ex) {
					tx.rollback();
					logger.error(player.getUsername() + ": GET MESSAGES FAILED!");
					logger.error(ex.getMessage(), ex);
					throw new DBException(ex);
				} catch (NullPointerException ex) {
					tx.rollback();
					logger.error(player.getUsername() + ": GET MESSAGES FAILED!");
					logger.error(ex.getMessage(), ex);
					StackTraceElement elements[] = ex.getStackTrace();
					for (StackTraceElement element: elements) {
						logger.error(element.getMethodName() + element.getLineNumber());
					}
					throw new DBException(ex);
				}
		} else try {
//			commStruct.player = GameServiceImpl.userCacheMap.get(player.getId());
			commStruct.player = userCacheMap.get(player.getId());

			if (player.getCurrentRoadMovement() == null) {
				//System.out.println("WHY");
			}
			if (player.getCurrentRoadMovement() != null) {
				commStruct.player.updatePosition();
			}
			commStruct.player.setLastRequest(new Date());

		} catch (Exception ex) {
			logger.warn("Exception caught when updating player " + player.getUsername());
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + ": " + element.getLineNumber());
			}
			for (UserDTO element : userCacheMap.values()) {
				logger.info("player " + element.getUsername() + " is in the cache map with key " + element.getId());
			}
		}
		
		commStruct.timestamp = new Date();
		commStruct.gameStarted = GameServiceImpl.isGameStarted;
		commStruct.gameFinished = GameServiceImpl.isGameFinished;
		commStruct.timeRemaining = GameServiceImpl.endTime - commStruct.timestamp.getTime();
		commStruct.gameDuration = GameServiceImpl.gameDuration;

//		tl.endTime = System.currentTimeMillis();
//		GameServiceImpl.transactionLog.add(tl);
		}
		return commStruct;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void sendMessage(UserDTO player, MessageDTO newMessage) throws GeoGameException {
		logger.warn("Player " + player.getUsername() + " sends: \"" + newMessage.getContent() + "\"");
		if(!newMessage.getSender().equals(player.getUsername())) {
			// If enters here, then username does not match the message's sender.
			// Now throws an exception
			InvalidMessageException unmatchedUsers = new InvalidMessageException("Reported and actual senders do not match!\n " +
					"Reported: " + player.getUsername() + "\n Actual: " + newMessage.getSender());
			throw(unmatchedUsers);
		}
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {	
//			TransactionLog tl = new TransactionLog("sendMessage", player.getUsername());
//			tl.startTime = System.currentTimeMillis();

			User user = AuthenticationUtil.authenticatePlayer(session, player);

			UserDTO cachePlayer = MessageServiceImpl.userCacheMap.get(player.getId());
			MessageServiceImpl.updateUserWithPlayer(session, user, cachePlayer);

			Message message = new Message(newMessage);
			session.save(message);
			
			message.setSender(user);

			DeliveredMessage autoDeliveredMessage = new DeliveredMessage();
			autoDeliveredMessage.setMessage(message);
			autoDeliveredMessage.setReceiver(user);
			//TODO: autoDeliveredMessage.setTimeReceived(new Date());
			session.save(autoDeliveredMessage);
			

			List<User> receivers;
			List<String> receiverNames = new ArrayList<String>();
			
			if (newMessage.isBroadcast()) {
				receivers = user.getNeighbors();			
			}
			else {
				receivers = (List<User>)session.createCriteria(User.class)
					.add(Restrictions.in("username",newMessage.getReceivers()))
					.list();
			}

			for (User receiver : receivers) {
				receiverNames.add(receiver.getUsername());
			}
			// receive your own messages
			hasMessagesMap.put(user.getId(), Boolean.TRUE);
			//TODO: messagesMap.get(user.getId()).add(new MessageDTO (newMessage));
			receiverNames.add(user.getUsername());
			
			for(User receiver : receivers) {
				DeliveredMessage newDeliveredMessage = new DeliveredMessage();
				newDeliveredMessage.setMessage(message);
				newDeliveredMessage.setReceiver(receiver);
				//newDeliveredMessage.setTimeReceived(new Date());
				session.save(newDeliveredMessage);
				
				hasMessagesMap.put(receiver.getId(), Boolean.TRUE);
				//TODO: put the messages in the message map
				//newMessage.setReceivers(receiverNames);
				//synchronized(messagesMap.get(receiver.getId())) {
				//	messagesMap.get(receiver.getId()).add(new MessageDTO(newMessage));
				//}
			}
			
			
			//session.update(user);
//			session.update(user);
//			tl.endTime = System.currentTimeMillis();
//			GameServiceImpl.transactionLog.add(tl);
			tx.commit();
		} catch (JDBCException ex) {
			tx.rollback();
			logger.error(player.getUsername() + ": SEND MESSAGE FAILED!");
			logger.error(ex.getMessage(), ex);
			String error_sql = ex.getSQL();
			if (error_sql != null) {
				logger.error("The following SQL caused the exception: " + ex.getSQL());
			}
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(player.getUsername() + ": SEND MESSAGE FAILED!");
			logger.error(ex.getMessage(), ex);
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
			throw new DBException(ex);
		} catch(NullPointerException ex) {
			tx.rollback();
			logger.error(player.getUsername() + ": SEND MESSAGE FAILED!");
			logger.error(ex.getMessage(), ex);
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
			throw new DBException(ex);
		} 
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommStruct getMessages(UserDTO player, Date minTimestamp, boolean needsDBRefresh) throws GeoGameException {
		System.out.println("IN GET MESSAGES" + player.getId());
		if(DevServiceImpl.resetFlag) {
			throw new AuthorizationException(null, "Game is resetting");
		}
		if(player.getUsername().equals("TestUser0")) {
			logger.trace("Inside MessageServiceImpl");
		}

//		TransactionLog tl = new TransactionLog("getMessages", player.getUsername());
//		tl.startTime = System.currentTimeMillis();
		
		CommStruct commStruct = new CommStruct();
		synchronized (commStruct) {
		if(new Date().getTime()-DevServiceImpl.lastResetTime<TIME_AFTER_RESET_LOGOFF) {
			commStruct.logOffPlayer = true;
//			tl.endTime = System.currentTimeMillis();
//			GameServiceImpl.transactionLog.add(tl);

			commStruct.timestamp = new Date();
			commStruct.gameStarted = GameServiceImpl.isGameStarted;
			commStruct.gameFinished = GameServiceImpl.isGameFinished;
			commStruct.timeRemaining = GameServiceImpl.endTime - commStruct.timestamp.getTime();
			commStruct.gameDuration = GameServiceImpl.gameDuration;
			return commStruct;
		}
		
		if(!AuthenticationUtil.authenticatePlayer(player)) {
//			tl.endTime = System.currentTimeMillis();
//			GameServiceImpl.transactionLog.add(tl);

			commStruct.timestamp = new Date();
			commStruct.gameStarted = GameServiceImpl.isGameStarted;
			commStruct.gameFinished = GameServiceImpl.isGameFinished;
			commStruct.timeRemaining = GameServiceImpl.endTime - commStruct.timestamp.getTime();
			commStruct.gameDuration = GameServiceImpl.gameDuration;
			return commStruct;
		}
		
		// distribute any score messages
		if (scoreMessagesMap.get(player.getId()) != null) {
			commStruct.scoreMessages = new ArrayList<ScoreMessage>(scoreMessagesMap.get(player.getId()));
		} else {
			commStruct.scoreMessages = new ArrayList<ScoreMessage>();
		}
		// clear the score messages map
		if (!commStruct.scoreMessages.isEmpty()) {
			scoreMessagesMap.get(player.getId()).clear();
		}
		//distribute commands
		if (commandMap.get(player.getId()) != null) {
			commStruct.commands = new ArrayList<GeoGameCommandDTO>(commandMap.get(player.getId()));
		}
		else {
			commStruct.commands = new ArrayList<GeoGameCommandDTO>();
		}
		//clear the command map
		if (!commStruct.commands.isEmpty()) {
			commandMap.get(player.getId()).clear();
		}
		
		// distribute regular messages
		/*if (messagesMap.containsValue(player.getId())) {
			commStruct.messages = new ArrayList<MessageDTO>(messagesMap.get(player.getId()));
			messagesMap.get(player.getId()).clear();
		}*/
		
		boolean sessionFlag = needsDBRefresh;

		if(hasMessagesMap.get(player.getId()) || GameServiceImpl.hasUserArrived(player) || !commStruct.scoreMessages.isEmpty()) {
			sessionFlag = true;
		}
		
		if(sessionFlag) {
			System.out.println("DB interaction starts" + player.getId());
			Session session = PersistenceManager.getSession();
			Transaction tx = session.beginTransaction();
			try {
				synchronized(userCacheMap.get(player.getId())) {
					String authCode = player.getAuthCode();
					if (authCode == null) {
						System.out.println("AuthCode is null, bad things happened!");
					}
					User user = AuthenticationUtil.authenticatePlayer(session, player);

					UserDTO cachePlayer = MessageServiceImpl.userCacheMap.get(player.getId());
					//cachePlayer.setLastRequest(new Date());
					MessageServiceImpl.updateUserWithPlayer(session, user, cachePlayer);
					
					// why are we querying the database for these messages?
					// idea: use synchronized cache map for messages
					// make sure list is synchronized - java.utils.concurrent
					// eliminate transaction mechanisms
					
					// if you use a queue you don't have to check this shit every time
					if(hasMessagesMap.get(player.getId())) {
						//TODO:
						Criteria deliveredMessageCriteria = session.createCriteria(DeliveredMessage.class).add(Restrictions.isNull("timeReceived"));
						deliveredMessageCriteria.add(Restrictions.eq("receiver", user));
						
						List<DeliveredMessage> deliveredMessages = (List<DeliveredMessage>)deliveredMessageCriteria.list();

						//TODO: MessageDTO newMessage;
						//ConcurrentLinkedQueue<MessageDTO> messageQueue;
						// as long as I've got messages in my queue, read them out
						/*synchronized (messagesMap.get(user.getId())) {
							messageQueue = messagesMap.get(user.getId());
							while ((newMessage = messageQueue.poll()) != null) {
								/*DeliveredMessage deliveredMessage = new DeliveredMessage();
								deliveredMessage.setMessage(new Message(newMessage));
								deliveredMessage.setReceiver(user);
								deliveredMessage.setTimeReceived(new Date());
								session.save(deliveredMessage);
								commStruct.messages.add(new MessageDTO(newMessage));
							}
						}*/
						
						//List<MessageDTO> newMessages = messagesMap.get(user.getId());
						// turn off message flag
						hasMessagesMap.put(player.getId(), Boolean.FALSE);

						for(DeliveredMessage deliveredMessage: deliveredMessages) {
							//Passing in empty list of users - getMessage should not be using the receivers of the message anyway.
							//If communication is not allowed, just don't distribute the messages to them. We'll log them just in case.
							if (GameServiceImpl.serverSettings.getCommAllowed()) {
								commStruct.messages.add(new MessageDTO(deliveredMessage.getMessage(), new ArrayList<User>()));
							}
							deliveredMessage.setTimeReceived(new Date());
			//				message.setDelivered(true);
			//				session.saveOrUpdate(message);
						}
						
						/*for(MessageDTO newMessage : newMessages) {
							//Passing in empty list of users - getMessage should not be using the receivers of the message anyway.
							DeliveredMessage deliveredMessage = new DeliveredMessage();
							deliveredMessage.setMessage(new Message(newMessage));
							deliveredMessage.setReceiver(user);
							session.save(deliveredMessage);
							
							commStruct.messages.add(newMessage);
							//deliveredMessage.setTimeReceived(new Date());
			//				message.setDelivered(true);
			//				session.saveOrUpdate(message);
						}*/
						
					}
		
					int score = user.getScore();
					for(ScoreMessage scoreMessage : commStruct.scoreMessages) {
						score+=scoreMessage.getScoreObtained();

						Action scoreIncreaseAction = new Action(user, ActionType.SCORE_INCREASE);
						scoreIncreaseAction.setScoreIncrease(scoreMessage.getScoreObtained());
						scoreIncreaseAction.setNewScore(score);
						session.save(scoreIncreaseAction);
					}
					user.setScore(score);

					// seems like the wrong place to do this...
					
					GameServiceImpl.updateUserLocationIfArrived(session, user);

					commStruct.player = updateCacheMap(session, user, true);

					tx.commit();
					System.out.println("DB interaction ends" + player.getId());
				}
			} catch (JDBCException ex) {
				tx.rollback();
				logger.error(player.getUsername() + ": GET MESSAGES FAILED!");
				logger.error(ex.getMessage(), ex);
				String error_sql = ex.getSQL();
				if (error_sql != null) {
					logger.error("The following SQL caused the exception: " + ex.getSQL());
				}
			} catch(HibernateException ex) {
				tx.rollback();
				logger.error(player.getUsername() + ": GET MESSAGES FAILED!");
				logger.error(ex.getMessage(), ex);
				throw new DBException(ex);
			} catch (NullPointerException ex) {
				tx.rollback();
				logger.error(player.getUsername() + ": GET MESSAGES FAILED!");
				logger.error(ex.getMessage(), ex);
				StackTraceElement elements[] = ex.getStackTrace();
				for (StackTraceElement element: elements) {
					logger.error(element.getMethodName() + element.getLineNumber());
				}
				throw new DBException(ex);
			}
		} else try {
//			commStruct.player = GameServiceImpl.userCacheMap.get(player.getId());
			commStruct.player = userCacheMap.get(player.getId());

			if (player.getCurrentRoadMovement() == null) {
				//System.out.println("WHY");
			}
			if (player.getCurrentRoadMovement() != null) {
				commStruct.player.updatePosition();
			}
			commStruct.player.setLastRequest(new Date());

		} catch (Exception ex) {
			logger.warn("Exception caught when updating player " + player.getUsername());
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + ": " + element.getLineNumber());
			}
			for (UserDTO element : userCacheMap.values()) {
				logger.info("player " + element.getUsername() + " is in the cache map with key " + element.getId());
			}
		}
		
		commStruct.timestamp = new Date();
		commStruct.gameStarted = GameServiceImpl.isGameStarted;
		commStruct.gameFinished = GameServiceImpl.isGameFinished;
		commStruct.timeRemaining = GameServiceImpl.endTime - commStruct.timestamp.getTime();
		commStruct.gameDuration = GameServiceImpl.gameDuration;

//		tl.endTime = System.currentTimeMillis();
//		GameServiceImpl.transactionLog.add(tl);
		}
		return commStruct;
	}
	
	public static void updateCacheMapFromClient(UserDTO player) {
		synchronized(userCacheMap) {
			userCacheMap.put(player.getId(), player);
		}
	}
	
	public static UserDTO updateCacheMap(Session session, User user, boolean includeNeighbors) {
		UserDTO player;
		if(userCacheMap.containsKey(user.getId())) {
			player = userCacheMap.get(user.getId());
			player.updateWithUser(user, includeNeighbors, userCacheMap);
			if(synMap.get(user.getId()) != null)
				player.setItemNameSynPairs(synMap.get(user.getId()));
			//System.out.println(player.getItemNameSynPairs());
		} else {
//			synchronized(userCacheMap.get(user.getId())) {
				player = new UserDTO(user, includeNeighbors, userCacheMap);
				userCacheMap.put(player.getId(), player);

				for(User neighbor : user.getNeighbors()) {
					UserDTO cacheNeighbor = userCacheMap.get(neighbor.getId());
					if(cacheNeighbor!=null) {
						player.getNeighbors().add(cacheNeighbor);
						cacheNeighbor.getNeighbors().add(player);
					}
				}
				
				List<ItemType> goalItemsCopy = new ArrayList<ItemType>(user.getItemsToCollect());
				for(Item item : user.getInventory()) {
					goalItemsCopy.remove(item);
				}
				if(!goalItemsCopy.isEmpty()) {
					player.setCurrentGoalItemType(new ItemTypeDTO(goalItemsCopy.get(0)));
				}
//			}
		}
		return player;
	}

	public static void updateUserWithPlayer(Session session, User user, UserDTO player) {
		try {
//		synchronized(userCacheMap.get(user.getId())) {
			if(player.getCurrentLocation()!=null) {
				user.setCurrentLocation((Location)session.get(Location.class, player.getCurrentLocation().getId()));
			}
			if(player.getCurrentRoad()!=null) {
				user.setCurrentRoad((Road)session.get(Road.class, player.getCurrentRoad().getId()));
			}
			if(player.getCurrentRoadMovement()!=null) {
				user.setCurrentRoadMovement((RoadMovement)session.get(RoadMovement.class, player.getCurrentRoadMovement().getId()));
			}
			user.setLatitude(player.getLatitude());
			user.setLongitude(player.getLongitude());
			user.setScore(player.getScore());
			user.setForward(player.getForward());
			user.setLoggedIn(player.isLoggedIn());
			user.setMoving(player.isMoving());
			user.setLastRequest(player.getLastRequest());
		} catch (Exception ex) {
			logger.error("Error occurred updating player " + player.getUsername());
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
		}
	}
	
	// static functions for interacting with agents
	
	public static CommStruct getAgentMessages(UserDTO player, Date minTimestamp, boolean needsDBRefresh) throws GeoGameException {
		if(DevServiceImpl.resetFlag) {
			throw new AuthorizationException(null, "Game is resetting");
		}
		if(player.getUsername().equals("TestUser0")) {
			logger.trace("Inside MessageServiceImpl");
		}

//		TransactionLog tl = new TransactionLog("getMessages", player.getUsername());
//		tl.startTime = System.currentTimeMillis();
		
		CommStruct commStruct = new CommStruct();
		synchronized (commStruct) {
		if(new Date().getTime()-DevServiceImpl.lastResetTime<TIME_AFTER_RESET_LOGOFF) {
			commStruct.logOffPlayer = true;
//			tl.endTime = System.currentTimeMillis();
//			GameServiceImpl.transactionLog.add(tl);

			commStruct.timestamp = new Date();
			commStruct.gameStarted = GameServiceImpl.isGameStarted;
			commStruct.gameFinished = GameServiceImpl.isGameFinished;
			commStruct.timeRemaining = GameServiceImpl.endTime - commStruct.timestamp.getTime();
			commStruct.gameDuration = GameServiceImpl.gameDuration;
			return commStruct;
		}
		
		if(!AuthenticationUtil.authenticatePlayer(player)) {
//			tl.endTime = System.currentTimeMillis();
//			GameServiceImpl.transactionLog.add(tl);

			commStruct.timestamp = new Date();
			commStruct.gameStarted = GameServiceImpl.isGameStarted;
			commStruct.gameFinished = GameServiceImpl.isGameFinished;
			commStruct.timeRemaining = GameServiceImpl.endTime - commStruct.timestamp.getTime();
			commStruct.gameDuration = GameServiceImpl.gameDuration;
			return commStruct;
		}
		
		// distribute any score messages
		if (scoreMessagesMap.get(player.getId()) != null) {
			commStruct.scoreMessages = new ArrayList<ScoreMessage>(scoreMessagesMap.get(player.getId()));
		} else {
			commStruct.scoreMessages = new ArrayList<ScoreMessage>();
		}
		// clear the score messages map
		if (!commStruct.scoreMessages.isEmpty()) {
			scoreMessagesMap.get(player.getId()).clear();
		}
		
		
		// distribute regular messages
		/*if (messagesMap.containsValue(player.getId())) {
			commStruct.messages = new ArrayList<MessageDTO>(messagesMap.get(player.getId()));
			messagesMap.get(player.getId()).clear();
		}*/
		
		boolean sessionFlag = needsDBRefresh;

		if(hasMessagesMap.get(player.getId()) || GameServiceImpl.hasUserArrived(player) || !commStruct.scoreMessages.isEmpty()) {
			sessionFlag = true;
		}
		
		if(sessionFlag) {
			Session session = PersistenceManager.getSession();
			Transaction tx = session.beginTransaction();
			try {
				synchronized(userCacheMap.get(player.getId())) {
					String authCode = player.getAuthCode();
					if (authCode == null) {
						System.out.println("AuthCode is null, bad things happened!");
					}
					User user = AuthenticationUtil.authenticatePlayer(session, player);

					UserDTO cachePlayer = MessageServiceImpl.userCacheMap.get(player.getId());
					//cachePlayer.setLastRequest(new Date());
					MessageServiceImpl.updateUserWithPlayer(session, user, cachePlayer);
					
					// why are we querying the database for these messages?
					// idea: use synchronized cache map for messages
					// make sure list is synchronized - java.utils.concurrent
					// eliminate transaction mechanisms
					
					// if you use a queue you don't have to check this shit every time
					if(hasMessagesMap.get(player.getId())) {
						//TODO:
						Criteria deliveredMessageCriteria = session.createCriteria(DeliveredMessage.class).add(Restrictions.isNull("timeReceived"));
						deliveredMessageCriteria.add(Restrictions.eq("receiver", user));
						
						List<DeliveredMessage> deliveredMessages = (List<DeliveredMessage>)deliveredMessageCriteria.list();

						//TODO: MessageDTO newMessage;
						//ConcurrentLinkedQueue<MessageDTO> messageQueue;
						// as long as I've got messages in my queue, read them out
						/*synchronized (messagesMap.get(user.getId())) {
							messageQueue = messagesMap.get(user.getId());
							while ((newMessage = messageQueue.poll()) != null) {
								/*DeliveredMessage deliveredMessage = new DeliveredMessage();
								deliveredMessage.setMessage(new Message(newMessage));
								deliveredMessage.setReceiver(user);
								deliveredMessage.setTimeReceived(new Date());
								session.save(deliveredMessage);
								commStruct.messages.add(new MessageDTO(newMessage));
							}
						}*/
						
						//List<MessageDTO> newMessages = messagesMap.get(user.getId());
						// turn off message flag
						hasMessagesMap.put(player.getId(), Boolean.FALSE);

						for(DeliveredMessage deliveredMessage: deliveredMessages) {
							//Passing in empty list of users - getMessage should not be using the receivers of the message anyway.
							commStruct.messages.add(new MessageDTO(deliveredMessage.getMessage(), new ArrayList<User>()));
							deliveredMessage.setTimeReceived(new Date());
			//				message.setDelivered(true);
			//				session.saveOrUpdate(message);
						}
						
						/*for(MessageDTO newMessage : newMessages) {
							//Passing in empty list of users - getMessage should not be using the receivers of the message anyway.
							DeliveredMessage deliveredMessage = new DeliveredMessage();
							deliveredMessage.setMessage(new Message(newMessage));
							deliveredMessage.setReceiver(user);
							session.save(deliveredMessage);
							
							commStruct.messages.add(newMessage);
							//deliveredMessage.setTimeReceived(new Date());
			//				message.setDelivered(true);
			//				session.saveOrUpdate(message);
						}*/
						
					}
		
					int score = user.getScore();
					for(ScoreMessage scoreMessage : commStruct.scoreMessages) {
						score+=scoreMessage.getScoreObtained();

						Action scoreIncreaseAction = new Action(user, ActionType.SCORE_INCREASE);
						scoreIncreaseAction.setScoreIncrease(scoreMessage.getScoreObtained());
						scoreIncreaseAction.setNewScore(score);
						session.save(scoreIncreaseAction);
					}
					user.setScore(score);

					// seems like the wrong place to do this...
					
					GameServiceImpl.updateUserLocationIfArrived(session, user);

					commStruct.player = updateCacheMap(session, user, true);

					tx.commit();
				}
			} catch (JDBCException ex) {
				tx.rollback();
				logger.error(player.getUsername() + ": GET MESSAGES FAILED!");
				logger.error(ex.getMessage(), ex);
				String error_sql = ex.getSQL();
				if (error_sql != null) {
					logger.error("The following SQL caused the exception: " + ex.getSQL());
				}
			} catch(HibernateException ex) {
				tx.rollback();
				logger.error(player.getUsername() + ": GET MESSAGES FAILED!");
				logger.error(ex.getMessage(), ex);
				throw new DBException(ex);
			} catch (NullPointerException ex) {
				tx.rollback();
				logger.error(player.getUsername() + ": GET MESSAGES FAILED!");
				logger.error(ex.getMessage(), ex);
				StackTraceElement elements[] = ex.getStackTrace();
				for (StackTraceElement element: elements) {
					logger.error(element.getMethodName() + element.getLineNumber());
				}
				throw new DBException(ex);
			} finally {
				session.close();
			}
		} else try {
//			commStruct.player = GameServiceImpl.userCacheMap.get(player.getId());
			commStruct.player = userCacheMap.get(player.getId());

			if (player.getCurrentRoadMovement() == null) {
				//System.out.println("WHY");
			}
			if (player.getCurrentRoadMovement() != null) {
				commStruct.player.updatePosition();
			}
			commStruct.player.setLastRequest(new Date());

		} catch (Exception ex) {
			logger.warn("Exception caught when updating player " + player.getUsername());
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + ": " + element.getLineNumber());
			}
			for (UserDTO element : userCacheMap.values()) {
				logger.info("player " + element.getUsername() + " is in the cache map with key " + element.getId());
			}
		}
		
		commStruct.timestamp = new Date();
		commStruct.gameStarted = GameServiceImpl.isGameStarted;
		commStruct.gameFinished = GameServiceImpl.isGameFinished;
		commStruct.timeRemaining = GameServiceImpl.endTime - commStruct.timestamp.getTime();
		commStruct.gameDuration = GameServiceImpl.gameDuration;

//		tl.endTime = System.currentTimeMillis();
//		GameServiceImpl.transactionLog.add(tl);
		}
		return commStruct;
	}
}