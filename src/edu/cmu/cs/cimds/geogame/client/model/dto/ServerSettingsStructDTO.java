package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;

import javax.persistence.Column;

import edu.cmu.cs.cimds.geogame.client.NetworkType;
import edu.cmu.cs.cimds.geogame.client.model.db.ServerSettingsStruct;

public class ServerSettingsStructDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5088735694658039351L;

	
	long GAME_DURATION_DEFAULT = 10*60*1000; //10 minutes
	long gameDuration;
	long gameInterval;
	int MINIMUM_DURATION = 5000;
	int PENALTY_DURATION = 30000;
	double USER_GRAPH_DENSITY = 0.03;
	double AVG_NETWORK_DEGREE = 3.3;
	
	boolean rebuildUserNetwork;
	boolean rebuildRoadNetwork;
	boolean newItemAssignment;
	boolean commAllowed;
	boolean periodicGame;
	
	NetworkType networkType;
	Double graphDensity;
	Double minRoadTime;
	Double maxRoadTime;
	
	public ServerSettingsStructDTO () { }
	
	public ServerSettingsStructDTO (ServerSettingsStruct serverSettings) {
		this.setGameDuration(serverSettings.getGameDuration());
		this.setGraphDensity(serverSettings.getGraphDensity());
		this.setMaxRoadTime(serverSettings.getMaxRoadTime());
		this.setMinRoadTime(serverSettings.getMinRoadTime());
		this.setNetworkType(serverSettings.getNetworkType());
		this.setNewItemAssignment(serverSettings.isNewItemAssignment());
		this.setRebuildRoadNetwork(serverSettings.isRebuildRoadNetwork());
		this.setRebuildUserNetwork(serverSettings.isRebuildUserNetwork());
		this.setCommAllowed(serverSettings.getCommAllowed());
		this.setGameInterval(serverSettings.getGameInterval());
		this.setPeriodicGame(serverSettings.isPeriodicGame());
	}
	
	public void updateServerSettings (ServerSettingsStructDTO serverSettings) {
		this.setGameDuration(serverSettings.getGameDuration());
		this.setGraphDensity(serverSettings.getGraphDensity());
		this.setMaxRoadTime(serverSettings.getMaxRoadTime());
		this.setMinRoadTime(serverSettings.getMinRoadTime());
		this.setNetworkType(serverSettings.getNetworkType());
		this.setNewItemAssignment(serverSettings.isNewItemAssignment());
		this.setRebuildRoadNetwork(serverSettings.isRebuildRoadNetwork());
		this.setRebuildUserNetwork(serverSettings.isRebuildUserNetwork());
		this.setCommAllowed(serverSettings.getCommAllowed());
		this.setGameInterval(serverSettings.getGameInterval());
		this.setPeriodicGame(serverSettings.isPeriodicGame());
	}
	
	public void setPeriodicGame(boolean periodicGame) {
		this.periodicGame = periodicGame;
	}
	
	public boolean isPeriodicGame() {
		return periodicGame;
	}
	
	public void setGameInterval(long gameInterval) {
		this.gameInterval = gameInterval;
	}
	
	public Long getGameInterval() {
		return gameInterval;
	}
	
	public Long getGameDuration() {
		return gameDuration;
	}
	
	public void setGameDuration(long gameDuration) {
		this.gameDuration = gameDuration;
	}

	public boolean isRebuildUserNetwork() {
		return rebuildUserNetwork;
	}

	public void setRebuildUserNetwork(boolean rebuildUserNetwork) {
		this.rebuildUserNetwork = rebuildUserNetwork;
	}

	public boolean isRebuildRoadNetwork() {
		return rebuildRoadNetwork;
	}

	public void setRebuildRoadNetwork(boolean rebuildRoadNetwork) {
		this.rebuildRoadNetwork = rebuildRoadNetwork;
	}

	public boolean isNewItemAssignment() {
		return newItemAssignment;
	}

	public void setNewItemAssignment(boolean newItemAssignment) {
		this.newItemAssignment = newItemAssignment;
	}

	public NetworkType getNetworkType() {
		return networkType;
	}

	public void setNetworkType(NetworkType networkType) {
		this.networkType = networkType;
	}

	public Double getGraphDensity() {
		return graphDensity;
	}

	public void setGraphDensity(Double graphDensity) {
		this.graphDensity = graphDensity;
	}

	public Double getMinRoadTime() {
		return minRoadTime;
	}

	public void setMinRoadTime(Double minRoadTime) {
		this.minRoadTime = minRoadTime;
	}

	public Double getMaxRoadTime() {
		return maxRoadTime;
	}

	public void setMaxRoadTime(Double maxRoadTime) {
		this.maxRoadTime = maxRoadTime;
	}
	
	public boolean getCommAllowed() {
		return commAllowed;
	}
	
	public void setCommAllowed(boolean commAllowed) {
		this.commAllowed = commAllowed;
	}
	
}
