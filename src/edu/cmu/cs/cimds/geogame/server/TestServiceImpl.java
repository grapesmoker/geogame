package edu.cmu.cs.cimds.geogame.server;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.cmu.cs.cimds.geogame.client.exception.DBException;
import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;
import edu.cmu.cs.cimds.geogame.client.model.db.User;
import edu.cmu.cs.cimds.geogame.client.services.TestService;

public class TestServiceImpl extends RemoteServiceServlet implements TestService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1877346259254663593L;
	private static Logger logger = Logger.getLogger(TestServiceImpl.class);
	
	public Integer getNextTestPlayer() throws GeoGameException {
		// returns the number of test players currently logged in.
		Integer numTestUsers = 0;
		
		Session session = PersistenceManager.getSession();
		Transaction tx = session.beginTransaction();
		try {
			numTestUsers = (Integer)session.createCriteria(User.class)
				.add(Restrictions.like("username", "TestUser%"))
				.add(Restrictions.eq("loggedIn", true))
				.setProjection(Projections.rowCount()).uniqueResult();
			
			tx.commit();
		} catch (HibernateException ex) {
			tx.rollback();
			logger.error(ex.getMessage(), ex);
			throw new DBException(ex);
		}

		return numTestUsers;
	}
	
	public void log (String message) {
		//logger.info(message);
	}
}
