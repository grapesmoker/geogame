package edu.cmu.cs.cimds.geogame.client.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.gwtwidgets.client.util.SimpleDateFormat;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;

import edu.cmu.cs.cimds.geogame.client.ActionResult;
import edu.cmu.cs.cimds.geogame.client.LoginResult;
import edu.cmu.cs.cimds.geogame.client.MapInformation;
import edu.cmu.cs.cimds.geogame.client.MoveResult;
import edu.cmu.cs.cimds.geogame.client.model.dto.CommStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.GameStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.MessageDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.RoadDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;
import edu.cmu.cs.cimds.geogame.client.ui.ChatPanel;
import edu.cmu.cs.cimds.geogame.client.ui.WindowInformation;

public class TestRemoteClient {

	private String username;
	private String password;
	private UserDTO player;
	private int pollPeriod;
	private int talkPeriod;
	private ChatPanel logPanel;
	private Timer pollTimer;
	private Timer startTimer;
	private Timer talkTimer;
	private int startTime;
	private int lastLagTime=0;
	private int maxLagTime=0;
	private Label lastLagTimeLabel;
	private Label maxLagTimeLabel;
	private Label locationLabel;
//	private boolean inAjaxMoveCall;
	private boolean movementFlag;
	private boolean talkFlag;
	private boolean pickupStuffFlag;
	private Button moveButton;
	private Button talkButton;
	private Button pickupStuffButton;
	private Label scoreLabel;
	private Label numItemsLabel;
	private boolean needsRefresh;
	private MapInformation mapInfo;
	
	private static List<Long> locationIds = new ArrayList<Long>();
	{
		locationIds.add(25L);
		locationIds.add(53L);
		locationIds.add(5L);
		locationIds.add(1L);
		locationIds.add(2L);
		locationIds.add(3L);
		locationIds.add(4L);
		locationIds.add(8L);
		locationIds.add(9L);
		locationIds.add(7L);
		locationIds.add(52L);
		locationIds.add(12L);
		locationIds.add(48L);
		locationIds.add(16L);
		locationIds.add(20L);
		locationIds.add(66L);
		locationIds.add(46L);
		locationIds.add(70L);
		locationIds.add(15L);
		locationIds.add(14L);
		locationIds.add(17L);
		locationIds.add(59L);
		locationIds.add(18L);
		locationIds.add(35L);
		locationIds.add(19L);
		locationIds.add(44L);
		locationIds.add(56L);
		locationIds.add(57L);
		locationIds.add(67L);
		locationIds.add(43L);
		locationIds.add(41L);
		locationIds.add(49L);
		locationIds.add(29L);
		locationIds.add(28L);
		locationIds.add(40L);
		locationIds.add(27L);
		locationIds.add(39L);
		locationIds.add(26L);
		locationIds.add(25L);
	}
	
	public boolean isMovementFlag() { return movementFlag; }
	public void setMovementFlag(boolean movementFlag) {
		if(this.movementFlag==movementFlag) {
			return;
		}
		this.movementFlag = movementFlag;
		if(movementFlag && this.isLoggedIn()) {
			this.startMovement();
		}
		if(movementFlag) {
			moveButton.setText("Stop");
		} else {
			moveButton.setText("Move");
		}
	}
	public void toggleMovementFlag() {
		this.setMovementFlag(!this.movementFlag);
	}

	public boolean isTalkFlag() { return talkFlag; }
	public void setTalkFlag(boolean talkFlag) {
		// TODO Auto-generated method stub
		
		if(this.talkFlag==talkFlag) {
			return;
		}
		this.talkFlag = talkFlag;
		if(talkFlag && this.isLoggedIn()) {
			this.startTalking();
		}
		if(talkFlag) {
			talkButton.setText("Stop");
		} else {
			talkButton.setText("Talk");
		}
	}
	public void toggleTalkFlag() {
		this.setTalkFlag(!this.talkFlag);
	}
	
