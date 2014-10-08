package edu.cmu.cs.cimds.geogame.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.cmu.cs.cimds.geogame.client.exception.AuthenticationException;
import edu.cmu.cs.cimds.geogame.client.model.db.User;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

public class AuthenticationUtil {
	
	private static Map<String, String> playerAuthCodesMap = new HashMap<String, String>();
//	private static Map<Long, Date> lastRequestDates = new HashMap<Long, Date>();
	private static Logger logger = Logger.getLogger(AuthenticationUtil.class);
	
	public static void registerUser(User user) {
		playerAuthCodesMap.put(user.getUsername(), user.getAuthCode());
	}
	public static void deRegisterUser(UserDTO player) throws AuthenticationException {
		logger.warn(player.getUsername() + " de-registered!");
		playerAuthCodesMap.remove(player.getUsername());
	}

	public static boolean authenticatePlayer(UserDTO player) throws AuthenticationException {
		String authCode = playerAuthCodesMap.get(player.getUsername());
//		lastRequestDates.put(player.getId(), new Date());
		
//		player.setLastRequest(nowDate);
		if(authCode==null) {
			return false;
		} else {
			return authCode.equals(player.getAuthCode());
		}
	}

	public static User authenticatePlayer(Session session, UserDTO player) throws AuthenticationException {
		try {
			User user = (User)session.createCriteria(User.class)
				.add(Restrictions.eq("username", player.getUsername()))
				.add(Restrictions.eq("admin", player.isAdmin()))
				.uniqueResult();
			
			if(!player.getAuthCode().equals(user.getAuthCode())) {
				//If authCode does not match,, then authentication fails
				throw new AuthenticationException(player);
			}
			//user.setLastRequest(new Date());
	//		lastRequestDates.put(user.getId(), user.getLastRequest());
	//		session.update(user);
			return user;
		} catch (NullPointerException ex) {
			logger.error("Something went horribly wrong!");
			logger.error(ex.getMessage());
			logger.error("Player: " + player);
			logger.error("Session: " + session);
			throw new AuthenticationException(player);
		}
	}

//	public static void deAuthenticatePlayer(Session session, UserDTO player) throws AuthenticationException {
//		playerAuthCodesMap.remove(player.getUsername());
//	}
	
//	public static void updateLastRequestDate(Session session, User user) {
//		if(lastRequestDates.get(user.getId())==null) {
//			return;
//		}
//		if(lastRequestDates.get(user.getId()).getTime()>user.getLastRequest().getTime()) {
//			user.setLastRequest(lastRequestDates.get(user.getId()));
//			session.update(user);
//		} else {
//			lastRequestDates.put(user.getId(), user.getLastRequest());
//		}
//	}
}