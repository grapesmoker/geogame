package edu.cmu.cs.cimds.geogame.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.lang.String;

public interface AgentServiceAsync {
	public void getLoginCommand(AsyncCallback<String> callback);
}
