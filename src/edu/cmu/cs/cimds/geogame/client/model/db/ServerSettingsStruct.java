package edu.cmu.cs.cimds.geogame.client.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import edu.cmu.cs.cimds.geogame.client.NetworkType;

@Entity
@Table(name="server_settings")
public class ServerSettingsStruct extends PersistentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7966697191515629792L;

	
		
	long GAME_DURATION_DEFAULT = 10*60*1000; //10 minutes
	long gameDuration;
	int MINIMUM_DURATION = 5000;
	int PENALTY_DURATION = 30000;
	double USER_GRAPH_DENSITY = 0.03;
	double AVG_NETWORK_DEGREE = 3.3;
	
	boolean rebuildUserNetwork;
	boolean rebuildRoadNetwork;
	boolean newItemAssignment;
	boolean commAllowed;
	NetworkType networkType;
	Double graphDensity;
	Double minRoadTime;
	Double maxRoadTime;
	
	boolean periodicGame = false;
	// gameInterval must be strictly bigger than gameDuration, preferably about 5 min. longer
	Long gameInterval;
	
	@Column(name="periodic_game", nullable=false)
	public Boolean isPeriodicGame() {
		return periodicGame;
	}
	
	public void setPeriodicGame(boolean periodicGame) {
		this.periodicGame = periodicGame;
	}
	
	@Column(name="game_interval", nullable=false)
	public Long getGameInterval() {
		return gameInterval;
	}
	
	public void setGameInterval(Long gameInterval) {
		this.gameInterval = gameInterval;
	}
	
	@Column(name="game_duration", nullable=false)
	public Long getGameDuration() {
		return gameDuration;
	}
	
	public void setGameDuration(long gameDuration) {
		this.gameDuration = gameDuration;
	}

	@Column(name="rebuild_user_network", nullable=false)
	public boolean isRebuildUserNetwork() {
		return rebuildUserNetwork;
	}

	public void setRebuildUserNetwork(boolean rebuildUserNetwork) {
		this.rebuildUserNetwork = rebuildUserNetwork;
	}

	@Column(name="rebuild_road_network", nullable=false)
	public boolean isRebuildRoadNetwork() {
		return rebuildRoadNetwork;
	}

	public void setRebuildRoadNetwork(boolean rebuildRoadNetwork) {
		this.rebuildRoadNetwork = rebuildRoadNetwork;
	}

	@Column(name="reassign_items", nullable=false)
	public boolean isNewItemAssignment() {
		return newItemAssignment;
	}

	public void setNewItemAssignment(boolean newItemAssignment) {
		this.newItemAssignment = newItemAssignment;
	}

	@Column(name="network_type", nullable=false)
	public NetworkType getNetworkType() {
		return networkType;
	}

	public void setNetworkType(NetworkType networkType) {
		this.networkType = networkType;
	}

	@Column(name="graph_density", nullable=false)
	public Double getGraphDensity() {
		return graphDensity;
	}

	public void setGraphDensity(Double graphDensity) {
		this.graphDensity = graphDensity;
	}

	@Column(name="min_road_time", nullable=false)
	public Double getMinRoadTime() {
		return minRoadTime;
	}

	public void setMinRoadTime(Double minRoadTime) {
		this.minRoadTime = minRoadTime;
	}

	@Column(name="max_road_time", nullable=false)
	public Double getMaxRoadTime() {
		return maxRoadTime;
	}

	public void setMaxRoadTime(Double maxRoadTime) {
		this.maxRoadTime = maxRoadTime;
	}

	@Column(name="comm_allowed", nullable=false)
	public boolean getCommAllowed() {
		return commAllowed;
	}
	
	public void setCommAllowed(boolean commAllowed) {
		this.commAllowed = commAllowed;
	}
}
