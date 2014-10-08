package edu.cmu.cs.cimds.geogame.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.cmu.cs.cimds.geogame.client.model.dto.GameStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.RoadDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;
import edu.cmu.cs.cimds.geogame.client.ui.WindowInformation;
import edu.cmu.cs.cimds.geogame.client.ui.map.MapUtil;


/**
 *
 * @author ajuarez
 */
public class GameInfo {
	
	public final boolean DEBUG_MODE = false;
	
	private static GameInfo instance;
	
	private boolean devMode;
	public long imagesTimeout = 30000;

	private UserDTO player;
	private boolean playerMoving;
	
	public com.google.gwt.user.client.Timer moveTimer;
	
	private LatLng mapPosition;
	private LocationDTO location;

	private List<String> messages = new ArrayList<String>();

	private Set<LocationDTO> mapLocations = new HashSet<LocationDTO>();
	private Set<RoadDTO> mapRoads = new HashSet<RoadDTO>();
	private List<UserDTO> scores = new ArrayList<UserDTO>();
	
//	public long endTime;
	private boolean gameStarted;
	private boolean gameFinished;
	private boolean needsRefresh;

	public boolean isGameStarted() { return gameStarted; }
	public void setGameStarted(boolean gameStarted) { this.gameStarted = gameStarted; }

	public boolean isGameFinished() { return gameFinished; }
	public void setGameFinished(boolean gameFinished) { this.gameFinished = gameFinished; }

	@SuppressWarnings("deprecation")
	Date zeroDate = new Date(2000,1,1,0,0,0);

	private GameInfo() {};

	public static GameInfo getInstance() {
		if(instance==null) {
			instance = new GameInfo();
		}
		return instance;
	}

	private long peekFormId;

	public boolean isNeedsRefresh() { return needsRefresh; }
	public void setNeedsRefresh(boolean needsRefresh) { this.needsRefresh = needsRefresh; }

	public boolean isPlayerMoving() { return playerMoving; }
	public void setPlayerMoving(boolean playerMoving) { this.playerMoving = playerMoving; }

	public UserDTO getPlayer() { return player; }
	public void setPlayer(UserDTO player) { this.player = player; }

//	public List<ItemDTO> getInventory() { return inventory; }
//	public void setInventory(List<ItemDTO> inventory) { this.inventory = inventory; }

	public List<String> getMessages() { return messages; }
	public void setMessages(List<String> messages) { this.messages = messages; }

	public LocationDTO getLocation() { return location; }
	public void setLocation(LocationDTO location) { this.location = location; }

	public LatLng getMapPosition() { return mapPosition; }
	public void setMapPosition(LatLng mapPosition) { this.mapPosition = mapPosition; }

	public Set<LocationDTO> getMapLocations() { return mapLocations; }
	public void setMapLocations(Set<LocationDTO> mapLocations) { this.mapLocations = mapLocations; }

	public Set<RoadDTO> getMapRoads() { return mapRoads; }
	public void setMapRoads(Set<RoadDTO> mapRoads) { this.mapRoads = mapRoads; }

	public List<UserDTO> getScores() { return scores; }
	public void setScores(List<UserDTO> scores) { this.scores = scores; }

	public void updateInformation(GameStruct gameInformation) {
		updatePlayerInformation(gameInformation.player);
		updateMapInformation(gameInformation.mapInformation);
		
//		WindowInformation.treasurePanel.log("inside" + this.gameStarted + " - " + gameInformation.gameStarted + this.gameFinished + " - " + gameInformation.gameFinished);
		if(!this.gameStarted && gameInformation.gameStarted && !gameInformation.gameFinished) {
			Date newDate = new Date(zeroDate.getTime()+gameInformation.timeRemaining);
//			MapUtil.refreshMap();
			//Window.alert("Game has started!\nTime remaining: " + new SimpleDateFormat("HH:mm:ss").format(newDate));
		}

		this.gameStarted = gameInformation.gameStarted;
		this.gameFinished = gameInformation.gameFinished;
//		this.endTime = new Date().getTime() + gameInformation.timeRemaining;
	}
	
