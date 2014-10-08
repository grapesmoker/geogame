package edu.cmu.cs.cimds.geogame.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gwtwidgets.client.util.SimpleDateFormat;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.GameInfo;
import edu.cmu.cs.cimds.geogame.client.model.dto.CommStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.GeoGameCommandDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.MessageDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ScoreMessage;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;
import edu.cmu.cs.cimds.geogame.client.services.MessageServiceAsync;
import edu.cmu.cs.cimds.geogame.client.ui.map.MapUtil;

public class CommsPanel extends VerticalPanel {

	private static final int MAX_SENT_CHARS = 150;
//	private static final DateTimeFormat fmt = DateTimeFormat.getFormat("HH:mm:ss");
	private static Date lastMessageTime = new Date(0);
	private static final int messagePollPeriod = 1000;
	
	private static final String EMPTY_MESSAGE_BOX_TEXT = "Click here to send a message to your team";

	Date zeroDate = new Date(2000,1,1,0,0,0);
	
	ScrollPanel incomingMsgScrollPanel = new ScrollPanel();
	SimplePanel hiddenPanel = new SimplePanel();
	
//	ScrollPanel neighborsScrollPanel = new ScrollPanel();
	
//	TabPanel commsTabPanel = new TabPanel();
	ChatPanel messagesPanel = new ChatPanel();
//	HTML allMessagesLabel = new HTML();
	TextArea outgoingMsgBox = new TextArea();
	boolean emptyMessageBox = true;
	
//	ListBox neighborsBox = new ListBox();
	Button outgoingSendButton = new Button("Send to Team");
	//TODO: Decide whether to keep the "Command" button.
//	Button commandButton = new Button("Command");
	Button logoutButton = new Button("Log out");
//	Button refreshButton = new Button("Clear");
	
//	final MessageServiceAsync messageService;
	final MessageServiceAsync messageService;
	Timer updateTimer;

	public void clearContent() {
//		this.allMessagesLabel.setText("");
		this.messagesPanel.clearMessages();
		this.outgoingMsgBox.setText("");
//		this.neighborsBox.clear();
//		this.commsTabPanel.getDeckPanel().setTitle("Team");
		this.outgoingMsgBox.setText(EMPTY_MESSAGE_BOX_TEXT);
		emptyMessageBox = true;
	}

	public void initialize() {
		this.clearContent();
		
		outgoingMsgBox.ensureDebugId("OutgoingMsgBox");
		outgoingSendButton.ensureDebugId("SendToTeam");
		logoutButton.ensureDebugId("Logout");
		
		
		messagesPanel.setMaxMessages(10);
		messagesPanel.setHeight("10ex");
//		for(UserDTO neighbor : GameInfo.getInstance().getPlayer().getNeighbors()) {
//			neighborsBox.addItem(neighbor.getUsername(), String.valueOf(neighbor.getId()));
//		}
		lastMessageTime.setTime(0);
		
		updateTimer.scheduleRepeating(messagePollPeriod);
//		UserDTO player = GameInfo.getInstance().getPlayer();
//		if(player==null) {
//			this.commsTabPanel.getTabBar().setTabText(1, "Team");
//		} else {
//			this.commsTabPanel.getTabBar().setTabText(1, player.getUsername() + "'s Team");
//		}
	}
	
	public CommsPanel() {
		this.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

		hiddenPanel.setVisible(false);
		this.add(hiddenPanel);
		
		outgoingMsgBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				if(emptyMessageBox) {
					outgoingMsgBox.setText("");
					emptyMessageBox = false;
				}
			}
		});

		outgoingMsgBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if(outgoingMsgBox.getText().isEmpty()) {
					emptyMessageBox = true;
					outgoingMsgBox.setText(EMPTY_MESSAGE_BOX_TEXT);
				}
			}
		});

		outgoingMsgBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode()==KeyCodes.KEY_ENTER) {
					outgoingSendButton.click();
					event.stopPropagation();
				}
				while(outgoingMsgBox.getText().matches("\\s.*")) {
					outgoingMsgBox.setText(outgoingMsgBox.getText().substring(1));
				}
				if (outgoingMsgBox.getText().length() > MAX_SENT_CHARS) {
					outgoingMsgBox.setText(outgoingMsgBox.getText().subSequence(0, MAX_SENT_CHARS).toString());
				}
			}
		});

		outgoingSendButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String message = outgoingMsgBox.getText();
				if(sendMessage(message)) {
					outgoingMsgBox.setText("");
				}
				outgoingMsgBox.setText("");
			}
		});

