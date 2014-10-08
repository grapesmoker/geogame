package edu.cmu.cs.cimds.geogame.client.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.ActionResult;
import edu.cmu.cs.cimds.geogame.client.GameInfo;
import edu.cmu.cs.cimds.geogame.client.MoveResult;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.RoadDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;

public class LocationInfoPanel extends VerticalPanel {

	private static final double probOfItemShowing = 1.0;
	
	VerticalPanel devPanel = new VerticalPanel();
	CheckBox createModeCheckBox = new CheckBox();
	CheckBox roadModeCheckBox = new CheckBox();
	VerticalPanel hiddenPanel = new VerticalPanel();
	
	Label locationNameLabel = new Label();
//	TabPanel locationTabPanel = new TabPanel();
	
	VerticalPanel itemsInLocationPanel = new VerticalPanel();
	List<GetItemButton> getItemButtons = new ArrayList<GetItemButton>();
	
//	Button showInfoButton = new Button("Market");
//	ListBox playersInLocationBox = new ListBox();
	
//	CheckBox showTownNamesCheckBox = new CheckBox("Show Town Names");
//	CheckBox showRoadNamesCheckBox = new CheckBox("Show Road Names");
	private long peekFormId;
	Button peekFormButton = new Button("See instructions");
	
	public boolean isShowTownLabels() {
//		return showTownNamesCheckBox.getValue();
		return true;
	}
	
	public boolean isShowRoadLabels() {
		return false;
//		return showRoadNamesCheckBox.getValue();		
	}

	public void clearContent() {
		this.itemsInLocationPanel.clear();
		this.getItemButtons.clear();
//		this.playersInLocationBox.clear();
		if(devPanel.getParent()==this) {
			this.remove(devPanel);
		}
		this.createModeCheckBox.setValue(false);
		this.roadModeCheckBox.setValue(false);
	}

//	public void setNoLocation() {
//		this.clearContent();
//		this.locationNameLabel.setText("In Motion...");
//	}

	public void refresh() {
		LocationDTO location;
		this.clearContent();
		if(GameInfo.getInstance().isPlayerMoving()) {
			RoadDTO road = GameInfo.getInstance().getPlayer().getCurrentRoad();
			if(GameInfo.getInstance().getPlayer().getForward()) {
				location = road.getLocation1();
			} else {
				location = road.getLocation2();
			}
			this.locationNameLabel.setText("In Motion...");
//			this.setNoLocation();
//			return;
		} else {
			location = GameInfo.getInstance().getPlayer().getCurrentLocation();
			this.locationNameLabel.setText(/*"You are in:\n" + */location.getName());
		}
		
		this.locationNameLabel.setWordWrap(true);
		this.locationNameLabel.setStyleName("largeText15");
		
		for(final ItemDTO item : location.getItems()) {
			//Simulates probability of an item appearing in a location or not at any given time.
			if(Random.nextDouble()>=probOfItemShowing) {
				continue;
			}
			
			HorizontalPanel hp = new HorizontalPanel();
			
			Image image = new Image(item.getItemType().getIconFilename());
			image.ensureDebugId("Icon-" + item.getItemType().getName());
			image.setWidth(WindowInformation.imageWidth);
			
			final AsyncCallback<ActionResult> callback = new AsyncCallback<ActionResult>() {
				public void onFailure(Throwable caught) {
					GameInfo.getInstance().log("Purchase failed! " + caught.getMessage());
					//***Window.alert("Error while calling Game Service: " + caught.getMessage());
				}

				public void onSuccess(ActionResult actionResult) {
					GameInfo.getInstance().log(actionResult.getMessage());
					GameInfo.getInstance().updateInformation(actionResult.getGameInformation());
					GameInfo.getInstance().refreshAll(false);
//					Window.alert("Item purchased. Your gold is now " + GameInformation.getInstance().getPlayer().getScore());
				}
			};
			GetItemButton buyButton = new GetItemButton("Get");
			buyButton.ensureDebugId("Button-" + item.getItemType().getName());
			buyButton.setButtonName(item.getItemType().getName());
			buyButton.setButtonItem(item);
			buyButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					List<ItemTypeDTO> goalItemsSet = new ArrayList<ItemTypeDTO>(GameInfo.getInstance().getPlayer().getItemsToCollect());
					for(ItemDTO item : GameInfo.getInstance().getPlayer().getInventory()) {
						goalItemsSet.remove(item);
					}
					
					if(!goalItemsSet.contains(item.getItemType())) {
						Window.alert("You do not need this item!");
					} else {
						GameServices.gameService.takeItemNew(GameInfo.getInstance().getPlayer(), item.getId(), callback);
						GameInfo.getInstance().setNeedsRefresh(true);
					}
				}
			});
			// getItemButtons is a list that tracks all the "get" buttons that exist
			// and creates a mechanism for exposing them to artificial "clicks"
			// -JV 02/14/2011
			getItemButtons.add(buyButton);

			//			image.addClickHandler(new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					if(GameInformation.getInstance().getPlayer().getScore()<item.getPrice()) {
