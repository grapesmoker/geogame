package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.NetworkType;
import edu.cmu.cs.cimds.geogame.client.ServerSettingsResult;
import edu.cmu.cs.cimds.geogame.client.model.dto.ServerSettingsStructDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;

public class ServerSettingsWindow extends DialogBox {
	
	VerticalPanel containerPanel = new VerticalPanel();
	Grid grid = new Grid(12, 2);
	Button okButton = new Button("OK");
	Button cancelButton = new Button("Cancel");
	ListBox networkTypeList = new ListBox();
	
	public ServerSettingsStructDTO serverSettings = new ServerSettingsStructDTO();
	
	public ServerSettingsWindow() {
		super(false, true);
	}

	public void init() {
		
		networkTypeList.addItem(NetworkType.SMALL_WORLD.name(), NetworkType.SMALL_WORLD.toString());
		networkTypeList.addItem(NetworkType.RANDOM.name(), NetworkType.RANDOM.toString());
		networkTypeList.addItem(NetworkType.LINE.name(), NetworkType.LINE.toString());
		networkTypeList.addItem(NetworkType.RING.name(), NetworkType.RING.toString());
		networkTypeList.addItem(NetworkType.GRID.name(), NetworkType.GRID.toString());
		networkTypeList.addItem(NetworkType.NULL_NETWORK.name(), NetworkType.NULL_NETWORK.toString());
	
		final TextBox minTravelTime = new TextBox();
		final TextBox maxTravelTime = new TextBox();
		final TextBox graphDensity = new TextBox();
		final TextBox gameDuration = new TextBox();
		final TextBox gameInterval = new TextBox();
		
		final CheckBox newItemAssignment = new CheckBox();
		final CheckBox newUserNetwork = new CheckBox();
		final CheckBox newRoadNetwork = new CheckBox();
		final CheckBox commAllowed = new CheckBox();
		final CheckBox periodicGame = new CheckBox();
		
		networkTypeList.ensureDebugId("networkTypeList");
		minTravelTime.ensureDebugId("minTravelTime");
		maxTravelTime.ensureDebugId("maxTravelTime");
		graphDensity.ensureDebugId("graphDensity");
		gameDuration.ensureDebugId("gameDuration");
		newItemAssignment.ensureDebugId("newItemAssignment");
		newUserNetwork.ensureDebugId("newUserNetwork");
		newRoadNetwork.ensureDebugId("newRoadNetwork");
		okButton.ensureDebugId("settingsOkButton");
		
		minTravelTime.setText("5");
		maxTravelTime.setText("20");
		graphDensity.setText("3.3");
	
		grid.setWidget(0, 0, new Label("Network topology: "));
		grid.setWidget(1, 0, new Label("Minimum travel time: "));
		grid.setWidget(2, 0, new Label("Maximum travel time: "));
		grid.setWidget(3, 0, new Label("Graph density: "));
		grid.setWidget(4, 0, new Label("Game duration: "));
		grid.setWidget(5, 0, new Label("Reassign items: "));
		grid.setWidget(6, 0, new Label("Rebuild user network: "));
		grid.setWidget(7, 0, new Label("Rebuild road network: "));
		grid.setWidget(8, 0, new Label("Communication allowed: "));
		grid.setWidget(9, 0, new Label("Periodic game?: "));
		grid.setWidget(10, 0, new Label("Game interval: "));
		
		grid.setWidget(0, 1, networkTypeList);
		grid.setWidget(1, 1, minTravelTime);
		grid.setWidget(2, 1, maxTravelTime);
		grid.setWidget(3, 1, graphDensity);
		grid.setWidget(4, 1, gameDuration);
		grid.setWidget(5, 1, newItemAssignment);
		grid.setWidget(6, 1, newUserNetwork);
		grid.setWidget(7, 1, newRoadNetwork);
		grid.setWidget(8, 1, commAllowed);
		grid.setWidget(9, 1, periodicGame);
		grid.setWidget(10, 1, gameInterval);
		
		grid.setWidget(11, 1, okButton);		
		
		AsyncCallback<ServerSettingsStructDTO> callback = new AsyncCallback<ServerSettingsStructDTO>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				InfoWindow alert = new InfoWindow(250, 100, "Could not fetch current server settings!");
				alert.center();
			}

			@Override
			public void onSuccess(ServerSettingsStructDTO result) {
				// TODO Auto-generated method stub
				serverSettings.updateServerSettings(result);
				networkTypeList.setItemSelected(serverSettings.getNetworkType().ordinal(), true);
				minTravelTime.setText(String.valueOf(serverSettings.getMinRoadTime() / 1000));
				maxTravelTime.setText(String.valueOf(serverSettings.getMaxRoadTime() / 1000));
				graphDensity.setText(String.valueOf(serverSettings.getGraphDensity()));
				gameDuration.setText(String.valueOf(serverSettings.getGameDuration() / 60000));
				newItemAssignment.setValue(serverSettings.isNewItemAssignment());
				newUserNetwork.setValue(serverSettings.isRebuildUserNetwork());
				newRoadNetwork.setValue(serverSettings.isRebuildRoadNetwork());
				commAllowed.setValue(serverSettings.getCommAllowed());
				periodicGame.setValue(serverSettings.isPeriodicGame());
				gameInterval.setValue(String.valueOf(serverSettings.getGameInterval() / 60000));
			}
			
		};
		GameServices.gameService.getServerSettings(callback);
		
		okButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
				serverSettings.setGameDuration(60000 * Long.valueOf(gameDuration.getText()));
				serverSettings.setGraphDensity(Double.valueOf(graphDensity.getText()));
				serverSettings.setMaxRoadTime(1000 * Double.valueOf(maxTravelTime.getText()));
				serverSettings.setMinRoadTime(1000 * Double.valueOf(minTravelTime.getText()));
				serverSettings.setNetworkType(NetworkType.valueOf(networkTypeList.getValue(networkTypeList.getSelectedIndex())));
				serverSettings.setNewItemAssignment(Boolean.valueOf(newItemAssignment.getValue()));
				serverSettings.setRebuildUserNetwork(Boolean.valueOf(newUserNetwork.getValue()));
				serverSettings.setCommAllowed(Boolean.valueOf(commAllowed.getValue()));
				serverSettings.setPeriodicGame(Boolean.valueOf(periodicGame.getValue()));
				serverSettings.setGameInterval(60000 * Long.valueOf(gameInterval.getText()));
				
				AsyncCallback<ServerSettingsResult> callback = new AsyncCallback<ServerSettingsResult>() {

					@Override
					public void onFailure(Throwable caught) {
						InfoWindow alert = new InfoWindow(250, 100, "Could not send settings to server!");
						ServerSettingsWindow.this.hide();	
						alert.center();
					}

					@Override
					public void onSuccess(ServerSettingsResult result) {
						if (result.isSuccess()) {
							ServerSettingsWindow.this.hide();
							InfoWindow alert = new InfoWindow(250, 100, "Server settings transmitted and set!");
							alert.center();
						}
						else {
							ServerSettingsWindow.this.hide();
							InfoWindow alert = new InfoWindow(250, 100, "Server settings were transmitted but could not be set! Game state is undefined!");
							alert.center();
						}
					}
					
				};
				GameServices.gameService.sendServerSettings(serverSettings, callback);
					
			}
		});
		
		containerPanel.add(grid);
		this.setWidget(containerPanel);
	}
}
