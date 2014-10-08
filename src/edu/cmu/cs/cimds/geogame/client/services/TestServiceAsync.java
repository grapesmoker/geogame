package edu.cmu.cs.cimds.geogame.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.lang.String;

public interface TestServiceAsync {
	public void getNextTestPlayer(AsyncCallback<Integer> testLoginCallback);
	public void log(String message, AsyncCallback<Void> callback);
}