//						Window.alert("Not enough gold!");
//					} else {
//						GameServices.gameService.takeItem(GameInformation.getInstance().getPlayer(), item.getId(), callback);
//					}
//				}
//			});
			
			
//			Label descriptionLabel = new Label(item.getItemType().getName() + ": " + item.getPrice() + "G");
			//Label descriptionLabel = new Label(item.getItemType().getName());
			//descriptionLabel.ensureDebugId("ItemLabel-" + item.getItemType().getName());
			
			VerticalPanel itemPanel = new VerticalPanel();
			itemPanel.setHorizontalAlignment(ALIGN_LEFT);
			itemPanel.add(image);
			//itemPanel.add(descriptionLabel);
			itemPanel.setPixelSize(100, 100);

			hp.add(itemPanel);
			hp.add(buyButton);
			
			this.itemsInLocationPanel.add(hp);
			
			if(peekFormId==0) {
				peekFormButton.setVisible(false);
			} else {
				peekFormButton.setVisible(true);
			}
		}
		
		Set<RoadDTO> allRoads = GameInfo.getInstance().getMapRoads();
		List<Long> destinationIds = new ArrayList<Long>();
		
		LocationDTO currentLocation = GameInfo.getInstance().getLocation();
		
		for(RoadDTO road : allRoads) {
			if(road.getLocation1().getId() == currentLocation.getId()) {
				destinationIds.add(road.getLocation2().getId());				
			} else if(road.getLocation2().getId() == currentLocation.getId()) {
				destinationIds.add(road.getLocation1().getId());
			}
		}
		
		WindowInformation.locationPanel.hiddenPanel.clear();
		
		for (final Long destId : destinationIds) {	
			Label hiddenLoc = new Label();
			hiddenLoc.ensureDebugId("LocID-" + destId);
			Button moveToLocationButton = new Button();
			moveToLocationButton.setText("Move to destId " + destId);
			
			if (GameInfo.getInstance().DEBUG_MODE) {
				hiddenLoc.setVisible(false);
				moveToLocationButton.setVisible(true);
			}
			else {
				moveToLocationButton.setVisible(false);
				hiddenLoc.setVisible(false);
			}
			moveToLocationButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					moveToLocation(destId);
				}
			});
			
			moveToLocationButton.ensureDebugId("ButtonLocId-" + destId);
			hiddenPanel.add(hiddenLoc);
			hiddenPanel.add(moveToLocationButton);
		}
		WindowInformation.locationPanel.add(hiddenPanel);

//		for(String player : location.getPlayers()) {
//			if(!GameInfo.getInstance().getPlayer().getUsername().equals(player)) {
//				this.playersInLocationBox.addItem(player, player);
//			}
//		}
		
//		showInfoButton.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				openLocationDialog(GameInformation.getInstance().getLocation());
////				Window.open("LocationInformationWindow.html", "WindowName", "WindowFeatures??");
//			}
//		});
		
		if(GameInfo.getInstance().getPlayer().isAdmin()) {
			this.add(devPanel);
		}
		
