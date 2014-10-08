package edu.cmu.cs.cimds.geogame.client.services;

import com.google.gwt.user.client.rpc.RemoteService;

import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;

public interface AgentService extends RemoteService {
	public String getLoginCommand() throws GeoGameException;
}
