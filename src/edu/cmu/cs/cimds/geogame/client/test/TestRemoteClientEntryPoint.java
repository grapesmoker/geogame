package edu.cmu.cs.cimds.geogame.client.test;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gwtwidgets.client.util.SimpleDateFormat;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MapZoomEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.cmu.cs.cimds.geogame.client.ActionResult;
import edu.cmu.cs.cimds.geogame.client.GameInfo;
import edu.cmu.cs.cimds.geogame.client.MapInformation;
import edu.cmu.cs.cimds.geogame.client.MoveResult;
import edu.cmu.cs.cimds.geogame.client.model.dto.CommStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.GameStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.RoadDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;
import edu.cmu.cs.cimds.geogame.client.ui.AcceptPanel;
import edu.cmu.cs.cimds.geogame.client.ui.CommsPanel;
import edu.cmu.cs.cimds.geogame.client.ui.LocationInfoPanel;
import edu.cmu.cs.cimds.geogame.client.ui.LoginPanel;
import edu.cmu.cs.cimds.geogame.client.ui.TreasurePanel;
import edu.cmu.cs.cimds.geogame.client.ui.WindowInformation;
import edu.cmu.cs.cimds.geogame.client.ui.map.DevUtil;

public class TestRemoteClientEntryPoint implements EntryPoint {
	
	private UserDTO player;
	private MapInformation mapInfo;
	private boolean movementFlag = true;
	private boolean speakFlag = true;
	private boolean pickupFlag = true;
		
	@SuppressWarnings("deprecation")
	public interface ControlImageBundle extends ImageBundle {
		@Resource("minus2.png")
		AbstractImagePrototype minus();
		
		@Resource("center2.png")
		AbstractImagePrototype center();

		@Resource("plus2.png")
		AbstractImagePrototype plus();
	}

	private static class ImageZoomControl extends CustomControl {
		public ImageZoomControl() {
			super(new ControlPosition(ControlAnchor.TOP_RIGHT, 7, 7));
		}