//		commandButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				processCommand();
//			}
//		});
//		commandButton.setEnabled(false);

		logoutButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				sendLogout();
			}
		});

//		refreshButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				CommsPanel.this.clearMessages();
////				GameInformation.getInstance().refreshAll(true);
//			}
//		});

//		incomingMsgScrollPanel.add(allMessagesLabel);
		incomingMsgScrollPanel.add(messagesPanel);
		VerticalPanel messagesVerticalPanel = new VerticalPanel();
		messagesVerticalPanel.add(incomingMsgScrollPanel);
		messagesVerticalPanel.add(outgoingMsgBox);
		messagesVerticalPanel.add(outgoingSendButton);
		
//		this.neighborsBox.setVisibleItemCount(10);
//		neighborsScrollPanel.add(neighborsBox);

//		Button tradeButton = new Button("Trade");
//		tradeButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				//TODO: Change the username key to a proper ID.
//				int neighborNum = neighborsBox.getSelectedIndex();
//				UserDTO neighbor = GameInformation.getInstance().getPlayer().getNeighbors().get(neighborNum);
//				openTradeDialog(neighbor);
//			}
//		});
		
//		VerticalPanel neighborsVerticalPanel = new VerticalPanel();
//		neighborsVerticalPanel.add(neighborsScrollPanel);
//		neighborsVerticalPanel.add(tradeButton);
		
//		this.commsTabPanel.add(messagesVerticalPanel, "Messages");
//		this.commsTabPanel.add(neighborsVerticalPanel, "Neighbors");
		this.add(messagesVerticalPanel);
//		this.commsTabPanel.selectTab(0);
//		this.add(commsTabPanel);
//		this.add(commandButton);
		this.add(logoutButton);
//		this.add(refreshButton);

		// Create repeating process to update messages at some interval
