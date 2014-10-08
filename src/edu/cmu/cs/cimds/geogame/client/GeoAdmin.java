package edu.cmu.cs.cimds.geogame.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.gwtwidgets.client.ui.pagination.DataProvider;
import org.gwtwidgets.client.util.SimpleDateFormat;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.json.client.*;
import com.google.gwt.user.client.DOM;

import edu.cmu.cs.cimds.geogame.client.model.dto.AcceptanceFormDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.GeoGameCommandDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.RoadDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.model.enums.CommandType;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;
import edu.cmu.cs.cimds.geogame.client.ui.AdminMessageWindow;
import edu.cmu.cs.cimds.geogame.client.ui.ChatPanel;
import edu.cmu.cs.cimds.geogame.client.ui.CreateUserNetworkWindow;
import edu.cmu.cs.cimds.geogame.client.ui.FormsAdminWindow;
import edu.cmu.cs.cimds.geogame.client.ui.InfoWindow;
import edu.cmu.cs.cimds.geogame.client.ui.LoginPanel;
import edu.cmu.cs.cimds.geogame.client.ui.PlayerEntry;
import edu.cmu.cs.cimds.geogame.client.ui.ProgressBar;
import edu.cmu.cs.cimds.geogame.client.ui.ServerSettingsWindow;
import edu.cmu.cs.cimds.geogame.client.ui.SynonymsWindow;
import edu.cmu.cs.cimds.geogame.client.ui.WindowInformation;

public class GeoAdmin implements EntryPoint {

	public GeoAdmin() { }
	
	/**
	 * The entry point method, called automatically by loading a module
	 * that declares an implementing class as an entry-point
	 */
	private static Timer refreshTimer;
	private static CheckBox pollCheckBox = new CheckBox();
	//private static Grid playerTable = new Grid();
	private static CellTable<PlayerEntry> playerTable;
	private static AsyncDataProvider<PlayerEntry> dataProvider;
	private static Grid distanceListGrid = new Grid();
	private static ChatPanel logPanel = new ChatPanel();
	private static final int REFRESH_PERIOD_MILLIS = 1000;
	private static final MultiSelectionModel <PlayerEntry> selectionModel = new MultiSelectionModel<PlayerEntry>();

	private static ProgressBar timerBar = new ProgressBar();
	private static Label timeRemainingLabel = new Label();
	private static Label gameStatusLabel = new Label();
	private static Label usersReadyLabel = new Label();
	private static long timeRemaining;
	private static long gameDuration;
	private static boolean gameStarted;
	private static boolean gameFinished;
	
	//private static Button createUserNetworkButton;
	
	private static ServerSettingsWindow settingsWindow = new ServerSettingsWindow();