//		HorizontalPanel timerPanel = new HorizontalPanel();
//		timerPanel.setBorderWidth(1);
//		timerPanel.set
	}
	
	public LocationInfoPanel() {
		
		ScrollPanel itemsInLocationScrollPanel = new ScrollPanel();
		if (!GameInfo.getInstance().DEBUG_MODE) hiddenPanel.setVisible(false);
		hiddenPanel.ensureDebugId("LocationPanel");
		this.add(hiddenPanel);
		
		itemsInLocationScrollPanel.add(itemsInLocationPanel);
		this.setBorderWidth(1);
//		this.playersInLocationBox.setVisibleItemCount(10);
//		this.locationTabPanel.add(itemsInLocationScrollPanel,"Items");
//		this.locationTabPanel.add(playersInLocationBox,"Players");
		this.add(locationNameLabel);
		this.add(itemsInLocationScrollPanel);
//		this.add(locationTabPanel);
//		this.locationTabPanel.selectTab(0);
//		this.add(showInfoButton);
		
//		showTownNamesCheckBox.setValue(true);
//		showRoadNamesCheckBox.setValue(false);
//		ClickHandler refreshMapHandler = new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				MapUtil.refreshMap();
//				}
//			};
//		showTownNamesCheckBox.addClickHandler(refreshMapHandler);
//		showRoadNamesCheckBox.addClickHandler(refreshMapHandler);
//		
//		this.add(showTownNamesCheckBox);
//		this.add(showRoadNamesCheckBox);
		
		createModeCheckBox.setText("Create Mode");
		roadModeCheckBox.setText("Road Mode");
		
		devPanel.add(createModeCheckBox);
		devPanel.add(roadModeCheckBox);
		
		peekFormButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AcceptPanelWindow instructionsDialog = new AcceptPanelWindow();
				instructionsDialog.getAcceptPanel().setHeight(512);
				instructionsDialog.getAcceptPanel().setWidth(724);
				instructionsDialog.setFormId(peekFormId);
				instructionsDialog.init();
				//instructionsDialog.showHideButton();
				instructionsDialog.center();
				//instructionsDialog.show();
				
				//popupInstructions.setWidget(instructionsDialog);
				//popupInstructions.show();
				
				//instructionsDialog.show();
				
//				WindowInformation.reset();
//
//				DockPanel parentPanel = WindowInformation.dockPanel;
//				final LoginPanel loginPanel = WindowInformation.loginPanel;
//
//				parentPanel.remove(loginPanel);
//				parentPanel.remove(WindowInformation.mapWidget);
//
//				WindowInformation.loginPanel.requestPeekDisplay(peekFormId);
			}
		});
		this.add(peekFormButton);
	}

	@Override
	public void setWidth(String width) {
		super.setWidth(width);
	}

	@Override
	public void setHeight(String height) {
		super.setHeight(height);
	}
	
	public boolean isCreateMode() {
		return this.createModeCheckBox.getValue();
	}

	public boolean isRoadMode() {
		return this.roadModeCheckBox.getValue();
	}

	public long getPeekFormId() { return peekFormId; }
	public void setPeekFormId(long peekFormId) { this.peekFormId = peekFormId; }
	
	// a function that clicks the correct "get" button based on the item we are trying to get
	// for test interface
	public void clickCorrectGet(ItemTypeDTO goalItem) {
		for (GetItemButton getButton : getItemButtons) {
			if (goalItem.getName() == getButton.getButtonName() || goalItem.equals(getButton.getButtonItem())) {
				getButton.click();
				break;
			}
		}
	}
	
	public void moveToLocation(Long locId) {
		AsyncCallback<MoveResult> callback = new AsyncCallback<MoveResult>() {
			
			public void onFailure(Throwable caught) {
				Window.alert("Move failed - " + caught.getMessage());
			}
			
			public void onSuccess(MoveResult result) {
				// Don't do anything right now
				/*if(result.isSuccess()) {
					updatePlayerInformation(result.getGameInformation().player);
				}*/
			}
		};

		GameServices.gameService.moveToLocation(GameInfo.getInstance().getPlayer(), locId, callback);
	}

//	public static void openLocationDialog(LocationDTO location) {
//		LocationInformationWindow locationDialog = WindowInformation.locationInformationWindow;
//		locationDialog.setTitle(location.getName());
//		locationDialog.refresh(location);
//		locationDialog.show();
//		locationDialog.setVisible(true);
//		locationDialog.setHeight("500px");
//		locationDialog.setWidth("500px");
//		locationDialog.setPixelSize(500, 500);
//		locationDialog.setPopupPosition(50, 50);
//	}
}