//		messageService = GameServices.messageService;
		messageService = GameServices.messageService;
		updateTimer = new Timer() {
			@Override
			public void run() {
				if(WindowInformation.inAjaxCall) {
					return;
				}
				if(GameInfo.getInstance().getPlayer()==null) {
					return;
				}
//				AsyncCallback<List<MessageDTO>> callback = new AsyncCallback<List<MessageDTO>>() {
//
//					@Override
//					public void onFailure(Throwable caught) {
//						Window.alert("Error while calling Message Service: " + caught.getMessage());
//					}
//
//					@Override
//					public void onSuccess(List<MessageDTO> result) {
//						for (MessageDTO m : result) {
//							if(m.getTimeSent().getTime()<=lastMessageTime.getTime()) {
//								continue;
//							}
//							addMessage(m);
//							long a = lastMessageTime.getTime();
//							long b = m.getTimeSent().getTime();
//							long c = Math.max(a,b);
//							
//							lastMessageTime = new Date(c);
//						}
//					}
//				};
//				messageService.getMessages(GameInformation.getInstance().getPlayer(), lastMessageTime, callback);
				AsyncCallback<CommStruct> callback = new AsyncCallback<CommStruct>() {

					@Override
					public void onFailure(Throwable caught) {
						WindowInformation.inAjaxCall = false;
						//Window.Location.reload();
						//***Window.alert("Error while calling Message Service: " + caught.getMessage());
					}

					@Override
					public void onSuccess(CommStruct commStruct) {
						WindowInformation.inAjaxCall = false;
						if(commStruct.logOffPlayer) {
//							Window.alert("Game is being reset - please wait for a few seconds while the system logs you back on");

//							WindowInformation.commsPanel.clearContent();
//							WindowInformation.loginPanel.clearContent();
//							WindowInformation.treasurePanel.clearContent();
//							WindowInformation.locationPanel.clearContent();
//							MapUtil.clearMap();
//							WindowInformation.loginPanel.select();
//							WindowInformation.newUserWindow.clearContent();
//
//							GameInfo.getInstance().refreshAll(true);

							
//							UserDTO player = GameInfo.getInstance().getPlayer();
//							final UserDTO playerCopy = new UserDTO();
//							playerCopy.setUsername(player.getUsername());
//							playerCopy.setAuthCode(player.getAuthCode());
							WindowInformation.reset();
							GameInfo.getInstance().reset();
//							GameInfo.getInstance().setPlayer(playerCopy);

							
//							Window.alert("4Game is being reset! And player's authcode is " + playerCopy.getAuthCode());

							new Timer() {
								@Override
								public void run() {
									WindowInformation.loginPanel.sendLogin(WindowInformation.loginPanel.username, WindowInformation.loginPanel.password);
//									Window.alert("5Game is being reset (in timer)! And player's authcode is " + playerCopy.getAuthCode());
//									WindowInformation.loginPanel.sendRefreshLogin(playerCopy);
								}
							}.schedule(1000);
							updateTimer.cancel();
							return;
						}
						
						for (MessageDTO message : commStruct.messages) {
							/*if(message.getTimeSent().getTime()<=lastMessageTime.getTime()) {
								continue;
							}*/
							addMessage(message);
							/*long a = lastMessageTime.getTime();
							long b = message.getTimeSent().getTime();
							long c = Math.max(a,b);
							
							lastMessageTime = new Date(c);*/
						}
						
						for (GeoGameCommandDTO command : commStruct.commands) {
							switch(command.getCommandType()) {
							case SHOW_SYNONYMS:
								showLanguageDisplay();
								break;
							}
						}
//						boolean refreshFlag = false;

//						UserDTO oldPlayer = GameInformation.getInstance().getPlayer();
//						if(oldPlayer.getLatitude()!=commStruct.player.getLatitude() || oldPlayer.getLongitude()!=commStruct.player.getLongitude()) {
//							refreshFlag = true;
//						}
//						WindowInformation.treasurePanel.log(GameInfo.getInstance().isGameStarted() + " - " + commStruct.gameStarted + GameInfo.getInstance().isGameFinished() + " - " + commStruct.gameFinished);
						if(!GameInfo.getInstance().isGameStarted() && commStruct.gameStarted) {
							
							Date newDate = new Date(zeroDate.getTime()+commStruct.timeRemaining);
							MapUtil.refreshMap();
							// Disabled the pop-ups for debugging purposes. Will re-enable once Selenium testing is complete.
							// -JV 03/16/2010
							if (GameInfo.getInstance().DEBUG_MODE) {
								WindowInformation.treasurePanel.log("Game has started!\nTime remaining: " + new SimpleDateFormat("HH:mm:ss").format(newDate));
							}
							else {
								InfoWindow alert = new InfoWindow(200, 100, "Game has started!\nTime remaining: " + new SimpleDateFormat("HH:mm:ss").format(newDate));
								alert.center();
							}
							new Timer() {
								@Override
								public void run() {
									MapUtil.setZoomLevel(9);
								}
							}.schedule(800);
						}
						if(GameInfo.getInstance().isGameFinished() && !commStruct.gameFinished) {
							Date newDate = new Date(zeroDate.getTime()+commStruct.timeRemaining);
							MapUtil.refreshMap();
							// Disabled the pop-ups for debugging purposes. Will re-enable once Selenium testing is complete.
							// -JV 03/16/2010
							if (GameInfo.getInstance().DEBUG_MODE) {
								WindowInformation.treasurePanel.log("Game has started!\nTime remaining: " + new SimpleDateFormat("HH:mm:ss").format(newDate));
							}
							else {
								InfoWindow alert = new InfoWindow(200, 100, "Game has started!\nTime remaining: " + new SimpleDateFormat("HH:mm:ss").format(newDate));
								alert.center();
							}
							
							new Timer() {
								@Override
								public void run() {
									MapUtil.setZoomLevel(9);
								}
							}.schedule(800);
						}

						if(!GameInfo.getInstance().isGameFinished() && commStruct.gameFinished) {
							//Window.alert("Game has ended!");
							if (GameInfo.getInstance().DEBUG_MODE) {
								WindowInformation.treasurePanel.log("Game has finished!");
							}
							else {
								String infoText = "";
								boolean AMTVisitor = GameInfo.getInstance().getPlayer().getAMTVisitor();
								
								if (AMTVisitor == false) {
									infoText = "Game has finished!";
								}
								else {
									String AMTCode = GameInfo.getInstance().getPlayer().getAMTCode();
									infoText = "Game has finished! Here is you Amazon Mechanical Turk code:\n" + AMTCode;
								}
								InfoWindow alert = new InfoWindow(200, 100, infoText);
								alert.center();
							}
							InfoWindow surveyAlert = new InfoWindow(200, 100, "<a target='_blank' href='http://tinyurl.com/cmu-geogame-posttest'>Please click the link to fill a short survey</a>");
							surveyAlert.center();
						}
						GameInfo.getInstance().setGameStarted(commStruct.gameStarted);
						GameInfo.getInstance().setGameFinished(commStruct.gameFinished);
						
						
						boolean wasMoving = GameInfo.getInstance().getPlayer().isMoving();
						boolean isMoving = commStruct.player.isMoving();
//						if(wasMoving!=isMoving) {
//							WindowInformation.treasurePanel.log("wasMoving is " + wasMoving);
//							WindowInformation.treasurePanel.log("isMoving is " + isMoving);
//						}
						LocationDTO wasDestination=wasMoving ? GameInfo.getInstance().getPlayer().getDestination() : null;
						
//						boolean scoreChanged = GameInfo.getInstance().getPlayer().getScore()!=commStruct.player.getScore();
//						if(scoreChanged) {
//							WindowInformation.treasurePanel.log("The score has changed! (Should never happen, as it is... right now, defectuous...) From " + GameInfo.getInstance().getPlayer().getScore() + " to " + commStruct.player.getScore());
//						}
						
//						if(!commStruct.scoreMessages.isEmpty()) {
//							WindowInformation.treasurePanel.log("A score message came in! Score before: " + GameInfo.getInstance().getPlayer().getScore() + "... after: " + commStruct.player.getScore());
//						}

						GameInfo.getInstance().updatePlayerInformation(commStruct.player);
//						if(!commStruct.scoreMessages.isEmpty()) {
//							WindowInformation.treasurePanel.log("And now player's score is: " + GameInfo.getInstance().getPlayer().getScore());
//						}
						GameInfo.getInstance().setScores(commStruct.scorePlayers);
//						boolean isMoving = GameInfo.getInstance().getPlayer().isMoving();
						
						LocationDTO currentLocation = commStruct.player.getCurrentLocation();
							
						
						if(wasMoving && !isMoving && currentLocation!=null && wasDestination.getId()==currentLocation.getId()) {
//							WindowInformation.treasurePanel.log("wasMoving && !isMoving!!!" + "... but...wasDestination.getId()=" + wasDestination.getId() + " and currentLocation.getId() is" + currentLocation.getId());
							WindowInformation.treasurePanel.log("You have reached " + GameInfo.getInstance().getPlayer().getCurrentLocation().getName());
							WindowInformation.locationPanel.refresh();
						}
						
						WindowInformation.treasurePanel.updateGameTimer(Math.max(0, commStruct.timeRemaining),commStruct.gameDuration);
//						WindowInformation.treasurePanel.refreshScoreGrid();
//						WindowInformation.treasurePanel.refreshScore();
//						MapUtil.updatePosition(commStruct.player.getCurrentLocation());
						
						MapUtil.updatePosition(commStruct.player.getLatitude(), commStruct.player.getLongitude());
						if(commStruct.player.isMoving()) {
//							WindowInformation.locationPanel.setNoLocation();
							WindowInformation.locationPanel.refresh();
						}

						for(ScoreMessage scoreMessage : commStruct.scoreMessages) {
							WindowInformation.treasurePanel.log(scoreMessage.getActor().getUsername() + " found an item! You gain " + scoreMessage.getScoreObtained() + " points.");
							WindowInformation.treasurePanel.log("Your score is now " + GameInfo.getInstance().getPlayer().getScore());
//							WindowInformation.treasurePanel.log(scoreMessage.getActor().getUsername() + " found an item! You gain " + scoreMessage.getScoreObtained() + " points.");
//							GameInfo.getInstance().setNeedsRefresh(true);
//							WindowInformation.treasurePanel.log("Score changed: " + (scoreChanged ? "Yes" : "No"));
//							if(!scoreChanged) {
//								WindowInformation.treasurePanel.log("But the score did not change!!");
//								GameInfo.getInstance().getPlayer().setScore(GameInfo.getInstance().getPlayer().getScore()+scoreMessage.getScoreObtained());
//							}
						}
						WindowInformation.treasurePanel.refreshScore();
					}
				};
//				messageService.getMessages(GameInformation.getInstance().getPlayer(), lastMessageTime, callback);
				WindowInformation.inAjaxCall = true;
				//AsyncCallback <void> call
				messageService.getMessagesNew(GameInfo.getInstance().getPlayer(), lastMessageTime, GameInfo.getInstance().isNeedsRefresh(), callback);
				GameInfo.getInstance().setNeedsRefresh(false);
				
				//Timeout for the call - if it takes more than "10 seconds", release the lock on AJAX Calls.
				new Timer() {
					@Override
					public void run() {
						WindowInformation.inAjaxCall = false;
					}
				}.schedule(WindowInformation.AJAX_TIMEOUT);
			}
		};