	public void onModuleLoad() {

		final LoginPanel loginPanel = new LoginPanel();
		final VerticalPanel vPanel = new VerticalPanel();
		
		
		
		vPanel.ensureDebugId("vPanel1");
		
		settingsWindow.init();
		settingsWindow.hide();
		
		loginPanel.setWidth("200px");
		loginPanel.setHeight("600px");
		
		MenuBar GameMenu = new MenuBar(true);
		MenuBar SettingsMenu = new MenuBar(true);
		MenuBar CommandMenu = new MenuBar(true);
		
		GameMenu.ensureDebugId("gameMenu");
		SettingsMenu.ensureDebugId("settingsMenu");
		CommandMenu.ensureDebugId("commandMenu");
		
		usersReadyLabel.setStylePrimaryName("usersNotReady");
		
		ProvidesKey<PlayerEntry> keyProvider = new ProvidesKey<PlayerEntry>() {
			@Override
			public Long getKey(PlayerEntry player) {
				return player.getId();
			}
		};
		
		playerTable = new CellTable<PlayerEntry>(keyProvider);
		createPlayerTable();
		
		
		Command changeSettings = new Command() {

			@Override
			public void execute() {
				settingsWindow.center();
			}
			
		};
		
		Command itemSynonyms = new Command() {
			@Override
			public void execute() {
				SynonymsWindow synWindow = new SynonymsWindow();
				synWindow.init();
				synWindow.center();
			}
		};
		
		Command resetGame = new Command() {
			@Override
			public void execute() {
				final InfoWindow confirm = new InfoWindow(200, 100, "ARE YOU ENTIRELY SURE YOU WANT TO RESET THE DB??", true);
				confirm.center();
				confirm.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						// TODO Auto-generated method stub
						if (confirm.getChoice()) {
							GameServices.devService.resetAllDB(settingsWindow.serverSettings, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									log("Failure! - 3 - " + caught.getMessage());
									log("Error occurred in GameServices.devService.resetAllDB");
								}
								@Override
								public void onSuccess(Void result) {
									log("Reset successful!");
									drawUserGraph();
								}
							});
						}
					}
				});
			}
		};
		
		Command startGame = new Command() {
			@Override
			public void execute() {
			
				GameServices.gameService.startGameTimer(settingsWindow.serverSettings.getGameDuration(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						log("Failure! - 4 - " + caught.getMessage());
						log("Error occurred in GameServices.gameService.startGameTimer");
					}
					@Override
					public void onSuccess(Void result) {
						log("Game started successfully!");
					}
				});
			}
		};
		
		Command changeForms = new Command () {
			@Override
			public void execute() {
				openFormsAdminDialog();
			}
		};
		
		Command createUserNetwork = new Command() {
			@Override
			public void execute() {
				openCreateUserNetworkDialog();
			}
		};
		
		Command sendAdminMessage = new Command() {
			@Override
			public void execute() {
				openAdminMessageDialog();
			}
		};
		
		Command showSynonymsToUsers = new Command() {
			@Override
			public void execute() {
				UserDTO admin = GameInfo.getInstance().getPlayer();
				GeoGameCommandDTO command = new GeoGameCommandDTO();
				command.setCommandType(CommandType.SHOW_SYNONYMS);
				command.setSender(admin.getId());
				
				//List<UserDTO> targetPlayers = new ArrayList<UserDTO>();
				Set<PlayerEntry> targetEntries = selectionModel.getSelectedSet();
				for (PlayerEntry player: targetEntries) {
					command.addTargetUser(player.getPlayer());
				}
				
				//Window.alert(command.getTargetUsers().toString());
				
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						InfoWindow alert = new InfoWindow(200, 100, "Synonyms display failed!");
						alert.center();
					}

					@Override
					public void onSuccess(Void result) {
						InfoWindow alert = new InfoWindow(200, 100, "Users are seeing the synonym display!");
						alert.center();
					}
					
				};
				GameServices.messageService.sendAdminCommand(admin, command, callback);
			}
		};
		
		Command redrawGraph = new Command() {
			@Override
			public void execute() {
				drawUserGraph();
			}
		};
		
		Command startPeriodicGame = new Command() {
			@Override
			public void execute() {
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						InfoWindow alert = new InfoWindow(200, 100, "Start periodic game failed!");
						alert.center();
					}
					
					@Override
					public void onSuccess(Void result) {
						InfoWindow alert = new InfoWindow(200, 100, "Periodic game is now running!");
						alert.center();
					}
				};
				GameServices.gameService.startPeriodicGame(callback);
			}
		};
		
		Command stopGameTimer = new Command() {
			@Override
			public void execute() {
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						InfoWindow alert = new InfoWindow(200,100, "Failed to stop the game timer! Something has gone very wrong!");
						alert.center();
					}
					
					@Override
					public void onSuccess(Void result) {
						InfoWindow alert = new InfoWindow(200, 100, "Game timer is stopped!");
						alert.center();
					}
				};
				GameServices.gameService.stopGameTimer(callback);
			}
		};
		
		MenuItem resetGameItem = new MenuItem("Reset game", resetGame);
		MenuItem startGameItem = new MenuItem("Start game", startGame);
		MenuItem stopGameTimerItem = new MenuItem("Stop game timer", stopGameTimer);
		MenuItem changeSettingsItem = new MenuItem("Change game settings", changeSettings);
		MenuItem itemSynonymsItem = new MenuItem("Change item synonyms", itemSynonyms);
		MenuItem changeFormsItem = new MenuItem("Change forms", changeForms);
		MenuItem createUserNetworkItem = new MenuItem("Create user network", createUserNetwork);
		MenuItem sendAdminMessageItem = new MenuItem("Send admin message", sendAdminMessage);
		MenuItem redrawGraphItem = new MenuItem("Redraw user graph", redrawGraph);
		MenuItem showSynonymsItem = new MenuItem("Show synonyms to players", showSynonymsToUsers);
		MenuItem startPeriodicGameItem = new MenuItem("Start periodic game", startPeriodicGame);
		
		
		resetGameItem.ensureDebugId("resetGame");
		startGameItem.ensureDebugId("startGame");
		changeSettingsItem.ensureDebugId("changeSettings");
		itemSynonymsItem.ensureDebugId("itemSynonyms");
		changeFormsItem.ensureDebugId("changeForms");
		createUserNetworkItem.ensureDebugId("createUserNetwork");
		sendAdminMessageItem.ensureDebugId("sendAdminMessage");
		redrawGraphItem.ensureDebugId("redrawGraph");
		showSynonymsItem.ensureDebugId("showSynonyms");
		
		
		GameMenu.addItem(resetGameItem);
		GameMenu.addItem(startGameItem);
		GameMenu.addSeparator();
		GameMenu.addItem(startPeriodicGameItem);
		GameMenu.addItem(stopGameTimerItem);
		
		SettingsMenu.addItem(changeSettingsItem);
		SettingsMenu.addItem(itemSynonymsItem);
		SettingsMenu.addItem(changeFormsItem);
		
		CommandMenu.addItem(createUserNetworkItem);
		CommandMenu.addItem(sendAdminMessageItem);
		CommandMenu.addItem(redrawGraphItem);
		CommandMenu.addSeparator();
		CommandMenu.addItem(showSynonymsItem);
		
		MenuBar MainMenuBar = new MenuBar();
		MainMenuBar.addItem("Game", GameMenu);
		MainMenuBar.addItem("Settings", SettingsMenu);
		MainMenuBar.addItem("Commands", CommandMenu);
		
		MainMenuBar.ensureDebugId("mainMenuBar");
		
		//playerTable.setBorderWidth(1);
		
		
		
		//WindowInformation.loginPanel.maybeLogInThroughCookies();
		
		/*vPanel.add(networkTypeList);
		vPanel.add(minTTLabel);
		vPanel.add(minTravelTime);
		vPanel.add(maxTTLabel);
		vPanel.add(maxTravelTime);
		vPanel.add(new Label("Graph Density"));
		vPanel.add(graphDensityTextBox);
		
		vPanel.add(changeSettings);
		vPanel.add(itemSynonymsButton);
		vPanel.add(resetButton);
		vPanel.add(startGameButton);
		vPanel.add(formsAdminButton);
		vPanel.add(createUserNetworkButton);
		vPanel.add(adminMessageButton);*/
		vPanel.add(usersReadyLabel);
		vPanel.add(playerTable);
		vPanel.add(timerBar);
		vPanel.add(timeRemainingLabel);
		vPanel.add(gameStatusLabel);
		//vPanel.add(distanceListGrid);

		VerticalPanel vPanel2 = new VerticalPanel();
		logPanel.setMaxMessages(40);
		vPanel2.add(logPanel);
		
		pollCheckBox.setText("Poll server");
		pollCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(pollCheckBox.getValue()) {
					refreshTimer.scheduleRepeating(REFRESH_PERIOD_MILLIS);
				} else {
					refreshTimer.cancel();
				}
			}
		});
		vPanel2.add(pollCheckBox);

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(vPanel);
		hPanel.add(vPanel2);
		
		WindowInformation.adminRootPanel = RootPanel.get();
		WindowInformation.loginPanel = loginPanel;
		WindowInformation.loginPanel.select();
		WindowInformation.adminPanel = hPanel;
		
		RootPanel.get().add(WindowInformation.loginPanel);
		RootPanel.get().add(MainMenuBar);
		
		//RootPanel.get().add(playerTable);
		
		setRefreshPlayersTimer();
		//refreshPlayersView();
		refreshDistanceList();
		
		//RootPanel.get().add(hPanel);
		
		/*setRefreshPlayersTimer();
		refreshPlayers();
		refreshDistanceList();*/
		
		WindowInformation.loginPanel.select();
		WindowInformation.loginPanel.maybeAdminLoginThroughCookies();
	}
	
	private static void createPlayerTable() {
		
		TextColumn<PlayerEntry> nameCol = new TextColumn<PlayerEntry>() {
			@Override
			public String getValue(PlayerEntry player) {
				return player.getPlayerName();
			}
		};
		
		TextColumn<PlayerEntry> locationCol = new TextColumn<PlayerEntry>() {
			@Override
			public String getValue(PlayerEntry player) {
				return player.getPlayerLocation();
			}
		};
		
		TextColumn<PlayerEntry> collectedItemsCol = new TextColumn<PlayerEntry>() {
			@Override
			public String getValue(PlayerEntry player) {
				return String.valueOf(player.getCollectedItems());
			}
		};
		
		TextColumn<PlayerEntry> numNeighborsCol = new TextColumn<PlayerEntry>() {

			@Override
			public String getValue(PlayerEntry player) {
				return String.valueOf(player.getNumNeighbors());
			}
		};
		
		TextColumn<PlayerEntry> scoreCol = new TextColumn<PlayerEntry>() {

			@Override
			public String getValue(PlayerEntry player) {
				return String.valueOf(player.getScore());
			}
		};
		
		TextColumn<PlayerEntry> loggedInCol = new TextColumn<PlayerEntry>() {

			@Override
			public String getValue(PlayerEntry player) {
				return player.isLoggedIn();
			}
		};
		
		TextColumn<PlayerEntry> statusCol = new TextColumn<PlayerEntry>() {
			@Override
			public String getValue(PlayerEntry player) {
				return player.getStatus();
			}
		};
		
		TextColumn<PlayerEntry> goalItemCol = new TextColumn<PlayerEntry>() {
			@Override
			public String getValue(PlayerEntry player) {
				return player.getGoalItem();
			}
		};
		
		playerTable.setSelectionModel(selectionModel, DefaultSelectionEventManager.<PlayerEntry> createCheckboxManager());
		
		Column<PlayerEntry, Boolean> checkedCol = new Column<PlayerEntry, Boolean>(new CheckboxCell(true)) {
			@Override
			public Boolean getValue(PlayerEntry player) {
				return playerTable.getSelectionModel().isSelected(player);
			}
		};
		
		checkedCol.setFieldUpdater(new FieldUpdater<PlayerEntry, Boolean>() {

			@Override
			public void update(int index, PlayerEntry player, Boolean state) {
				// TODO Auto-generated method stub
				//Window.alert(String.valueOf(index));
				//Window.alert(String.valueOf(player));
				//Window.alert(String.valueOf(state));
				playerTable.getSelectionModel().setSelected(player, state);
			}
			
		});
		
		playerTable.addColumn(nameCol, "Name");
		playerTable.addColumn(locationCol, "Location");
		playerTable.addColumn(collectedItemsCol, "Items collected");
		playerTable.addColumn(numNeighborsCol, "Number of neighbors");
		playerTable.addColumn(loggedInCol, "Logged in?");
		playerTable.addColumn(scoreCol, "Score");
		playerTable.addColumn(statusCol, "Status");
		playerTable.addColumn(goalItemCol, "Goal item");
		playerTable.addColumn(checkedCol, "Selected");
		
		dataProvider = new AsyncDataProvider<PlayerEntry>() {

			@Override
			protected void onRangeChanged(HasData<PlayerEntry> display) {
				// TODO Auto-generated method stub
				AsyncCallback<List<UserDTO>> callback = new AsyncCallback<List<UserDTO>>() {
					@Override
					public void onFailure(Throwable caught) {
						if(!"Game is resetting".equals(caught.getMessage())) {
							log("Failure! - 5 - " + caught.getMessage());
							log("Error occurred in refreshPlayers");
						}
					}

					@Override
					public void onSuccess(List<UserDTO> players) {
						//playerDataTable.resize(players.size()+1, 8);

						int numUsersNotReady = 0;
						
						List<PlayerEntry> playerTableList = new ArrayList<PlayerEntry>();
						
						for(int i = 0; i < players.size(); i++) {
							String string;
							UserDTO player = players.get(i);
							PlayerEntry playerEntry = new PlayerEntry();
							
							playerEntry.setPlayer(player);
							playerEntry.setId(player.getId());
							playerEntry.setPlayerName(player.getUsername());
							
							if(player.getCurrentLocation()!=null) {
								string = player.getCurrentLocation().getName();
							} else {
								RoadDTO road = player.getCurrentRoad();
								LocationDTO source = player.getForward() ? road.getLocation1() : road.getLocation2();
								LocationDTO destination = player.getForward() ? road.getLocation2() : road.getLocation1();
								
								string = source.getName() + " -> " + destination.getName();
							}
							
							playerEntry.setPlayerLocation(string);
							playerEntry.setCollectedItems(player.getInventory().size());
							playerEntry.setNumNeighbors(player.getNeighbors().size());
							
							if(player.isSeemsActive()) {
								string = "YES";
							} else {
								string = "NO";
							}
							
							playerEntry.setLoggedIn(string);
							playerEntry.setScore(player.getScore());
							
							AcceptanceFormDTO currentForm = player.getCurrentAcceptanceFormDTO();
							if(currentForm!=null) {
								playerEntry.setStatus(currentForm.getName());
							} else {
								playerEntry.setStatus("In Game");
							}
							
							ItemTypeDTO goalItem = player.getCurrentGoalItemType();
							if(goalItem!=null) {
								playerEntry.setGoalItem(goalItem.getName());
							} else {
								playerEntry.setGoalItem("");
							}
							
							if(currentForm!=null || !player.isSeemsActive()) {
								numUsersNotReady++;
							}
							
							playerTableList.add(playerEntry);
						}
						
						//List<PlayerEntry> playersToUpdate = new ArrayList<PlayerEntry>();
						
						//playerTable.setRowData(0, playerTableList);
						//for (PlayerEntry currentPlayer : playerTable.getVisibleItems()) {
						//	for (PlayerEntry dbPlayer : playerTableList) {
						//		
						//	}
						//}
						
						Set<PlayerEntry> selectedPlayers = selectionModel.getSelectedSet();
						//String str = StringUtils.join(selectedPlayers, "; ");
						//log(str);
						
						updateRowData(0, playerTableList);
						
						for (PlayerEntry selectedPlayer : selectedPlayers) {
							selectionModel.setSelected(selectedPlayer, true);
						}
						
						if(numUsersNotReady==0) {
							usersReadyLabel.setText("ALL " + players.size() + " USERS READY");
							usersReadyLabel.setStylePrimaryName("usersReady");
						} else {
							usersReadyLabel.setText(numUsersNotReady + "/" + players.size() + " USERS NOT YET READY");
							usersReadyLabel.setStylePrimaryName("usersNotReady");
						}
					}
				};
				GameServices.gameService.getAllPlayers(callback);
			}
			
		};
		
		dataProvider.addDataDisplay(playerTable);
		
		
	}
	
	private static void setRefreshPlayersTimer() {
		refreshTimer = new Timer() {
			@Override
			public void run() {
				//refreshPlayersView();
				refreshTime();
				Range range = playerTable.getVisibleRange();
				RangeChangeEvent.fire(playerTable, range);
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_PERIOD_MILLIS);
	}
	
	private static void refreshPlayersView() {
		AsyncCallback<List<UserDTO>> callback = new AsyncCallback<List<UserDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
				if(!"Game is resetting".equals(caught.getMessage())) {
					log("Failure! - 5 - " + caught.getMessage());
					log("Error occurred in refreshPlayers");
				}
			}

			@Override
			public void onSuccess(List<UserDTO> players) {
				//playerDataTable.resize(players.size()+1, 8);

				int numUsersNotReady = 0;
				
				List<PlayerEntry> playerTableList = new ArrayList<PlayerEntry>();
				
				for(int i = 0; i < players.size(); i++) {
					String string;
					UserDTO player = players.get(i);
					PlayerEntry playerEntry = new PlayerEntry();
					
					playerEntry.setPlayer(player);
					playerEntry.setId(player.getId());
					playerEntry.setPlayerName(player.getUsername());
					
					if(player.getCurrentLocation()!=null) {
						string = player.getCurrentLocation().getName();
					} else {
						RoadDTO road = player.getCurrentRoad();
						LocationDTO source = player.getForward() ? road.getLocation1() : road.getLocation2();
						LocationDTO destination = player.getForward() ? road.getLocation2() : road.getLocation1();
						
						string = source.getName() + " -> " + destination.getName();
					}
					
					playerEntry.setPlayerLocation(string);
					playerEntry.setCollectedItems(player.getInventory().size());
					playerEntry.setNumNeighbors(player.getNeighbors().size());
					
					if(player.isSeemsActive()) {
						string = "YES";
					} else {
						string = "NO";
					}
					
					playerEntry.setLoggedIn(string);
					playerEntry.setScore(player.getScore());
					
					AcceptanceFormDTO currentForm = player.getCurrentAcceptanceFormDTO();
					if(currentForm!=null) {
						playerEntry.setStatus(currentForm.getName());
					} else {
						playerEntry.setStatus("In Game");
					}
					
					ItemTypeDTO goalItem = player.getCurrentGoalItemType();
					if(goalItem!=null) {
						playerEntry.setGoalItem(goalItem.getName());
					} else {
						playerEntry.setGoalItem("");
					}
					
					if(currentForm!=null || !player.isSeemsActive()) {
						numUsersNotReady++;
					}
					
					playerTableList.add(playerEntry);
				}
				
				playerTable.setRowData(0, playerTableList);
				
				if(numUsersNotReady==0) {
					usersReadyLabel.setText("ALL " + players.size() + " USERS READY");
					usersReadyLabel.setStylePrimaryName("usersReady");
				} else {
					usersReadyLabel.setText(numUsersNotReady + "/" + players.size() + " USERS NOT YET READY");
					usersReadyLabel.setStylePrimaryName("usersNotReady");
				}
			}
		};
		GameServices.gameService.getAllPlayers(callback);
		playerTable.redraw();
	}
	