	public boolean isPickupStuffFlag() { return pickupStuffFlag; }
	public void setPickupStuffFlag(boolean pickupStuffFlag) {
		// TODO Auto-generated method stub
		
		if(this.pickupStuffFlag==pickupStuffFlag) {
			return;
		}
		this.pickupStuffFlag = pickupStuffFlag;
//		if(pickupStuffFlag && this.isLoggedIn()) {
//			this.startPickupStuff();
//		}
		if(pickupStuffFlag) {
			pickupStuffButton.setText("Stop");
		} else {
			pickupStuffButton.setText("Pick up stuff");
		}
	}
	public void togglePickupStuffFlag() {
		this.setPickupStuffFlag(!this.pickupStuffFlag);
	}
	
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	TestRemoteClient() {}

	void init(String username, String password, int pollPeriod, ChatPanel logPanel, int startTime) {
		this.username = username;
		this.password = password;
		this.pollPeriod = pollPeriod;
		this.talkPeriod = pollPeriod;
		this.logPanel = logPanel;
		this.startTime = startTime;
	}
	
	void log(String message) {
		if(logPanel==null) {
			Window.alert("LogPanel is not set for user " + this.username);
		} else {
			String usernamePrefix = this.player!=null ? this.player.getUsername() + " - " : (this.username!=null ? this.username + " - " : "");
			logPanel.addMessage(usernamePrefix + dateFormat.format(new Date()) + " -  " + message);
		}
	}
	
	void beginCycle() {
		this.lastLagTime=0;
		this.maxLagTime=0;
		this.refreshLagLabels();
		this.startTimer = new Timer() {
			@Override
			public void run() {
				AsyncCallback<Void> cb = new AsyncCallback<Void> () {
					@Override
					public void onFailure(Throwable caught) { }
					@Override
					public void onSuccess(Void result) {
						TestRemoteClient.this.getGameInformation();
						TestRemoteClient.this.startPolling();
						if(TestRemoteClient.this.isMovementFlag()) {
							TestRemoteClient.this.startMovement();
						}
					}
				};
				TestRemoteClient.this.login(cb);
				TestRemoteClient.this.startTimer=null;
			}
		};
		this.startTimer.schedule(this.startTime);
	}
	
	void getGameInformation() {
		AsyncCallback<GameStruct> callback = new AsyncCallback<GameStruct>() {

			public void onFailure(Throwable caught) {
			}

			public void onSuccess(GameStruct result) {
				TestRemoteClient.this.mapInfo = result.mapInformation;
			}
		};
		log("Sending login");
		GameServices.gameService.getGameInformation(this.player, callback);
	}
	
	void login(final AsyncCallback<Void> cb) {
		
		AsyncCallback<Void> createUserCallback = new AsyncCallback<Void>() {
			@Override
			public void onSuccess (Void result) {
				// looks like we created the user
				AsyncCallback<LoginResult> callback = new AsyncCallback<LoginResult>() {
					
					public void onFailure(Throwable caught) {
						log("Error while calling Login Service: " + caught.getMessage());
					}
		
					public void onSuccess(LoginResult result) {
						if(result.isSuccess()) {
							log("Login successful!");
							TestRemoteClient.this.player = result.getPlayer();
							cb.onSuccess(null);
						} else {
							log("Invalid login. Please try again.");
						}
					}
				};
				log("Sending login");
				GameServices.loginService.sendLogin(username, password, callback);
			}
			@Override
			public void onFailure(Throwable caught) {
				// presumably this means the user already exists
				AsyncCallback<LoginResult> callback = new AsyncCallback<LoginResult>() {
		
					public void onFailure(Throwable caught) {
						log("Error while calling Login Service: " + caught.getMessage());
					}
		
					public void onSuccess(LoginResult result) {
						if(result.isSuccess()) {
							log("Login successful!");
							TestRemoteClient.this.player = result.getPlayer();
							cb.onSuccess(null);
						} else {
							log("Invalid login. Please try again.");
						}
					}
				};
				log("Sending login");
				GameServices.loginService.sendLogin(username, password, callback);
			}
			
		};
		GameServices.gameService.createUser(this.username, "Test", "User", "", "test", WindowInformation.AMTVisitor, createUserCallback);
	}
	