//		updateTimer.scheduleRepeating(messagePollPeriod);
	}

	@Override
	public void setWidth(String width) {
		super.setWidth(width);

		outgoingMsgBox.setWidth(width);
		outgoingSendButton.setWidth(width);
		incomingMsgScrollPanel.setWidth(width);
//		allMessagesLabel.setWidth(width);
		messagesPanel.setWidth(width);
	}

	@Override
	public void setHeight(String height) {
		super.setHeight(height);

//		outgoingMsgBox.setHeight("100px");
//		outgoingSendButton.setHeight("30px");
//		incomingMsgScrollPanel.setHeight("420px");
//		allMessagesLabel.setHeight("380px");
		outgoingMsgBox.setHeight("100px");
		outgoingSendButton.setHeight("30px");
		incomingMsgScrollPanel.setHeight("420px");
//		allMessagesLabel.setHeight("380px");
		messagesPanel.setHeight("380px");
	}

	public void addMessage(MessageDTO msg) {
		incomingMsgScrollPanel.setTitle("Messages");
		if(msg.isBroadcast()) {
			String sender = msg.getSender();
			
			messagesPanel.addMessage("<b>" + msg.getSender() + "</b>"
	//				+ "@" + fmt.format(msg.getTimeSent())
					+ ": " + msg.getContent());
		} else {
			messagesPanel.addMessage("<b>(Private) " + msg.getSender() + " to " + msg.getReceivers().get(0) + "</b>"
					//				+ "@" + fmt.format(msg.getTimeSent())
									+ ": " + msg.getContent());
		}
	}

	public void clearMessages() {
//		allMessagesLabel.setText("");
		messagesPanel.clearMessages();
	}

	public boolean sendMessage(String msg) {
		String target = null;
//		WindowInformation.treasurePanel.log("Sending a message!");
		if(msg.substring(0,1).equals("/")) {
//			WindowInformation.treasurePanel.log("It had a slash at the beginning!");
			String[] pieces = msg.split(" ", 2);
//			WindowInformation.treasurePanel.log("1");
			if(pieces[1]==null) {
//				WindowInformation.treasurePanel.log("2");
				return false;
			} else {
				msg = pieces[1];
			}
//			WindowInformation.treasurePanel.log("3");
			target = pieces[0].substring(1);
//			WindowInformation.treasurePanel.log("Sending a private message to " + target + ": " + msg);
//			WindowInformation.treasurePanel.log("4");
			boolean neighborExists = false;
			for(UserDTO neighbor : GameInfo.getInstance().getPlayer().getNeighbors()) {
				if(neighbor.getUsername().equals(target)) {
					neighborExists = true;
				}
			}
			if(!neighborExists) {
				Window.alert("Neighbor " + target + " doesn't exist!");
				
//				WindowInformation.treasurePanel.log("5");
//				StringBuilder sb = new StringBuilder();
//				for(UserDTO neighbor : GameInfo.getInstance().getPlayer().getNeighbors()) {
//					sb.append(neighbor.getUsername());
//				}
//				WindowInformation.treasurePanel.log("5" + sb.toString());
				return false;
			}
//			WindowInformation.treasurePanel.log("6");
		}
		
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				//***Window.alert("Error while calling Message Service.");
				//Window.Location.reload();
			}

			@Override
			public void onSuccess(Void result) {
				CommsPanel.this.updateTimer.cancel();
				CommsPanel.this.updateTimer.run();
				CommsPanel.this.updateTimer.scheduleRepeating(CommsPanel.messagePollPeriod);
			}
		};

		MessageDTO newMessage = new MessageDTO();
		newMessage.setId(-1L);
		newMessage.setSender(GameInfo.getInstance().getPlayer().getUsername());
		if(target==null) {
			//For now, only broadcast implemented
			newMessage.setBroadcast(true);
		} else {
			List<String> receivers = new ArrayList<String>();
			receivers.add(target);
			newMessage.setReceivers(receivers);
		}
		//leaves timeSent empty - client time is unreliable
		newMessage.setContent(msg);
		
