package edu.cmu.cs.cimds.geogame.client;

import java.util.ArrayList;
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
public class GameInformation {
	private static GameInformation instance;
	
	private boolean devMode;

	private UserDTO player;
	private LatLng mapPosition;
	private LocationDTO location;

	private List<String> messages = new ArrayList<String>();

	private Set<LocationDTO> mapLocations = new HashSet<LocationDTO>();
	private Set<RoadDTO> mapRoads = new HashSet<RoadDTO>();
	
	private GameInformation() {};

	public static GameInformation getInstance() {
		if(instance==null) {
			instance = new GameInformation();
		}
		return instance;
	}

	public UserDTO getPlayer() { return player; }
	public void setUser(UserDTO player) { this.player = player; }

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

	public void updateInformation(GameStruct gameInformation) {
		updatePlayerInformation(gameInformation.player);
		updateMapInformation(gameInformation.mapInformation);
	}
	
	public void updatePlayerInformation(UserDTO player) {
		if(player!=null) {
			this.player.setCurrentLocation(player.getCurrentLocation());
			this.player.setCurrentRoad(player.getCurrentRoad());
			
//			for(ItemDTO item : player.getInventory()) {
//				if(!this.player.getInventory().contains(item)) {
//					this.player.getInventory().add(item);
//				}
//			}
			this.player.setInventory(player.getInventory());
			this.player.setForward(player.getForward());
			this.player.setPosition(player.getPosition());
			this.player.setScore(player.getScore());
			this.player.setNeighbors(player.getNeighbors());
		}
		this.location = this.player.getCurrentLocation();
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
	}

	public void refreshAll(boolean queryServer) {
		if(queryServer) {
			//Collection updated Map Information from server
			AsyncCallback<GameStruct> callback = new AsyncCallback<GameStruct>() {
				public void onFailure(Throwable caught) {
					//***Window.alert("Error while calling Game Service: " + caught.getMessage());
				}
	
				public void onSuccess(GameStruct gameInformation) {
					GameInformation.getInstance().updateInformation(gameInformation);
					//Come back to refreshAll with updated Information.
					refreshAll(false);
				}
			};
			GameServices.gameService.getGameInformation(GameInformation.getInstance().getPlayer(),callback);
		} else {
			MapUtil.refreshMap();
			WindowInformation.locationPanel.refresh();
			WindowInformation.treasurePanel.refresh();
		}
	}
	
	public boolean isDevMode() { return devMode; }
	public void setDevMode(boolean devMode) { this.devMode = devMode; }

	public void log(String text) {
		WindowInformation.treasurePanel.log(text);
	}
}