	void logout() {
		if(this.isLoggingIn()) {
			log("Stopping login");
			this.stopLogin();
			return;
		}
		if(!this.isLoggedIn()) {
			return;
		}
		
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {
				log("Error while calling Logout Service: " + caught.getMessage());
			}

			public void onSuccess(Boolean result) {
				if(result) {
					log("Logged out!");
					TestRemoteClient.this.player = null;
				} else {
					log("Invalid logout. Please try again.");
				}
			}
		};
		log("Logging out");
		this.pollTimer.cancel();
		GameServices.loginService.sendLogout(this.player, callback);
	}
	
	boolean isLoggingIn() {
		return this.startTimer!=null;
	}
	
	void stopLogin() {
		this.startTimer.cancel();
		this.startTimer = null;
	}
	
	boolean isLoggedIn() {
		return this.player!=null;
	}
	
	void startPolling() {
		this.pollTimer = new Timer() {
			int ajaxCallPollCounter = 0;
			int numPeriodsToPassBeforeAjaxCallExpires = 10;
			Date lastRequestDate = new Date();
			@Override
			public void run() {
				if(!TestRemoteClient.this.isLoggedIn()) {
					return;
				}
//				log("Attempting to poll - ajaxPollCounter=" + ajaxCallPollCounter);
				if(ajaxCallPollCounter>0) {
					log(":( - " + ajaxCallPollCounter);
					ajaxCallPollCounter--;
					return;
				}
//				log("Polling... :)");

				AsyncCallback<CommStruct> callback = new AsyncCallback<CommStruct>() {
					@Override
					public void onFailure(Throwable caught) {
						log("Poll failure - " + caught.getMessage());
						ajaxCallPollCounter=0;
					}
					@Override
					public void onSuccess(CommStruct result) {
//						log("Poll success1");
						Date newDate = result.timestamp;
						
						TestRemoteClient.this.lastLagTime = (int)(newDate.getTime()-lastRequestDate.getTime());
						if(TestRemoteClient.this.lastLagTime>TestRemoteClient.this.maxLagTime) {
							TestRemoteClient.this.maxLagTime=TestRemoteClient.this.lastLagTime;
						}
						
//						log("Poll success2");

						boolean arrivedFlag = false;
						if(TestRemoteClient.this.player.getCurrentLocation()==null && result.player.getCurrentLocation()!=null) {
							arrivedFlag = true;
							log("Arrived! CurrentLocation becomes " + result.player.getCurrentLocation().getName());
//						} else {
//							try {
//								log("Not arrived yet! CurrentLocation is " + TestRemoteClient.this.player.getCurrentLocation().getName() + " and becomes " + result.player.getCurrentLocation().getName());
//							} catch(Exception ex) {
//								log("Would've thrown an exception!");
//							}
						}
						
						
						TestRemoteClient.this.updatePlayerInformation(result.player);
//						log("Player's position: " + result.player.getLatitude() + "," + result.player.getLongitude());
						
//						boolean pickedUpItem;
						if(arrivedFlag) {
							/*pickedUpItem = */TestRemoteClient.this.maybePickupItem();
						}
						TestRemoteClient.this.refreshLagLabels();
//						log("Stuff");
						TestRemoteClient.this.refreshPlayerInfo();

//						log("Resetting ajaxCallPollCounter to 0");
						ajaxCallPollCounter=0;
						if(result.logOffPlayer) {
							TestRemoteClient.this.pollTimer.cancel();
							TestRemoteClient.this.player = null;
						}
						
						if(arrivedFlag && TestRemoteClient.this.isMovementFlag()) {
//							if(!pickedUpItem) {
//							log("Restarting movement");
								TestRemoteClient.this.startMovement();
//								log("Restarted movement");
//							} else {
//								new Timer() {
//									@Override
//									public void run() {
//										TestRemoteClient.this.startMovement();
//									}
//								}.schedule(2000);
//							}
						}
//						if(result.player!=null) {
//							String authCode = TestRemoteClient.this.player.getAuthCode();
//							TestRemoteClient.this.player = result.player;
//							TestRemoteClient.this.player.setAuthCode(authCode);
//						} else {
//							log("Get' result was null!");
//						}
//						TestRemoteClient.this.player.setCurrentLocation(result.player.getCurrentLocation());
						lastRequestDate = newDate;
					}
				};
				ajaxCallPollCounter = numPeriodsToPassBeforeAjaxCallExpires;
				GameServices.messageService.getMessages(TestRemoteClient.this.player, lastRequestDate, needsRefresh, callback);
				needsRefresh=false;
			}
		};
		this.pollTimer.scheduleRepeating(this.pollPeriod);
	}
	
	void startMovement() {
		long nextLocationId = -1;
		long curLocationId = this.player.getCurrentLocation().getId();
		if(this.mapInfo!=null) {
			List<Long> destinationIds = new ArrayList<Long>();
			for(RoadDTO road : this.mapInfo.roads) {
				if(road.getLocation1().getId()==curLocationId) {
					destinationIds.add(road.getLocation2().getId());
				} else if(road.getLocation2().getId()==curLocationId) {
					destinationIds.add(road.getLocation1().getId());
				}
			}
			log("Found " + destinationIds.size() + " possible destinations");
			if(destinationIds.size()==0) {
//				this.mapInfo=null;
//				this.startMovement();
				return;
			}
			nextLocationId = destinationIds.get(Random.nextInt(destinationIds.size()));
//		} else {
//			int index = locationIds.indexOf(curLocationId);
//			if(index==-1) {
//				log("Location " + curLocationId + " not found :(");
//				return;
//			}
//			nextLocationId = locationIds.get((int)(index+1)%locationIds.size());
		}
		if(nextLocationId==-1) {
			log("Couldn't find a destination... :(");
			return;
		}
		
		log("In location " + this.player.getCurrentLocation().getName() + ", will move to " + nextLocationId);
		
//		GameInfo.getInstance().log("Traveling " + GameInfo.getInstance().getLocation().getName() + " -> " + nextLocationId + "...");
		
		AsyncCallback<MoveResult> callback = new AsyncCallback<MoveResult>() {
			
			public void onFailure(Throwable caught) {
//				TestRemoteClient.this.needsRefresh = true;
//				TestRemoteClient.this.inAjaxMoveCall = false;
				log("Move failed - " + caught.getMessage());
				//GameInfo.getInstance().log("Move failed! " + caught.getMessage());

//				new Timer() {
//					@Override
//					public void run() {
//						if(TestRemoteClient.this.isLoggedIn() && TestRemoteClient.this.isMovementFlag()) {
//							log("Move timer done, moving again!");
////							TestRemoteClient.this.player.setCurrentLocation(location);
//							TestRemoteClient.this.startMovement();
//						} else {
//							log("Move timer done, but NOT moving again!");
//						}
//					}
//				}.schedule(10000);
			}
			
			public void onSuccess(MoveResult result) {
				log("Move start successful");
//				TestRemoteClient.this.inAjaxMoveCall = false;
				if(result.isSuccess()) {
//					log("Result is " + result);
//					log("GameInformation is " + result.getGameInformation());
//					log("Player " + result.getGameInformation().player);
//					log("Location is " + result.getGameInformation().player.getCurrentLocation());

//					String locationName;
					
//					locationName="NULL";
//					if(TestRemoteClient.this.player.getCurrentLocation()!=null) {
//						locationName = TestRemoteClient.this.player.getCurrentLocation().getName();
//					}					
//					log("After movement start, before player update: Location = " + locationName);

					TestRemoteClient.this.updatePlayerInformation(result.getGameInformation().player);
					
//					locationName="NULL";
//					if(TestRemoteClient.this.player.getCurrentLocation()!=null) {
//						locationName = TestRemoteClient.this.player.getCurrentLocation().getName();
//					}
//					log("After movement start, after player update: Location = " + locationName);

					
					//if(result.getGameInformation().mapInformation.)
					log("Moving to " + TestRemoteClient.this.player.getDestination().getName() + " - duration: " + (double)result.getDuration()/1000 + " s");
//					GameInfo.getInstance().log(result.getMessage());
//					GameInfo.getInstance().updateInformation(result.getGameInformation());
//					GameInfo.getInstance().refreshAll(false);
//					new Timer() {
//						@Override
//						public void run() {
//							if(TestRemoteClient.this.isLoggedIn() && TestRemoteClient.this.isMovementFlag()) {
//								log("Move timer done, moving again!");
////								TestRemoteClient.this.player.setCurrentLocation(location);
//								TestRemoteClient.this.startMovement();
//							} else {
//								log("Move timer done, but NOT moving again!");
//							}
//						}
//					}.schedule(result.getDuration()+1000);
				}
			}
		};
		log("About to move...");
		GameServices.gameService.moveToLocationNew(this.player, nextLocationId, callback);
	}
	
	void startTalking() {
		this.talkTimer = new Timer() {
			@Override
			public void run() {
				if(!TestRemoteClient.this.isLoggedIn()) {
					this.cancel();
					return;
				}
				if(!TestRemoteClient.this.talkFlag) {
					this.cancel();
					return;
				}
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						log("Sending message failed - " + caught.getMessage());
					}
					@Override
					public void onSuccess(Void result) {
						log("Message sent!");
					}
				};
				
				MessageDTO message = new MessageDTO();
				message.setBroadcast(true);
				message.setContent(dateFormat.format(new Date()) + " - " + TestRemoteClient.this.username + " SENDS message");
				message.setSender(TestRemoteClient.this.username);
				
				GameServices.messageService.sendMessage(TestRemoteClient.this.player, message, callback);
				needsRefresh=false;
			}
		};
		this.talkTimer.scheduleRepeating(this.talkPeriod);
	}

	void setTimeLabels(Label lastLagTimeLabel, Label maxLagTimeLabel, Label locationLabel, Button moveButton, Button pickupStuffButton, Button talkButton, Label scoreLabel, Label numItemsLabel) {
		this.lastLagTimeLabel = lastLagTimeLabel;
		this.maxLagTimeLabel = maxLagTimeLabel;
		this.locationLabel = locationLabel;
		this.moveButton = moveButton;
		this.pickupStuffButton = pickupStuffButton;
		this.talkButton = talkButton;
		this.scoreLabel = scoreLabel;
		this.numItemsLabel = numItemsLabel;
		this.refreshLagLabels();
	}
	
	void refreshLagLabels() {
		this.lastLagTimeLabel.setText((double)this.lastLagTime/1000 + "s");
		this.maxLagTimeLabel.setText((double)this.maxLagTime/1000 + "s");
	}
	
	void refreshPlayerInfo() {
		if(this.player!=null) {
			if(!this.player.isMoving()) {
				this.locationLabel.setText(this.player.getCurrentLocation().getName());
			} else {
				this.locationLabel.setText(this.player.getSource().getName() + " -> " + this.player.getDestination().getName() + " (" + this.player.getCurrentRoadMovement().getDuration()/1000 + " s -- " + this.player.getLatitude() + "," + this.player.getLongitude() + ")");
			}
			this.scoreLabel.setText(String.valueOf(this.player.getScore()));
			this.numItemsLabel.setText(String.valueOf(this.player.getInventory().size()));
		}
	}
	private void updatePlayerInformation(UserDTO player) {
		String authCode = this.player.getAuthCode();
		TestRemoteClient.this.player = player;
		TestRemoteClient.this.player.setAuthCode(authCode);
	}

	private boolean maybePickupItem() {
		if(this.isPickupStuffFlag() && this.player!=null && this.player.getCurrentLocation()!=null) {
			List<ItemTypeDTO> goalItems = new ArrayList<ItemTypeDTO>(this.player.getItemsToCollect());
			
			Iterator<ItemDTO> itemIterator = this.player.getInventory().iterator();
			while (itemIterator.hasNext()) {
				itemIterator.remove();
			}
			
			/*for(ItemDTO inventoryItem : this.player.getInventory()) {
				goalItems.remove(inventoryItem);
			}*/
			
			for(ItemTypeDTO goalItem : goalItems) {
				if(this.player.getCurrentLocation().getItems().contains(goalItem)) {
					pickupItem(this.player.getCurrentLocation().getItems().get(this.player.getCurrentLocation().getItems().indexOf(goalItem)));
					return true;
//					break;
				}
			}
		}
		return false;
	}
	
	private void pickupItem(ItemDTO item) {
		AsyncCallback<ActionResult> callback = new AsyncCallback<ActionResult>() {
			@Override
			public void onFailure(Throwable caught) {
				log("Could not pick up item! - " + caught.getMessage());
			}

			@Override
			public void onSuccess(ActionResult result) {
				log("Item picked up!");
			}
		};
		log("Picking up " + item.getItemType().getName() + " at " + item.getLocation().getName());
		GameServices.gameService.takeItem(this.player, item.getId(), callback);
	}
}