		@Override
		protected Widget initialize(final MapWidget map) {
			ControlImageBundle imgBundle = GWT.create(ControlImageBundle.class);
			Image centerImage = imgBundle.center().createImage();
			Image zoomInImage = imgBundle.plus().createImage();
			Image zoomOutImage = imgBundle.minus().createImage();

			centerImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if(GameInfo.getInstance().getPlayer()!=null) {
						//		        		map.setCenter(MapUtil.toLatLng(GameInformation.getInstance().getPlayer().getCurrentLocation().getMapPosition()));
						map.setCenter(LatLng.newInstance(GameInfo.getInstance().getPlayer().getCurrentLocation().getLatitude(), GameInfo.getInstance().getPlayer().getCurrentLocation().getLongitude()));
						//		        		map.setZoomLevel(9);
					}
				}
			});
			zoomInImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					map.zoomIn();
				}
			});
			zoomOutImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					map.zoomOut();
				}
			});

			Grid container = new Grid(4, 1);
			container.setWidget(0, 0, centerImage);
			container.setWidget(2, 0, zoomInImage);
			container.setWidget(3, 0, zoomOutImage);

			return container;
		}

		@Override
		public boolean isSelectable() {
			return false;
		}
	}

	private LatLng startLocation = LatLng.newInstance(26.771974,-4.523621);
	private Timer pollTimer;
	protected boolean needsRefresh;
	//	private LatLngBounds outside = LatLngBounds.newInstance(
	//			LatLng.newInstance(25.771974,-5.523621),
	//			LatLng.newInstance(27.771974,-3.523621));
	private Timer talkTimer;
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public static String titleString = "Geogame";
	public static String footerString = "Carnegie Mellon, Department of Psychology and Robotics Institute (C) 2010";

	/**
	 * Creates a new instance of MainEntryPoint
	 */
	public TestRemoteClientEntryPoint() { }

	/**
	 * The entry point method, called automatically by loading a module
	 * that declares an implementing class as an entry-point
	 */

	public void onModuleLoad() {
		
		// Define widget elements
		final DockPanel dock = new DockPanel();
		dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		dock.setBorderWidth(3);

		final Label titleLabel = new Label(titleString);
		final Label footerLabel = new Label(footerString);

		final TreasurePanel treasurePanel = new TreasurePanel();
		final LocationInfoPanel locationPanel = new LocationInfoPanel();
		final MapWidget map = new MapWidget(startLocation, 1);
		final LoginPanel loginPanel = new LoginPanel();
		final CommsPanel commsPanel = new CommsPanel();
		final AcceptPanel acceptPanel = new AcceptPanel();

		loginPanel.setWidth("200px");
		loginPanel.setHeight("600px");

		locationPanel.setWidth("150px");
		//		locationPanel.setHeight("800px");

		map.setSize("800px", "380px");
		map.setGoogleBarEnabled(false);
		map.setCurrentMapType(MapType.getSatelliteMap());
		map.setScrollWheelZoomEnabled(true);
		map.setPinchToZoom(true);


		ImageZoomControl izm = new ImageZoomControl();
		izm.initialize(map);
		map.addControl(izm);

		map.addMapZoomEndHandler(new MapZoomEndHandler() {
			public void onZoomEnd(MapZoomEndEvent event) {
				int minZoomLevel = 7;
				int maxZoomLevel = 10;
				if (map.getZoomLevel() < minZoomLevel) {
					map.setZoomLevel(minZoomLevel);
				} else if (map.getZoomLevel() > maxZoomLevel) {
					map.setZoomLevel(maxZoomLevel);
				}
				GameInfo.getInstance().refreshAll(false);
			}
		});

		map.addMapClickHandler(new MapClickHandler() {
			public void onClick(MapClickEvent event) {
				if(event.getOverlay()!=null) {
					return;
				} else {
					//Then it is a click on any point on the map
					if(WindowInformation.locationPanel.isCreateMode()) {
						DevUtil.handleDevMapClick(event.getLatLng());
					}
				}
			}
		});

		dock.add(titleLabel, DockPanel.NORTH);
		dock.add(footerLabel, DockPanel.SOUTH);
		dock.add(loginPanel, DockPanel.EAST);
		dock.add(map, DockPanel.CENTER);

		RootPanel.get().add(dock);

		WindowInformation.dockPanel = dock;
		WindowInformation.loginPanel = loginPanel;
		WindowInformation.treasurePanel = treasurePanel;
		WindowInformation.locationPanel = locationPanel;
		WindowInformation.commsPanel = commsPanel;
		WindowInformation.acceptPanel = acceptPanel;
		WindowInformation.titleLabel = titleLabel;
		WindowInformation.footerLabel = footerLabel;
		WindowInformation.mapWidget = map;
		
		
		Button playGameButton = new Button("Play game");
		playGameButton.addClickHandler(new ClickHandler() {
			public void onClick (ClickEvent event) {
				playGame();
			}
		});
		
		Button talkButton = new Button("Start talking");
		talkButton.addClickHandler(new ClickHandler() {
			public void onClick (ClickEvent event) {
				startTalking();
			}
		});
		
		Button stopPlayingButton = new Button("Stop playing");
		stopPlayingButton.addClickHandler(new ClickHandler() {
			public void onClick (ClickEvent event) {
				stopPlaying();
			}
		});
		
		
		WindowInformation.commsPanel.add(playGameButton);
		WindowInformation.commsPanel.add(talkButton);
		WindowInformation.commsPanel.add(stopPlayingButton);

		//log("about to log in through cookies");
		//WindowInformation.loginPanel.maybeLoginThroughCookies();
		log("About to log in the next test player");
		WindowInformation.loginPanel.loginNextTestPlayer();
		log("a thing");
	}
	
	public void playGame() {
		// the stages of game playing:
		// 1) move to a location that's different from where you are
		// 2) pick up an item there if possible
		// 3) send a message indicating what you did
		// 4) go to 1
		
		this.player = GameInfo.getInstance().getPlayer();
		
		log("getting game info...");
		getGameInformation();
		log("starting polling");
		
		Timer gameTimer = new Timer() {
			
			@Override
			public void run() {
				log("starting polling");
				startPolling();
				log("starting movement");
				startMovement();
				//log("starting talking");
				//startTalking();
			}
		};
		gameTimer.schedule(5000);
	}
	
	public void stopPlaying() {
		this.pollTimer.cancel();
		this.talkTimer.cancel();
	}
	
	void getGameInformation() {
		AsyncCallback<GameStruct> callback = new AsyncCallback<GameStruct>() {

			public void onFailure(Throwable caught) {
			}

			public void onSuccess(GameStruct result) {
				mapInfo = result.mapInformation;
				if (mapInfo != null) {
					log("mapInfo is not null");
				
					if (mapInfo.locations.isEmpty()) {
						log("no locations!");
					}
				}
				else {
					log ("fuuuck mapInfo is null!");
				}
			}
		};
		GameServices.gameService.getGameInformationNew(this.player, callback);
	}
	

	void startPolling() {
		log("startPolling called");
		this.pollTimer = new Timer() {
			int ajaxCallPollCounter = 0;
			int numPeriodsToPassBeforeAjaxCallExpires = 10;
			Date lastRequestDate = new Date();
			@Override
			public void run() {
				if (GameInfo.getInstance().isGameFinished()) {
					this.cancel();
					//talkTimer.cancel();
				}
				if(player == null) {
					return;
				}

				if(ajaxCallPollCounter > 0) {
					ajaxCallPollCounter--;
					return;
				}

				AsyncCallback<CommStruct> callback = new AsyncCallback<CommStruct>() {
					@Override
					public void onFailure(Throwable caught) {
						ajaxCallPollCounter=0;
					}
					@Override
					public void onSuccess(CommStruct result) {
						Date newDate = result.timestamp;
						boolean arrivedFlag = false;
						
						if (player.getDestination() == null && player.getCurrentLocation() != null) {
							speak("I have arrived at " + player.getCurrentLocation().getName());
							arrivedFlag = true;
						}			
						
						updatePlayerInformation(result.player);
						/*if (result.player.getCurrentRoadMovement() == null) {
							log("client-side CRM is null!");
						}
						else {
							log("client-side CRM is ok!");
						}*/
						
						if(arrivedFlag) {
							if (maybePickupItem()) {
								speak("I just picked up " + player.getInventory().get(player.getInventory().size()).getItemType().getName());
								speak("I am now looking for " + player.getCurrentGoalItemType().getName());
							}
							
							for (ItemDTO item : player.getCurrentLocation().getItems()) {
								speak(player.getCurrentLocation().getName() + " contains " + item.getItemType().getName());
							}
						}

						ajaxCallPollCounter=0;
						if(result.logOffPlayer) {
							pollTimer.cancel();
							player = null;
						}
						
						//log("arrived: " + String.valueOf(arrivedFlag) + ", movement: " + String.valueOf(movementFlag));
						
						if(arrivedFlag && movementFlag) {
							startMovement();
						}

						lastRequestDate = newDate;
						
					}
				};
				ajaxCallPollCounter = numPeriodsToPassBeforeAjaxCallExpires;
				log(player.getUsername() + " Getting messages");
				GameServices.messageService.getMessagesNew(player, lastRequestDate, needsRefresh, callback);
				needsRefresh=false;
			}
		};
		this.pollTimer.scheduleRepeating(1000);
	}
	
	void startMovement() {
		log("Starting movement.");
		long nextLocationId = -1;
		long curLocationId = this.player.getCurrentLocation().getId();
		if(this.mapInfo != null) {
			List<Long> destinationIds = new ArrayList<Long>();
			for(RoadDTO road : this.mapInfo.roads) {
				if(road.getLocation1().getId()==curLocationId) {
					destinationIds.add(road.getLocation2().getId());
				} else if(road.getLocation2().getId()==curLocationId) {
					destinationIds.add(road.getLocation1().getId());
				}
			}
			if(destinationIds.size()==0) {
				return;
			}
			nextLocationId = destinationIds.get(Random.nextInt(destinationIds.size()));
		}
		
		if(nextLocationId==-1) {
			Window.alert("Couldn't find a destination... :(");
			return;
		} else {
			log("Trying to move to destination " + String.valueOf(nextLocationId));
		}
		
		
		AsyncCallback<MoveResult> callback = new AsyncCallback<MoveResult>() {
			
			public void onFailure(Throwable caught) {
				Window.alert("Move failed - " + caught.getMessage());
			}
			
			public void onSuccess(MoveResult result) {
				if(result.isSuccess()) {
					log (result.getMessage());
					//log("moved succesfully! ended up at " + result.getGameInformation().player.getCurrentLocation().getName());
					updatePlayerInformation(result.getGameInformation().player);
				}
			}
		};

		GameServices.gameService.moveToLocationNew(this.player, nextLocationId, callback);
	}
	
	void speak(String message) {
		WindowInformation.commsPanel.appendToChatWindow(message);
		WindowInformation.commsPanel.clickOutgoingSendButton();
	}
	
	void startTalking() {
		this.talkTimer = new Timer() {
			@Override
			public void run() {
				if(player == null) {
					this.cancel();
					return;
				}
				if(!speakFlag) {
					this.cancel();
					return;
				}

				WindowInformation.commsPanel.appendToChatWindow(dateFormat.format(new Date()) + " - " + player.getUsername() + " SENDS message");
				WindowInformation.commsPanel.clickOutgoingSendButton();
			}
		};
		this.talkTimer.scheduleRepeating(5000);
	}
	
	private void updatePlayerInformation(UserDTO player) {
		String authCode = this.player.getAuthCode();
		this.player = player;
		/*if (player.getCurrentRoadMovement() == null) {
			log("got a CRM that was null from server side");
		}*/
		//GameInfo.getInstance().updatePlayerInformation(player);
		player.setAuthCode(authCode);
	}

	private boolean maybePickupItem() {
		if(pickupFlag && this.player!=null && this.player.getCurrentLocation()!=null) {
			List<ItemTypeDTO> goalItems = new ArrayList<ItemTypeDTO>(this.player.getItemsToCollect());

			goalItems.removeAll(this.player.getInventory());

			/*for(ItemDTO inventoryItem : this.player.getInventory()) {
				goalItems.remove(inventoryItem);
			}*/
			
			for(ItemTypeDTO goalItem : goalItems) {
				if(this.player.getCurrentLocation().getItems().contains(goalItem)) {
					WindowInformation.locationPanel.clickCorrectGet(goalItem);
					//pickupItem(this.player.getCurrentLocation().getItems().get(this.player.getCurrentLocation().getItems().indexOf(goalItem)));
					return true;
				}
			}
		}
		return false;
	}
	
	
	@SuppressWarnings("unused")
	private void pickupItem(ItemDTO item) {
		AsyncCallback<ActionResult> callback = new AsyncCallback<ActionResult>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not pick up item! - " + caught.getMessage());
			}

			@Override
			public void onSuccess(ActionResult result) {
				// item picked up
			}
		};
		GameServices.gameService.takeItemNew(this.player, item.getId(), callback);
	}
	
	public void log (String message) {
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to log your message!");
			}

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				// a winner is you
			}
		};
		GameServices.testService.log(message, callback);
	}
}
