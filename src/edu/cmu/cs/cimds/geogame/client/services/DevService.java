/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;
import edu.cmu.cs.cimds.geogame.client.model.dto.ServerSettingsStructDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;


@RemoteServiceRelativePath("DevService")
public interface DevService extends RemoteService {
	public void createLocation(UserDTO player, String locationName, double latitude, double longitude) throws GeoGameException;
	public void createRoad(UserDTO player, String name, long location1Id, long location2Id, String roadPointsString) throws GeoGameException;
	//public void resetAllDB(UserDTO player, boolean rebuildUserNetwork, boolean rebuildRoadNetwork, boolean newItemAssignment, NetworkType networkType, Double graphDensity, Double minRoadTime, Double maxRoadTime) throws GeoGameException;
	public void resetAllDB(ServerSettingsStructDTO serverSettings) throws GeoGameException;
}