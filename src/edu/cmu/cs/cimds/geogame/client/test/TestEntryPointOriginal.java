//package edu.cmu.cs.cimds.geogame.client.test;
//
//import java.util.Date;
//
//import org.gwtwidgets.client.util.SimpleDateFormat;
//
//import com.google.gwt.core.client.EntryPoint;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.user.client.Random;
//import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.DockPanel;
//import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.RootPanel;
//import com.google.gwt.user.client.ui.TextBox;
//
//import edu.cmu.cs.cimds.geogame.client.LoginResult;
//import edu.cmu.cs.cimds.geogame.client.model.dto.CommStruct;
//import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
//import edu.cmu.cs.cimds.geogame.client.services.GameServices;
//import edu.cmu.cs.cimds.geogame.client.ui.ChatPanel;
//
//public class TestEntryPointOriginal implements EntryPoint {
//
//	public static String titleString = "Geogame";
//	public static String footerString = "CMU RI (C) 2009";
//	
//	/**
//	 * Creates a new instance of TestEntryPoint
//	 */
//	public TestEntryPointOriginal() { }
//
//	/**
//	 * The entry point method, called automatically by loading a module
//	 * that declares an implementing class as an entry-point
//	 */
//	
//	private static final int DEFAULT_NUM_TEST_PLAYERS = 50;
//	private static final int GET_MESSAGES_POLL_PERIOD = 1000;
//	private static final int INITIAL_LOG_IN_INTERVAL = 30000;
//	
//	ChatPanel logPanel = new ChatPanel();
//	
//	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
//	private void log(String message) {
//		logPanel.addMessage(dateFormat.format(new Date()) + " -  " + message);
//	}
//	
//	private int numTestPlayers = DEFAULT_NUM_TEST_PLAYERS;
//	private int getMessagesPollPeriod = GET_MESSAGES_POLL_PERIOD;
//	
//	public void onModuleLoad() {
//		// Define widget elements
//		final DockPanel dock = new DockPanel();
//		dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
//		dock.setBorderWidth(3);
//
//		final Label titleLabel = new Label(titleString);
//		final Label footerLabel = new Label(footerString);
//
//		RootPanel.get().add(titleLabel);
//		RootPanel.get().add(footerLabel);
//		RootPanel.get().add(logPanel);
//		
//		RootPanel.get().add(new Label("Num Test Players:"));
//		final TextBox numPlayersTextBox = new TextBox();
//		numPlayersTextBox.setText(String.valueOf(numTestPlayers));
//		RootPanel.get().add(numPlayersTextBox);
//		Button changeNumUsersButton = new Button("Set Number of Users");
//		changeNumUsersButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				try {
//					int numUsers = Integer.valueOf(numPlayersTextBox.getText());
//					if(numUsers<0) {
//						throw new NumberFormatException();
//					}
//					changeNumTestPlayers(numUsers);
//				} catch(NumberFormatException ex) {
//					Window.alert(numPlayersTextBox.getText() + " is not a valid number of test users");
//				}
//			}
//		});
//		RootPanel.get().add(changeNumUsersButton);
//		
//		RootPanel.get().add(logPanel);
//		
//		runTestCycle(DEFAULT_NUM_TEST_PLAYERS);
//	}
//
//	private void changeNumTestPlayers(int numTestPlayers) {
//		this.numTestPlayers = numTestPlayers;
//		
//	}
//	
//	public void runTestCycle(int numTestPlayers) {
//		loginPlayers(numTestPlayers);
////		while(players.remove(null));
////		double[] startTimes = new double[numTestPlayers];
////		Timer[] startTimers = new Timer[numTestPlayers];
////		for(int i=0;i<numTestPlayers;i++) {
////			final UserDTO player = players.get(i);
////			startTimes[i] = Random.nextDouble();
////			startTimers[i] = new Timer() {
////				@Override
////				public void run() {
////					new Timer() {
////						@Override
////						public void run() {
////							updateTimer = new Timer() {
////								@Override
////								public void run() {
////									if(WindowInformation.inAjaxCall) {
////										return;
////									}
////									if(GameInfo.getInstance().getPlayer()==null) {
////										return;
////									}
//////									AsyncCallback<List<MessageDTO>> callback = new AsyncCallback<List<MessageDTO>>() {
////					//
//////										@Override
//////										public void onFailure(Throwable caught) {
//////											Window.alert("Error while calling Message Service: " + caught.getMessage());
//////										}
////					//
//////										@Override
//////										public void onSuccess(List<MessageDTO> result) {
//////											for (MessageDTO m : result) {
//////												if(m.getTimeSent().getTime()<=lastMessageTime.getTime()) {
//////													continue;
//////												}
//////												addMessage(m);
//////												long a = lastMessageTime.getTime();
//////												long b = m.getTimeSent().getTime();
//////												long c = Math.max(a,b);
//////												
//////												lastMessageTime = new Date(c);
//////											}
//////										}
//////									};
//////									messageService.getMessages(GameInformation.getInstance().getPlayer(), lastMessageTime, callback);
////									AsyncCallback<CommStruct> callback = new AsyncCallback<CommStruct>() {
////
////										@Override
////										public void onFailure(Throwable caught) {
////											WindowInformation.inAjaxCall = false;
////											//***Window.alert("Error while calling Message Service: " + caught.getMessage());
////										}
////
////										@Override
////										public void onSuccess(CommStruct commStruct) {
////											WindowInformation.inAjaxCall = false;
////											if(commStruct.logOffPlayer) {
////												Window.alert("Game is being reset - you will now be logged off");
////												WindowInformation.reset();
////												GameInfo.getInstance().reset();
////												return;
////											}
////											
////											for (MessageDTO message : commStruct.messages) {
////												if(message.getTimeSent().getTime()<=lastMessageTime.getTime()) {
////													continue;
////												}
////												addMessage(message);
////												long a = lastMessageTime.getTime();
////												long b = message.getTimeSent().getTime();
////												long c = Math.max(a,b);
////												
////												lastMessageTime = new Date(c);
////											}
////											
//////											boolean refreshFlag = false;
////
//////											UserDTO oldPlayer = GameInformation.getInstance().getPlayer();
//////											if(oldPlayer.getLatitude()!=commStruct.player.getLatitude() || oldPlayer.getLongitude()!=commStruct.player.getLongitude()) {
//////												refreshFlag = true;
//////											}
////											if(!GameInfo.getInstance().isGameStarted() && commStruct.gameStarted) {
////												Date newDate = new Date(zeroDate.getTime()+commStruct.timeRemaining);
////												Window.alert("Game has started!\nTime remaining: " + new SimpleDateFormat("HH:mm:ss").format(newDate));
////											}
////											if(GameInfo.getInstance().isGameFinished() && !commStruct.gameFinished) {
////												Date newDate = new Date(zeroDate.getTime()+commStruct.timeRemaining);
////												Window.alert("Game has started!\nTime remaining: " + new SimpleDateFormat("HH:mm:ss").format(newDate));
////											}
////
////											if(!GameInfo.getInstance().isGameStarted() && commStruct.gameFinished) {
////												Window.alert("Game has ended!");
////											}
////											GameInfo.getInstance().setGameStarted(commStruct.gameStarted);
////											GameInfo.getInstance().setGameFinished(commStruct.gameFinished);
////											
////											boolean wasMoving = GameInfo.getInstance().getPlayer().isMoving();
////											GameInfo.getInstance().updatePlayerInformation(commStruct.player);
////											GameInfo.getInstance().setScores(commStruct.scorePlayers);
////											boolean isMoving = GameInfo.getInstance().getPlayer().isMoving();
////											
////											if(wasMoving && !isMoving) {
////												WindowInformation.treasurePanel.log("You have reached " + GameInfo.getInstance().getPlayer().getCurrentLocation().getName());
////												WindowInformation.locationPanel.refresh();
////											}
////											
////											WindowInformation.treasurePanel.updateGameTimer(Math.max(0, commStruct.timeRemaining),commStruct.gameDuration);
//////											WindowInformation.treasurePanel.refreshScoreGrid();
////											WindowInformation.treasurePanel.refreshScore();
//////											MapUtil.updatePosition(commStruct.player.getCurrentLocation());
////											
//////											for (TradeOfferDTO tradeOffer : commStruct.tradeOffers) {
//////												//If it is a new trade offer being offered to the player:
//////												if(tradeOffer.getPlayer2().getId()==GameInformation.getInstance().getPlayer().getId()) {
//////													//*** Add code to open tradeWindow properly.
//////													new TradeWindow2(tradeOffer).show();
//////												}
//////												//If it is a trade offer bein replied to the player:
//////												else if(tradeOffer.getPlayer1().getId()==GameInformation.getInstance().getPlayer().getId()) {
//////													String outcomeString = null;
//////													switch(tradeOffer.getStatus()) {
//////														case ACCEPTED: 	outcomeString = " accepted ";
//////																		refreshFlag = true;
//////																		break;
//////														case REFUSED: 	outcomeString = " refused ";
//////																		break;
//////													}
//////													Window.alert(tradeOffer.getPlayer2().getUsername() + " has " + outcomeString + " your offer!");
//////												}
//////												lastMessageTime = new Date(Math.max(lastMessageTime.getTime(), tradeOffer.getTimeCreated().getTime()));
//////											}
//////											if(refreshFlag) {
//////												GameInformation.getInstance().refreshAll(true);
//////											} else {
////												MapUtil.updatePosition(commStruct.player.getLatitude(), commStruct.player.getLongitude());
////												if(commStruct.player.isMoving()) {
//////													WindowInformation.locationPanel.setNoLocation();
////													WindowInformation.locationPanel.refresh();
////												}
//////											}
////											for(ScoreMessage scoreMessage : commStruct.scoreMessages) {
////												WindowInformation.treasurePanel.log(scoreMessage.getActor().getUsername() + " found an item! You gain +" + scoreMessage.getItemObtained() + " points.");
////											}
////										}
////									};
//////									messageService.getMessages(GameInformation.getInstance().getPlayer(), lastMessageTime, callback);
////									WindowInformation.inAjaxCall = true;
////									messageService.getMessages(GameInfo.getInstance().getPlayer(), lastMessageTime, callback);
////									
////									//Timeout for the call - if it takes more than "10 seconds", release the lock on AJAX Calls.
////									new Timer() {
////										@Override
////										public void run() {
////											WindowInformation.inAjaxCall = false;
////										}
////									}.schedule(WindowInformation.AJAX_TIMEOUT);
////								}
////							};
////
////						}
////					}.scheduleRepeating(GET_MESSAGES_POLL_PERIOD);
////				}
////			};
////		}
//	}
//	
//	public void loginPlayers(final int numTestPlayers) {
//		log("Logging " + numTestPlayers + " players in");
//		final UserDTO[] players = new UserDTO[numTestPlayers];
//		for(int i=0;i<numTestPlayers;i++) {
//			String playerName = "TestUser" + i;
//			sendLogin(playerName, playerName, players, i);
////			if(player!=null) {
////				players.add(player);
////			} else {
////				players.add(null);
////			}
//		}
//		
//		final int allPlayersLoggedInPollPeriod = 5000;
//		
//		new Timer() {
//			@Override
//			public void run() {
//				for(int i=0;i<numTestPlayers;i++) {
//					if(players[i]==null) {
//						return;
//					}
//				}
//				this.cancel();
//				startPolling(players);
//			}
//		}.scheduleRepeating(allPlayersLoggedInPollPeriod);
//	}
//	
//	private void startPolling(final UserDTO[] players) {
//		int[] startTimes = new int[players.length];
//		final Timer[] startTimers = new Timer[players.length];
//		final int[] ajaxCallPollCounter = new int[players.length];
//		final int numPeriodsToPassBeforeAjaxCallExpires = 20;
//		for(int i=0;i<players.length;i++) {
//			final UserDTO player = players[i];
//			final Date lastRequestDate = new Date();
//			final int index=i;
//			startTimes[i] = (int)(Random.nextDouble()*INITIAL_LOG_IN_INTERVAL);
//			startTimers[i] = new Timer() {
//				@Override
//				public void run() {
//					if(ajaxCallPollCounter[index]>0) {
//						ajaxCallPollCounter[index]--;
//						return;
//					}
//					log(players[index].getUsername() + " polling");
//
//					AsyncCallback<CommStruct> callback = new AsyncCallback<CommStruct>() {
//						@Override
//						public void onFailure(Throwable caught) {
//							ajaxCallPollCounter[index]=0;
//						}
//						@Override
//						public void onSuccess(CommStruct result) {
//							ajaxCallPollCounter[index]=0;
//							if(result.logOffPlayer) {
//								cancel();
//							}
//						}
//					};
//					ajaxCallPollCounter[index] = numPeriodsToPassBeforeAjaxCallExpires;
//					GameServices.messageService.getMessages(player, lastRequestDate, callback);
//				}
//			};
//		}
//		
//		for(int i=0;i<players.length;i++) {
//			final int index = i;
//			new Timer() {
//				@Override
//				public void run() {
//					log(players[index].getUsername() + " beginning message poll");
//					startTimers[index].scheduleRepeating(GET_MESSAGES_POLL_PERIOD);
//				}
//			}.schedule((int) startTimes[i]);
//		}
//	}
//
//	private void sendLogin(String username, String password, final UserDTO[] players, final int index) {
//		AsyncCallback<LoginResult> callback = new AsyncCallback<LoginResult>() {
//
//			public void onFailure(Throwable caught) {
//				log("Error while calling Login Service: " + caught.getMessage());
//			}
//
//			public void onSuccess(LoginResult result) {
//				if(result.isSuccess()) {
//					log("Login for " + result.getPlayer().getUsername() + " successful!");
//					players[index] = result.getPlayer();
//				} else {
//					log("Invalid login. Please try again.");
//				}
//			}
//		};
//		log("Sending login for " + username);
//		GameServices.loginService.sendLogin(username, password, callback);
//	}
//}