	public void updatePlayerInformation(UserDTO player) {
		if(player!=null) {
			this.player.setCurrentLocation(player.getCurrentLocation());
			this.player.setCurrentRoad(player.getCurrentRoad());
			this.player.setCurrentRoadMovement(player.getCurrentRoadMovement());
			this.player.setItemsToCollect(player.getItemsToCollect());
//			for(ItemDTO item : player.getInventory()) {
//				if(!this.player.getInventory().contains(item)) {
//					this.player.getInventory().add(item);
//				}
//			}
			this.player.setInventory(player.getInventory());
			this.player.setForward(player.getForward());
			this.player.setLatitude(player.getLatitude());
			this.player.setLongitude(player.getLongitude());
//			this.player.setMapPosition(player.getMapPosition());
			this.player.setScore(player.getScore());
			this.player.setNeighbors(player.getNeighbors());

			this.player.setMoving(player.isMoving());
			this.location = this.player.getCurrentLocation();
			this.playerMoving = this.player.isMoving();
			this.player.setAMTVisitor(player.getAMTVisitor());
		}
	}

	public void updateMapInformation(MapInformation mapInformation) {
		//Adds all locations returned by the server that the player did not already have
		if(mapInformation!=null) {
			for(LocationDTO location : mapInformation.locations) {
				if(!this.mapLocations.contains(location)) {
					this.mapLocations.add(location);
				}
				/*else {
					//Removes the old object and inserts the new one
					this.mapLocations.remove(location);
					this.mapLocations.add(location);
				}*/
			}
		}
		//Adds all roads returned by the server that the player did not already have
		if(mapInformation!=null) {
			for(RoadDTO road : mapInformation.roads) {
				if(!this.mapRoads.contains(road)) {
					this.mapRoads.add(road);
				} 
			/*	else {
					//Removes the old object and inserts the new one
					this.mapRoads.remove(road);
					this.mapRoads.add(road);
				}*/
//				if(!this.mapLocations.contains(road.getLocation1())) {
//					this.mapLocations.add(road.getLocation1());
//				}
//				if(!this.mapLocations.contains(road.getLocation2())) {
//					this.mapLocations.add(road.getLocation2());
//				}
			}
		}
	}
	
	public void reset() {
		this.player = null;
		this.mapPosition = null;
		this.location = null;
		this.messages = new ArrayList<String>();
//		this.inventory = new ArrayList<ItemDTO>();
		
		this.mapLocations.clear();
		this.mapRoads.clear();
		this.gameStarted=false;
		this.gameFinished=false;
		this.peekFormId=0;
	}

	public void refreshAll(boolean queryServer) {
		if(queryServer) {
			//Collection updated Map Information from server
			AsyncCallback<GameStruct> callback = new AsyncCallback<GameStruct>() {
				public void onFailure(Throwable caught) {
					//***Window.alert("Error while calling Game Service: " + caught.getMessage());
				}
	
				public void onSuccess(GameStruct gameInformation) {
					GameInfo.getInstance().updateInformation(gameInformation);
					//Come back to refreshAll with updated Information.
					refreshAll(false);
				}
			};
			GameServices.gameService.getGameInformationNew(GameInfo.getInstance().getPlayer(),callback);
		} else {
			WindowInformation.locationPanel.setPeekFormId(this.peekFormId);
			if(GameInfo.getInstance().isGameStarted()) {
				MapUtil.refreshMap();
				WindowInformation.locationPanel.refresh();
				WindowInformation.treasurePanel.refresh();
			} else {
				MapUtil.clearMap();
				MapUtil.setStandbyMap();
			}
		}
	}
	
	public boolean isDevMode() { return devMode; }
	public void setDevMode(boolean devMode) { this.devMode = devMode; }

	public void log(String text) {
		WindowInformation.treasurePanel.log(text);
	}

	public long getPeekFormId() { return peekFormId; }
	public void setPeekFormId(long peekFormId) { this.peekFormId = peekFormId; }
}