//	private static void refreshPlayers() {
//		AsyncCallback<List<UserDTO>> callback = new AsyncCallback<List<UserDTO>>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				if(!"Game is resetting".equals(caught.getMessage())) {
//					log("Failure! - 5 - " + caught.getMessage());
//					log("Error occurred in refreshPlayers");
//				}
//			}
//
//			@Override
//			public void onSuccess(List<UserDTO> players) {
//				playerTable.resize(players.size()+1, 8);
//				
//				Label label;
//				label = new Label("Player");
//				label.setStylePrimaryName("largeText12");
//				playerTable.setWidget(0, 0, label);
//				
//				label = new Label("Location");
//				label.setStylePrimaryName("largeText12");
//				playerTable.setWidget(0, 1, label);
//				
//				label = new Label("Collected items");
//				label.setStylePrimaryName("largeText12");
//				playerTable.setWidget(0, 2, label);
//				
//				label = new Label("# Neighbors");
//				label.setStylePrimaryName("largeText12");
//				playerTable.setWidget(0, 3, label);
//
//				label = new Label("Logged in");
//				label.setStylePrimaryName("largeText12");
//				playerTable.setWidget(0, 4, label);
//
//				label = new Label("Score");
//				label.setStylePrimaryName("largeText12");
//				playerTable.setWidget(0, 5, label);
//
//				label = new Label("Status");
//				label.setStylePrimaryName("largeText12");
//				playerTable.setWidget(0, 6, label);
//
//				label = new Label("Goal Item");
//				label.setStylePrimaryName("largeText12");
//				playerTable.setWidget(0, 7, label);
//
//				int numUsersNotReady = 0;
//				
//				for(int i=0;i<players.size();i++) {
//					String string;
//					UserDTO player = players.get(i);
//					playerTable.setText(i+1, 0, player.getUsername());
//					if(player.getCurrentLocation()!=null) {
//						string = player.getCurrentLocation().getName();
//					} else {
//						RoadDTO road = player.getCurrentRoad();
//						LocationDTO source = player.getForward() ? road.getLocation1() : road.getLocation2();
//						LocationDTO destination = player.getForward() ? road.getLocation2() : road.getLocation1();
//						
//						string = source.getName() + " -> " + destination.getName();
//					}
//					playerTable.setText(i+1, 1, string);
//					playerTable.setText(i+1, 2, String.valueOf(player.getInventory().size()));
//					playerTable.setText(i+1, 3, String.valueOf(player.getNeighbors().size()));
//					if(player.isSeemsActive()) {
//						string = "YES";
//					} else {
//						string = "NO";
//					}
//					playerTable.setText(i+1, 4, string);
//					playerTable.setText(i+1, 5, String.valueOf(player.getScore()));
//					AcceptanceFormDTO currentForm = player.getCurrentAcceptanceFormDTO();
//					if(currentForm!=null) {
//						playerTable.setText(i+1, 6, currentForm.getName());
//					} else {
//						playerTable.setText(i+1, 6, "In Game");
//					}
//					ItemTypeDTO goalItem = player.getCurrentGoalItemType();
//					if(goalItem!=null) {
//						playerTable.setText(i+1, 7, goalItem.getName());
//					} else {
//						playerTable.setText(i+1, 7, "");
//					}
//					if(currentForm!=null || !player.isSeemsActive()) {
//						numUsersNotReady++;
//					}
//				}
//				
//				if(numUsersNotReady==0) {
//					usersReadyLabel.setText("ALL " + players.size() + " USERS READY");
//					usersReadyLabel.setStylePrimaryName("usersReady");
//				} else {
//					usersReadyLabel.setText(numUsersNotReady + "/" + players.size() + " USERS NOT YET READY");
//					usersReadyLabel.setStylePrimaryName("usersNotReady");
//				}
//			}
//			
//		};
//		GameServices.gameService.getAllPlayers(callback);
//	}

	private static void refreshDistanceList() {
		AsyncCallback<List<List<Object>>> callback = new AsyncCallback<List<List<Object>>>() {
			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(List<List<Object>> result) {
				distanceListGrid.resize(result.size(), 3);
				for(int i=0; i<result.size(); i++) {
					List<Object> trioList = result.get(i);
					distanceListGrid.setText(i, 0, trioList.get(0).toString());
					distanceListGrid.setText(i, 1, trioList.get(1).toString());
					distanceListGrid.setText(i, 2, trioList.get(2).toString());
				}
			}
		};
		GameServices.gameService.getDistanceMap(callback);
	}
	
	private static void refreshTime() {

		AsyncCallback<GameTime> callback = new AsyncCallback<GameTime>() {
			@Override
			public void onFailure(Throwable caught) {
				log("Failure! - 6 - " + caught.getMessage());
				log("Error occurred in refreshTime");
			}

			@Override
			public void onSuccess(GameTime gameTime) {
				timeRemaining = gameTime.timeRemaining;
				gameDuration = gameTime.gameDuration;
				gameStarted = gameTime.gameStarted;
				gameFinished = gameTime.gameFinished;
				updateGameTimer();
			}
		};
		GameServices.gameService.getGameTime(callback);
	}
	
	public static void updateGameTimer() {
		if(gameDuration>0) {
			timerBar.setProgress(timeRemaining*100/gameDuration);
		} else {
			timerBar.setProgress(0);
		}

		if(gameStarted && !gameFinished) {
			Date zeroDate = new Date(2000,1,1,0,0,0);
			timeRemainingLabel.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(zeroDate.getTime() + timeRemaining)));
			gameStatusLabel.setText("Game in progress: YES");
		} else {
			timeRemainingLabel.setText("00:00");
			gameStatusLabel.setText("Game in progress: NO");
		}
	}
	
	public static void openCreateUserNetworkDialog() {
		CreateUserNetworkWindow createUserNetworkDialog = WindowInformation.createUserNetworkWindow;
		
		createUserNetworkDialog.setTitle("Create User Network");
		createUserNetworkDialog.clearContent();
		createUserNetworkDialog.show();
		//createUserNetworkDialog.setHeight("800px");
		//createUserNetworkDialog.setWidth("300px");
		createUserNetworkDialog.setPopupPosition(50, 50);
		createUserNetworkDialog.refresh();
	}
	
	public static void openFormsAdminDialog() {
		FormsAdminWindow formsAdminDialog = WindowInformation.formsAdminWindow;
		
		formsAdminDialog.setTitle("Manage Forms");
		formsAdminDialog.clearContent();
		formsAdminDialog.show();
		//formsAdminDialog.setHeight("800px");
		//formsAdminDialog.setWidth("300px");
		formsAdminDialog.setPopupPosition(50, 50);
		formsAdminDialog.refresh();
	}
	
	public static void openAdminMessageDialog() {
		AdminMessageWindow adminMessageDialog = WindowInformation.adminMessageWindow;
		
		adminMessageDialog.setTitle("Send Administrator Message");
		adminMessageDialog.clearContent();
		adminMessageDialog.center();
		//adminMessageDialog.setPopupPosition(50, 50);
	}

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private static void log(String message) {
		logPanel.addMessage(dateFormat.format(new Date()) + " -  " + message);
	}
	
	private static void drawUserGraph () {
		
		AsyncCallback<String> callback = new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				InfoWindow alert = new InfoWindow(200, 100, "Could not retrieve graph JSON string!");
				alert.center();
			}

			@Override
			public void onSuccess(String result) {
				Element graphDiv = DOM.getElementById("graphDiv");
				if (graphDiv == null) {
					graphDiv = DOM.createDiv();
					graphDiv.setId("graphDiv");
					DOM.appendChild(DOM.getElementById("gwt-debug-vPanel1"), graphDiv);
				}
				Element innerDetails = DOM.getElementById("innerDetails");
				if (innerDetails == null) {
					innerDetails = DOM.createDiv();
					innerDetails.setId("innerDetails");
					DOM.appendChild(DOM.getElementById("gwt-debug-vPanel1"), innerDetails);
				}
				drawGraphFromJSON(result);
			}
			
		};
		GameServices.gameService.userGraphToJSON(callback);
	}
	
	private static native void drawGraphFromJSON (String graphJSON) /*-{
		var labelType, useGradients, nativeTextSupport, animate;
		
		(function() {
	    	var ua = navigator.userAgent,
	    	iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
	    	typeOfCanvas = typeof HTMLCanvasElement,
	    	nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
	    	textSupport = nativeCanvasSupport
	    		&& (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
	    	//I'm setting this based on the fact that ExCanvas provides text support for IE
	    	//and that as of today iPhone/iPad current text support is lame
	    	labelType = (!nativeCanvasSupport || (textSupport && !iStuff)) ? 'Native': 'HTML';
	    	nativeTextSupport = labelType == 'Native';
	    	useGradients = nativeCanvasSupport;
	    	animate = !(iStuff || !nativeCanvasSupport);
		})();
	
		var fd = new $wnd.$jit.ForceDirected({
    	//id of the visualization container
    	injectInto: 'graphDiv',
	    //Enable zooming and panning
	    //by scrolling and DnD
	    Navigation: {
	      enable: true,
	      //Enable panning events only if we're dragging the empty
	      //canvas (and not a node).
	      panning: 'avoid nodes',
	      zooming: 10 //zoom speed. higher is more sensible
	    },
	    // Change node and edge styles such as
	    // color and width.
	    // These properties are also set per node
	    // with dollar prefixed data-properties in the
	    // JSON structure.
	    Node: {
	      overridable: true
	    },
	    Edge: {
	      overridable: true,
	      color: '#FF7F24',
	      lineWidth: 0.5
	    },
	    //Native canvas text styling
	    Label: {
	      type: labelType, //Native or HTML
	      size: 10,
	      style: 'bold'
	    },
	    //Add Tips
	    Tips: {
	      enable: true,
	      onShow: function(tip, node) {
	        //count connections
	        var count = 0;
	        node.eachAdjacency(function() { count++; });
	        //display node info in tooltip
	        tip.innerHTML = "<div class=\"tip-title\">" + node.name + "</div>"
	          + "<div class=\"tip-text\"><b>connections:</b> " + count + "</div>";
	      }
	    },
	    // Add node events
	    Events: {
	      enable: true,
	      type: 'Native',
	      //Change cursor style when hovering a node
	      onMouseEnter: function() {
	        fd.canvas.getElement().style.cursor = 'move';
	      },
	      onMouseLeave: function() {
	        fd.canvas.getElement().style.cursor = '';
	      },
	      //Update node positions when dragged
	      onDragMove: function(node, eventInfo, e) {
	          var pos = eventInfo.getPos();
	          node.pos.setc(pos.x, pos.y);
	          fd.plot();
	      },
	      //Implement the same handler for touchscreens
	      onTouchMove: function(node, eventInfo, e) {
	        $wnd.$jit.util.event.stop(e); //stop default touchmove event
	        this.onDragMove(node, eventInfo, e);
	      },
	      //Add also a click handler to nodes
	      onClick: function(node) {
	        if(!node) return;
	        // Build the right column relations list.
	        // This is done by traversing the clicked node connections.
	        var html = "<h4>" + node.name + "</h4><b> connections:</b><ul><li>",
	            list = [];
	        node.eachAdjacency(function(adj){
	          list.push(adj.nodeTo.name);
	        });
	        //append connections information
	        $wnd.$jit.id('innerDetails').innerHTML = html + list.join("</li><li>") + "</li></ul>";
	      }
	    },
	    //Number of iterations for the FD algorithm
	    iterations: 200,
	    //Edge length
	    levelDistance: 130,
	    // Add text to the labels. This method is only triggered
	    // on label creation and only for DOM labels (not native canvas ones).
	    onCreateLabel: function(domElement, node) {
	      domElement.innerHTML = node.name;
	      var style = domElement.style;
	      style.fontSize = "0.8em";
	      style.color = "#ddd";
	    },
	    // Change node styles when DOM labels are placed
	    // or moved.
	    onPlaceLabel: function(domElement, node) {
	      var style = domElement.style;
	      var left = parseInt(style.left);
	      var top = parseInt(style.top);
	      var w = domElement.offsetWidth;
	      style.left = (left - w / 2) + 'px';
	      style.top = (top + 10) + 'px';
	      style.display = '';
	    }
	  });
	  // load JSON data
	  // alert(graphJSON);
	  fd.loadJSON(eval(graphJSON));
	  // compute positions incrementally and animate.
	  fd.computeIncremental({
	    iter: 40,
	    property: 'end',
	    onStep: function(perc){
	      // Log.write(perc + '% loaded...');
	    },
	    onComplete: function(){
	      // Log.write('done');
	      fd.animate({
	        modes: ['linear'],
	        transition: $wnd.$jit.Trans.Elastic.easeOut,
	        duration: 2500
	      });
	    }
	  });
	  // end
	}-*/;
}