//		messageService.sendMessage(GameInformation.getInstance().getPlayer(), newMessage, callback);
		messageService.sendMessageNew(GameInfo.getInstance().getPlayer(), newMessage, callback);
		return true;
	}
	
	public void sendLogout() {
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {
				//***Window.alert("Error while calling Login Service: " + caught.getMessage());
				WindowInformation.reset();
				GameInfo.getInstance().reset();
			}

			public void onSuccess(Boolean success) {
				if(success) {
					WindowInformation.reset();
					GameInfo.getInstance().reset();
					Cookies.setCookie("username", null);
					Cookies.setCookie("password", null);
					MapUtil.setStandbyMap();
				} else {
					Window.alert("Log out failed");
				}
			}
		};
		GameServices.loginService.sendLogout(GameInfo.getInstance().getPlayer(), callback);
	}

	public void appendToChatWindow(String text) {
		if(this.emptyMessageBox) {
			this.emptyMessageBox = false;
			this.outgoingMsgBox.setText("");
		}
		this.outgoingMsgBox.setText(this.outgoingMsgBox.getText() + text);
		this.outgoingMsgBox.setCursorPos(this.outgoingMsgBox.getText().length());
	}
	
	public void focusOnChat() {
		this.outgoingMsgBox.setFocus(true);
	}
	
	public void clickOutgoingSendButton () {
		this.outgoingSendButton.click();
	}
	
	public void showLanguageDisplay() {
		InfoWindow info = new InfoWindow();
		info.setContent("When you click OK you will see about a dozen items available to you in the game. Please pay close attention to all of them");
		info.center();
		info.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				// TODO Auto-generated method stub
				LanguageDisplay languageDisplay = WindowInformation.languageDisplay;
				languageDisplay.setTitle("Item Types");
				languageDisplay.init();
			}
			
		});
	}
//	private void processCommand() {
//		String commandString = Window.prompt("Command:","");
//		
//		AsyncCallback<ActionResult> callback = new AsyncCallback<ActionResult>() {
//			public void onFailure(Throwable caught) {
//				//***Window.alert("Command failed - " + caught.getMessage());
//			}
//			
//			public void onSuccess(ActionResult result) {
//				if(result.isSuccess()) {
//					GameInformation.getInstance().updateInformation(result.getGameInformation());
//					GameInformation.getInstance().refreshAll(false);
//				}
//			}
//		};
////		GameServices.gameService.sendCommand(GameInformation.getInstance().getPlayer(),commandString, callback);
//	}

//	public void openTradeDialog(UserDTO player2) {
//		TradeWindow tradeDialog = WindowInformation.tradeWindow;
//		tradeDialog.setTitle("Trade with " + player2.getUsername());
//		tradeDialog.show();
//		tradeDialog.setVisible(true);
//		//tradeDialog.setHeight("500px");
//		//tradeDialog.setWidth("500px");
//		//tradeDialog.setPixelSize(500, 500);
//		tradeDialog.setPopupPosition(50, 50);
//		tradeDialog.refresh(GameInformation.getInstance().getPlayer(), player2);
//	}
}