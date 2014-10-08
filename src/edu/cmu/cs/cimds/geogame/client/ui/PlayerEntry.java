package edu.cmu.cs.cimds.geogame.client.ui;

import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

public class PlayerEntry {
	
	private String playerName = "";
	private String playerLocation = "";
	private int collectedItems = 0;
	private int numNeighbors = 0;
	private String loggedIn = "";
	private int score = 0;
	private String status = "";
	private String goalItem = "";
	private Boolean checked = false;
	
	private long id;
	
	private UserDTO player;
	
	public UserDTO getPlayer() {
		return player;
	}
	
	public void setPlayer(UserDTO player) {
		this.player = player;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void playerEntry() {	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerLocation() {
		return playerLocation;
	}

	public void setPlayerLocation(String playerLocation) {
		this.playerLocation = playerLocation;
	}

	public int getCollectedItems() {
		return collectedItems;
	}

	public void setCollectedItems(int collectedItems) {
		this.collectedItems = collectedItems;
	}

	public int getNumNeighbors() {
		return numNeighbors;
	}

	public void setNumNeighbors(int numNeighbors) {
		this.numNeighbors = numNeighbors;
	}

	public String isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(String loggedIn) {
		this.loggedIn = loggedIn;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGoalItem() {
		return goalItem;
	}

	public void setGoalItem(String goalItem) {
		this.goalItem = goalItem;
	}
	
	public Boolean isChecked() {
		return checked;
	}
	
	public void setChecked(Boolean state) {
		this.checked = state;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != this.getClass()) {
			return false;
		}
		
		return true;
	}
	
}
