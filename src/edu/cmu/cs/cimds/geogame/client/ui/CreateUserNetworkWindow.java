package edu.cmu.cs.cimds.geogame.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;

/**
 * Client entry point for the LocationInformationWindow.
 *
 * @author Antonio Juarez <ajuarez@andrew.cmu.edu>
 */
public class CreateUserNetworkWindow extends DialogBox {

	Grid widgetGrid = new Grid();
	Grid pairsGrid = new Grid(1,2);
	
	Map<CheckBox,UserDTO> checkboxToUserMap = new HashMap<CheckBox,UserDTO>();
	
	public CreateUserNetworkWindow() {
		super(false,true);
		
		VerticalPanel containerPanel = new VerticalPanel();
		
		containerPanel.add(widgetGrid);
		Button submitButton = new Button("Create Network");
		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				List<UserDTO> playersInNetwork = new ArrayList<UserDTO>();
				for(Map.Entry<CheckBox, UserDTO> entry : checkboxToUserMap.entrySet()) {
					if(entry.getKey().getValue()) {
						playersInNetwork.add(entry.getValue());
					}
				}
				GameServices.gameService.createUserNetwork(playersInNetwork, 0.03, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						Window.alert("Failure! - 1 - " + caught.getMessage());
						Window.alert("error occurred in createUserNetwork");
					}
					@Override
					public void onSuccess(Void result) {
						InfoWindow alert = new InfoWindow(200, 100, "Success!");
						alert.ensureDebugId("alert");
						alert.center();
						CreateUserNetworkWindow.this.refresh();
					}
				});
			}
		});
		containerPanel.add(submitButton);

		Button cancelButton = new Button("Cancel");
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CreateUserNetworkWindow.this.hide();
			}
		});
		containerPanel.add(cancelButton);

		containerPanel.add(pairsGrid);
		
		this.setWidget(containerPanel);
	}

	public void clearContent() {
		widgetGrid.clear();
		widgetGrid.resize(0,0);
		pairsGrid.resize(1,2);
	}
	
	public void refresh() {
		this.clearContent();
		AsyncCallback<List<UserDTO>> callback = new AsyncCallback<List<UserDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<UserDTO> players) {
				widgetGrid.resize(players.size()+1, 2);
				pairsGrid.resize(1,2);
				
				int i=0;
				int j=0;
				checkboxToUserMap.clear();
				for(UserDTO player : players) {
					CheckBox checkbox = new CheckBox();
					widgetGrid.setWidget(i,0,checkbox);
					widgetGrid.setText(i,1,player.getUsername());
					checkboxToUserMap.put(checkbox, player);
					i=i+1;
					
					pairsGrid.resizeRows(pairsGrid.getRowCount() + player.getNeighbors().size());
					for(UserDTO neighbor : player.getNeighbors()) {
						pairsGrid.setText(j, 0, player.getUsername());
						pairsGrid.setText(j, 1, neighbor.getUsername());
						j=j+1;
					}
				}
			}
		};
		GameServices.gameService.getAllPlayers(callback);
	}
}