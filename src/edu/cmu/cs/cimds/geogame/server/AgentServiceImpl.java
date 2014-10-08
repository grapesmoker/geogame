package edu.cmu.cs.cimds.geogame.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;
import edu.cmu.cs.cimds.geogame.client.services.AgentService;

public class AgentServiceImpl extends RemoteServiceServlet implements AgentService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6117382563520338484L;

	@Override
	public String getLoginCommand() throws GeoGameException {
		// TODO Auto-generated method stub
		return GameServiceImpl.getNextLoginCommand();
	}
}
