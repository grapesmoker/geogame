package edu.cmu.cs.cimds.geogame.server;

import org.hibernate.Session;


public class PersistenceManager {

	public static Session getSession() {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		return session;
	}
	
//	public static Connection getConnection() throws Exception {
//		String url = "jdbc:mysql://localhost:3306/";
//		String db = "geogame";
//		String driver = "com.mysql.jdbc.Driver"; 
//		String user = "root";
//		String password = "root";
//		
//		try{
//			Class.forName(driver).newInstance();
//			Connection conn = DriverManager.getConnection(url+db, user, password);
//			return conn;
//		} catch(Exception e) {
//			System.err.println("Unable to connect - " + e.getMessage());
//			e.printStackTrace();
//			throw e;
//		}
//	}
}