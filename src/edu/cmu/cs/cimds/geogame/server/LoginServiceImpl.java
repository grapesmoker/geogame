package edu.cmu.cs.cimds.geogame.server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.log4j.Logger;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jasypt.util.password.BasicPasswordEncryptor;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.cmu.cs.cimds.geogame.client.DisplayResult;
import edu.cmu.cs.cimds.geogame.client.LoginResult;
import edu.cmu.cs.cimds.geogame.client.exception.AuthorizationException;
import edu.cmu.cs.cimds.geogame.client.exception.DBException;
import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;
import edu.cmu.cs.cimds.geogame.client.exception.InvalidArgsException;
import edu.cmu.cs.cimds.geogame.client.model.db.AcceptanceForm;
import edu.cmu.cs.cimds.geogame.client.model.db.AcceptedForm;
import edu.cmu.cs.cimds.geogame.client.model.db.Action;
import edu.cmu.cs.cimds.geogame.client.model.db.User;
import edu.cmu.cs.cimds.geogame.client.model.dto.AcceptanceFormDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.MessageDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ScoreMessage;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.model.enums.ActionType;
import edu.cmu.cs.cimds.geogame.client.services.LoginService;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {
	
	private static final long serialVersionUID = 5557097158301985678L;

	private static Logger logger = Logger.getLogger(LoginServiceImpl.class);
	
	static {
		MessageServiceImpl.initialize();
	}

	private static final long GENERAL_INSTRUCTIONS_FORM_ID = 2;
	private static final long FAMILIARIZATION_INSTRUCTIONS_FORM_ID = 3;
	
	public static LoginResult sendAgentLogin (String username, String password) throws GeoGameException {
		logger.info(Thread.currentThread().getId() + " -- Processing login of user: " + username);
		
		LoginResult loginResult = new LoginResult();

		BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			//Look in database for username
			User user = (User)session.createCriteria(User.class)
			.add(Restrictions.eq("username", username))
//			.setFetchMode("neighbors1", FetchMode.SELECT)
//			.setFetchMode("neighbors2", FetchMode.SELECT)
			.setFetchMode("neighbors", FetchMode.SELECT)
			.setFetchMode("inventory", FetchMode.SELECT)
			.uniqueResult();

			// IMPORTANT!
			// You MUST use the checkPassword method of the passwordEncryptor to compare
			// the submitted to the stored password! You cannot hash the submitted password
			// again and compare the two hashes; they will be different. I suspect this has
			// to do with random salting, but I'm not sure.
			// -JV 9/15/2010
			
			if(user!=null && passwordEncryptor.checkPassword(password, user.getPassword())) {
				//Login successful
				user.setLoggedIn(true);
				loginResult.setSuccess(true);
				UserDTO player = MessageServiceImpl.updateCacheMap(session, user, true);
				MessageServiceImpl.scoreMessagesMap.put(user.getId(), new ArrayList<ScoreMessage>());
				MessageServiceImpl.messagesMap.put(user.getId(), new ConcurrentLinkedQueue<MessageDTO>());
				String authCode = generateAuthCodeForUser(user);
				player.setAuthCode(authCode);
				loginResult.setPlayer(player);

				user.setAuthCode(authCode);
				
				Action loginAction = new Action(user, ActionType.LOGIN);

				session.save(loginAction);
				
				GameServiceImpl.updateUserLocationIfArrived(session, user);
//				session.update(user);

				logger.info(Thread.currentThread().getId() + " -- Login of player " + username + " successful");
				AuthenticationUtil.registerUser(user);
			} else {
				//Login failed
				loginResult.setSuccess(false);
				loginResult.setPlayer(null);
				logger.warn(Thread.currentThread().getId() + " -- Login failed");
			}
			tx.commit();
			return loginResult;
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage());
			throw new DBException(ex);
		} catch(NullPointerException ex) {
			tx.rollback();
			logger.error("LOGIN FAILED!");
			logger.error(ex.getMessage(), ex);
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
			throw new DBException(ex);
		}
	}
	
	@Override
	public LoginResult sendLogin(String username, String password) throws GeoGameException {
//		return LoginLogic.getInstance().sendLogin(username, password);
		logger.info(Thread.currentThread().getId() + " -- Processing login of user: " + username);
		
		LoginResult loginResult = new LoginResult();

		BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			//Look in database for username
			User user = (User)session.createCriteria(User.class)
			.add(Restrictions.eq("username", username))
//			.setFetchMode("neighbors1", FetchMode.SELECT)
//			.setFetchMode("neighbors2", FetchMode.SELECT)
			.setFetchMode("neighbors", FetchMode.SELECT)
			.setFetchMode("inventory", FetchMode.SELECT)
			.uniqueResult();

			// IMPORTANT!
			// You MUST use the checkPassword method of the passwordEncryptor to compare
			// the submitted to the stored password! You cannot hash the submitted password
			// again and compare the two hashes; they will be different. I suspect this has
			// to do with random salting, but I'm not sure.
			// -JV 9/15/2010
			
			if(user!=null && passwordEncryptor.checkPassword(password, user.getPassword())) {
				//Login successful
				user.setLoggedIn(true);
				loginResult.setSuccess(true);
				if (user.isAMTVisitor() && user.getAMTCode() == null) {
					SecureRandom random = new SecureRandom();
					String AMTCode = new BigInteger(130, random).toString(32);
					user.setAMTCode(AMTCode);
				}
				UserDTO player = MessageServiceImpl.updateCacheMap(session, user, true);
				MessageServiceImpl.scoreMessagesMap.put(user.getId(), new ArrayList<ScoreMessage>());
				MessageServiceImpl.messagesMap.put(user.getId(), new ConcurrentLinkedQueue<MessageDTO>());
				String authCode = generateAuthCodeForUser(user);
				player.setAuthCode(authCode);
				loginResult.setPlayer(player);

				user.setAuthCode(authCode);
				
				Action loginAction = new Action(user, ActionType.LOGIN);

				session.save(loginAction);
				
				GameServiceImpl.updateUserLocationIfArrived(session, user);
//				session.update(user);

				logger.info(Thread.currentThread().getId() + " -- Login of player " + username + " successful");
				AuthenticationUtil.registerUser(user);
			} else {
				//Login failed
				loginResult.setSuccess(false);
				loginResult.setPlayer(null);
				logger.warn(Thread.currentThread().getId() + " -- Login failed");
			}
			tx.commit();
			return loginResult;
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage());
			throw new DBException(ex);
		} catch(NullPointerException ex) {
			tx.rollback();
			logger.error("LOGIN FAILED!");
			logger.error(ex.getMessage(), ex);
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
			throw new DBException(ex);
		} 
	}

	@Override
	public LoginResult sendAdminLogin(String username, String password) throws GeoGameException {
//		return LoginLogic.getInstance().sendLogin(username, password);
		logger.info(Thread.currentThread().getId() + " -- Processing login of user: " + username);
		
		LoginResult loginResult = new LoginResult();

		BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			//Look in database for username
			User user = (User)session.createCriteria(User.class)
			.add(Restrictions.eq("username", username))
			.add(Restrictions.eq("admin", Boolean.TRUE))
//			.setFetchMode("neighbors1", FetchMode.SELECT)
//			.setFetchMode("neighbors2", FetchMode.SELECT)
			.setFetchMode("neighbors", FetchMode.SELECT)
			.setFetchMode("inventory", FetchMode.SELECT)
			.uniqueResult();

			// IMPORTANT!
			// You MUST use the checkPassword method of the passwordEncryptor to compare
			// the submitted to the stored password! You cannot hash the submitted password
			// again and compare the two hashes; they will be different. I suspect this has
			// to do with random salting, but I'm not sure.
			// -JV 9/15/2010
			
			if(user!=null && passwordEncryptor.checkPassword(password, user.getPassword())) {
				//Login successful
				user.setLoggedIn(true);
				loginResult.setSuccess(true);
				UserDTO player = MessageServiceImpl.updateCacheMap(session, user, true);
				String authCode = generateAuthCodeForUser(user);
				player.setAuthCode(authCode);
				loginResult.setPlayer(player);

				user.setAuthCode(authCode);
				
				Action loginAction = new Action(user, ActionType.LOGIN);

				session.save(loginAction);
				
				GameServiceImpl.updateUserLocationIfArrived(session, user);
//				session.update(user);

				logger.info(Thread.currentThread().getId() + " -- Login of player " + username + " successful");
				AuthenticationUtil.registerUser(user);
			} else {
				//Login failed
				loginResult.setSuccess(false);
				loginResult.setPlayer(null);
				logger.warn(Thread.currentThread().getId() + " -- Login failed");
			}
			tx.commit();
			return loginResult;
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage());
			throw new DBException(ex);
		} catch(NullPointerException ex) {
			tx.rollback();
			logger.error("ADMIN LOGIN FAILED!");
			logger.error(ex.getMessage(), ex);
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
			throw new DBException(ex);
		} 
	}

	@Override
	public LoginResult sendRefreshLogin(UserDTO player) throws GeoGameException {
//		return LoginLogic.getInstance().sendLogin(username, password);
		logger.info(Thread.currentThread().getId() + " -- Refreshing login information for: (" + player.getUsername() + ")");
		
		LoginResult loginResult = new LoginResult();

		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			User user = AuthenticationUtil.authenticatePlayer(session, player);

//			user.setLoggedIn(true);
			loginResult.setSuccess(true);
			player = MessageServiceImpl.updateCacheMap(session, user, true);
//			String authCode = generateAuthCodeForUser(user);
//			player.setAuthCode(authCode);
			loginResult.setPlayer(player);

//			user.setAuthCode(authCode);
			
			Action loginAction = new Action(user, ActionType.LOGIN_REFRESH);
			session.save(loginAction);
			
			GameServiceImpl.updateUserLocationIfArrived(session, user);
//			session.update(user);

			logger.info(Thread.currentThread().getId() + " -- Refresh login of player " + player.getUsername() + " successful");
//			AuthenticationUtil.registerUser(user);
			tx.commit();
			return loginResult;
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage());
			throw new DBException(ex);
		} catch(NullPointerException ex) {
			tx.rollback();
			logger.error(player.getUsername() + ": REFRESH LOGIN FAILED!");
			logger.error(ex.getMessage(), ex);
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
			throw new DBException(ex);
		}
	}

	@Override
	public Boolean sendLogout(UserDTO player) throws GeoGameException {
//		return LoginLogic.getInstance().sendLogout(player);
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			User user = AuthenticationUtil.authenticatePlayer(session, player);
			if(user!=null) {
				//Performing logout
				user.setAuthCode(null);
				user.setLoggedIn(false);

				MessageServiceImpl.updateCacheMap(session, user, true);

				Action logoutAction = new Action(user, ActionType.LOGOUT);
				logger.info(Thread.currentThread().getId() + " -- Processing logout of user: " + player.getUsername());
				session.save(logoutAction);

//				session.update(user);
				tx.commit();
				
				AuthenticationUtil.deRegisterUser(player);
				return true;
			} else {
				tx.commit();
				return false;
			}
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage());
			throw new DBException(ex);
		} 
	}
	
	private static String generateAuthCodeForUser(User user) {
		try {
			byte[] hash = MessageDigest.getInstance("MD5").digest((user.getUsername() + new Date() + user.getPassword()).getBytes());
			return new BigInteger(1,hash).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	@Override
	public DisplayResult requestNextAdminDisplay(UserDTO admin) throws GeoGameException {
		DisplayResult result = new DisplayResult();
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			User user = AuthenticationUtil.authenticatePlayer(session, admin);
			if (user != null) {
				result.setShowGame(true);
			}
			else {
				result.setShowGame(false);
			}
			tx.commit();
		}
		catch (GeoGameException ex) {
			tx.rollback();
		}
		catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage());
			throw new DBException(ex);
		} 
		return result;
	}

	@Override
	public DisplayResult requestNextDisplay(UserDTO player) throws GeoGameException {
//		AcceptanceFormDTO formDTO=null;
		DisplayResult result = new DisplayResult();
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			User user = AuthenticationUtil.authenticatePlayer(session, player);
			UserDTO cachePlayer = MessageServiceImpl.userCacheMap.get(user.getId());
			
			List<AcceptanceForm> neededForms = (List<AcceptanceForm>)session.createCriteria(AcceptanceForm.class)
				.add(Restrictions.eq("active", Boolean.TRUE))
				.addOrder(Order.asc("order"))
				.list();
			
			List<AcceptedForm> acceptedForms = (List<AcceptedForm>)session.createCriteria(AcceptedForm.class)
				.add(Restrictions.eq("user", user))
				.add(Restrictions.eq("success", Boolean.TRUE))
				.createCriteria("acceptanceForm")
					.addOrder(Order.asc("order"))
				.list();
			
			while(!acceptedForms.isEmpty() && !neededForms.isEmpty() && acceptedForms.contains(neededForms.get(0))) {
				acceptedForms.remove(neededForms.get(0));
				neededForms.remove(0);
			}
			if(!neededForms.isEmpty()) {
				AcceptanceFormDTO formDTO = new AcceptanceFormDTO(neededForms.get(0));
				result.setAcceptanceForm(formDTO);
				cachePlayer.setCurrentAcceptanceFormDTO(formDTO);
			} else {
				result.setShowGame(true);
				AcceptanceForm familiarizationForm = (AcceptanceForm)session.createCriteria(AcceptanceForm.class)
					.add(Restrictions.idEq(FAMILIARIZATION_INSTRUCTIONS_FORM_ID))
					.uniqueResult();
				if(familiarizationForm.isActive()) {
					result.setPeekFormId(GENERAL_INSTRUCTIONS_FORM_ID);
				}
				cachePlayer.setCurrentAcceptanceFormDTO(null);
			}
			tx.commit();
		} catch(GeoGameException ex) {
			tx.rollback();
		} catch (JDBCException ex) {
			tx.rollback();
			logger.error(player.getUsername() + ": REQUEST NEXT DISPLAY FAILED!");
			logger.error(ex.getMessage(), ex);
			String error_sql = ex.getSQL();
			if (error_sql != null) {
				logger.error("The following SQL caused the exception: " + ex.getSQL());
			}
			throw new DBException(ex);
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(player.getUsername() + ": REQUEST NEXT DISPLAY FAILED!");
			logger.error(ex.getMessage());
			throw new DBException(ex);
		} catch (NullPointerException ex) {
			tx.rollback();
			logger.error(player.getUsername() + ": REQUEST NEXT DISPLAY FAILED!");
			logger.error(ex.getMessage(), ex);
			StackTraceElement elements[] = ex.getStackTrace();
			for (StackTraceElement element: elements) {
				logger.error(element.getMethodName() + element.getLineNumber());
			}
			throw new DBException(ex);
		}
		return result;
	}

	@Override
	public DisplayResult requestPeekDisplay(UserDTO player, long peekFormId) throws GeoGameException {
		DisplayResult result = new DisplayResult();
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			AuthenticationUtil.authenticatePlayer(session, player);
			
			AcceptanceForm requestedForm = (AcceptanceForm)session.createCriteria(AcceptanceForm.class)
				.add(Restrictions.idEq(Long.valueOf(peekFormId)))
				.uniqueResult();
			if(requestedForm==null) {
				throw new InvalidArgsException("Peek form requested (" + peekFormId + ") does not exist!");
			}
			
			AcceptanceFormDTO formDTO = new AcceptanceFormDTO(requestedForm);
			result.setAcceptanceForm(formDTO);
			
			tx.commit();
		} catch(GeoGameException ex) {
			tx.rollback();
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage());
			throw new DBException(ex);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AcceptanceFormDTO> getAllAcceptanceForms() throws GeoGameException {
		List<AcceptanceFormDTO> forms = new ArrayList<AcceptanceFormDTO>();
		
		if(DevServiceImpl.resetFlag) {
			throw new AuthorizationException(null, "Game is resetting");
		}

		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			List<AcceptanceForm> allForms = (List<AcceptanceForm>)session.createCriteria(AcceptanceForm.class)
				.list();
			for(AcceptanceForm form : allForms) {
				forms.add(new AcceptanceFormDTO(form));
			}
			tx.commit();
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage());
			throw new DBException(ex);
		} 
		
		return forms;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateAcceptanceForms(List<AcceptanceFormDTO> updateForms) throws GeoGameException {
		
		if(DevServiceImpl.resetFlag) {
			throw new AuthorizationException(null, "Game is resetting");
		}

		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			List<AcceptanceForm> allForms = (List<AcceptanceForm>)session.createCriteria(AcceptanceForm.class)
				.list();
			for(AcceptanceForm form : allForms) {
				for(AcceptanceFormDTO formDTO : updateForms) {
					if(form.getId()==formDTO.getId()) {
						form.setActive(formDTO.isActive());
						form.setContent(formDTO.getContent());
						form.setOrder(formDTO.getOrder());
						break;
					}
				}
			}
			tx.commit();
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage());
			throw new DBException(ex);
		}
	}

	@Override
	public void deleteFormConfirmations(Long formId) throws GeoGameException {
		if(DevServiceImpl.resetFlag) {
			throw new AuthorizationException(null, "Game is resetting");
		}

		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			List<AcceptedForm> allAcceptedForms = (List<AcceptedForm>)session.createCriteria(AcceptedForm.class)
				.add(Restrictions.eq("acceptanceForm.id", formId))
				.list();
			for(AcceptedForm acceptedForm : allAcceptedForms) {
				session.delete(acceptedForm);
			}
			tx.commit();
		} catch(HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage());
			throw new DBException(ex);
		} 
	}
}