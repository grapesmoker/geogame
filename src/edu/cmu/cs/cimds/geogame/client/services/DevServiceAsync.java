/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.cmu.cs.cimds.geogame.client.model.dto.ServerSettingsStructDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

public interface DevServiceAsync {
	public void createLocation(UserDTO player, String locationName, double latitude, double longitude, AsyncCallback<Void> asyncCallback);
	public void createRoad(UserDTO player, String name, long location1Id, long location2Id, String roadPointsString, AsyncCallback<Void> asyncCallback);
	public void resetAllDB(ServerSettingsStructDTO serverSettings, AsyncCallback<Void> asyncCallback);
}