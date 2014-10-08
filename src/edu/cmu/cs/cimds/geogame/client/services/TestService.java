package edu.cmu.cs.cimds.geogame.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;

@RemoteServiceRelativePath("TestService")
public interface TestService extends RemoteService {
	public Integer getNextTestPlayer() throws GeoGameException;
	public void log (String message);
}
