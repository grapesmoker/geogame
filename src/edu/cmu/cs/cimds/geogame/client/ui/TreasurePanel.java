package edu.cmu.cs.cimds.geogame.client.ui;

import java.util.Date;

import org.gwtwidgets.client.util.SimpleDateFormat;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.GameInfo;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

public class TreasurePanel extends HorizontalPanel {

	public static final String MONEY_ICON_FILENAME = "coinbag.jpg";

	private static final int NUM_MAX_MESSAGES = 5;
	HorizontalPanel treasurePanel2 = new HorizontalPanel();
//	ScrollPanel inventoryScrollPanel = new ScrollPanel();
	ScrollPanel goalItemsScrollPanel = new ScrollPanel();
	//HorizontalPanel inventoryInset = new HorizontalPanel();
//	InventoryGrid inventoryGrid = new InventoryGrid(2,4, new SaleItemImageCreator());
//	ItemTypeGrid goalItemsGrid = new ItemTypeGrid(1,4, new NoopItemTypeImageCreator());
	Label numPointsLabel = new Label("Items found: 0");
	GoalItemsGrid goalItemsGrid = new GoalItemsGrid(1,1,new NoopItemTypeImageCreator());
	Label moneyLabel = new Label();
	TabPanel tabPanel = new TabPanel();
//	Label timerLabel = new Label("00:00:00");
//	boolean timerRunning = false;
//	Timer gameTimer;
//	ScrollPanel logTextScrollPanel = new ScrollPanel();
	//HTML logText = new HTML();
	ChatPanel logPanel = new ChatPanel();
	
//	Grid scoresGrid = new Grid();
	Label scoreLabel = new Label();
	
	Button testMoveButton = new Button("TEST");
	Button stopTestMoveButton = new Button("STOP TEST");
	boolean deactivateTestMoveFlag = true;
	
	ProgressBar timerBar = new ProgressBar();
	Label timeRemainingLabel = new Label();
	long timeRemaining = 0;
//	long gameDuration = 0;
	
	public TreasurePanel() {
//		initTestMoveButton();

//		this.scoresGrid = new Grid();
//		this.scoresGrid.setBorderWidth(1);
//		this.add(this.scoresGrid);
		this.scoreLabel.setStylePrimaryName("largeText15");
		this.add(scoreLabel);

		VerticalPanel container = new VerticalPanel();
		
		container.setHorizontalAlignment(ALIGN_LEFT);
		treasurePanel2.add(numPointsLabel);

		//		this.inventoryInset.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.goalItemsGrid.setBorderWidth(1);
		this.goalItemsGrid.setImageWidth("50px");

		//		this.inventoryScrollPanel.add(this.inventoryInset);
//		this.inventoryGrid.setBorderWidth(1);
//		this.inventoryGrid.setImageWidth("50px");
//		this.inventoryScrollPanel.add(this.inventoryGrid);
		this.goalItemsScrollPanel.add(this.goalItemsGrid);

//		Image moneyImage = new Image(MONEY_ICON_FILENAME);
//		moneyImage.setWidth("60px");
//		moneyLabel.setText("Gold left:");

//		VerticalPanel treasureSubPanel = new VerticalPanel();
//		treasureSubPanel.setHorizontalAlignment(ALIGN_LEFT);
//		treasureSubPanel.add(moneyImage);
//		treasureSubPanel.add(moneyLabel);
//		treasureSubPanel.setPixelSize(100, 100);
//		treasurePanel2.add(treasureSubPanel);

		tabPanel.add(goalItemsScrollPanel,"Goal Items");
//		tabPanel.add(inventoryScrollPanel,"Backpack");
		treasurePanel2.add(tabPanel);
		tabPanel.selectTab(0);
		
		HorizontalPanel spaceEater = new HorizontalPanel();
		spaceEater.add(new Label(" "));
		spaceEater.setWidth("200 px");
		treasurePanel2.add(spaceEater);
		timerBar.ensureDebugId("GameTimer");
		treasurePanel2.add(timerBar);
		timeRemainingLabel.ensureDebugId("GameTimerLabel");
		treasurePanel2.add(timeRemainingLabel);
		
//		treasurePanel2.add(timerLabel);
		container.add(treasurePanel2);
		
//		logTextScrollPanel.setHeight("100px");
		
		logPanel.setMaxMessages(NUM_MAX_MESSAGES);
		for(int i=0;i<NUM_MAX_MESSAGES;i++) {
			logPanel.addMessage("");
		}
//		logTextScrollPanel.add(logPanel);
//		this.add(logTextScrollPanel);
		logPanel.setHeight("5ex");
		logPanel.ensureDebugId("LogPanel");
		container.add(logPanel);
		this.add(container);
		
//		Button clearLogButton = new Button("Clear Log");
		//this.add(recenterButton);
//		clearLogButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				TreasurePanel.this.clearLog();
//			}
//		});
//		this.add(clearLogButton);
	}

//	private void initTestMoveButton() {
//		this.add(testMoveButton);
//		this.add(stopTestMoveButton);
//		stopTestMoveButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				deactivateTestMoveFlag = true;
//			}
//		});
//		
//		final List<Long> locationIds = new ArrayList<Long>();
//		locationIds.add(25L);
//		locationIds.add(53L);
//		locationIds.add(5L);
//		locationIds.add(1L);
//		locationIds.add(2L);
//		locationIds.add(3L);
//		locationIds.add(4L);
//		locationIds.add(8L);
//		locationIds.add(9L);
//		locationIds.add(7L);
//		locationIds.add(52L);
//		locationIds.add(12L);
//		locationIds.add(48L);
//		locationIds.add(16L);
//		locationIds.add(20L);
//		locationIds.add(66L);
//		locationIds.add(46L);
//		locationIds.add(70L);
//		locationIds.add(15L);
//		locationIds.add(14L);
//		locationIds.add(17L);
//		locationIds.add(59L);
//		locationIds.add(18L);
//		locationIds.add(35L);
//		locationIds.add(19L);
//		locationIds.add(44L);
//		locationIds.add(56L);
//		locationIds.add(57L);
//		locationIds.add(67L);
//		locationIds.add(43L);
//		locationIds.add(41L);
//		locationIds.add(49L);
//		locationIds.add(29L);
//		locationIds.add(28L);
//		locationIds.add(40L);
//		locationIds.add(27L);
//		locationIds.add(39L);
//		locationIds.add(26L);
//		locationIds.add(25L);
//		
////		locationIds.add(22L);
////		locationIds.add(32L);
////		locationIds.add(31L);
////		locationIds.add(29L);
////		locationIds.add(28L);
////		locationIds.add(27L);
////		locationIds.add(26L);
////		locationIds.add(25L);
////		locationIds.add(13L);
////		locationIds.add(12L);
////		locationIds.add(11L);
////		locationIds.add(10L);
////		locationIds.add(14L);
////		locationIds.add(17L);
////		locationIds.add(18L);
////		locationIds.add(35L);
////		locationIds.add(34L);
////		locationIds.add(37L);
////		locationIds.add(23L);
////		locationIds.add(22L);
////		locationIds.add(30L);
////		locationIds.add(31L);
////		locationIds.add(29L);
////		locationIds.add(28L);
////		locationIds.add(27L);
////		locationIds.add(26L);
////		locationIds.add(25L);
////		locationIds.add(13L);
////		locationIds.add(12L);
////		locationIds.add(11L);
////		locationIds.add(10L);
////		locationIds.add(14L);
////		locationIds.add(17L);
////		locationIds.add(18L);
////		locationIds.add(35L);
////		locationIds.add(34L);
////		locationIds.add(37L);
////		locationIds.add(23L);
////		locationIds.add(22L);
////		locationIds.add(30L);
////		locationIds.add(31L);
////		locationIds.add(29L);
////		locationIds.add(28L);
////		locationIds.add(27L);
////		locationIds.add(26L);
//		deactivateTestMoveFlag = false;
//		
//		testMoveButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				long curLocationId = GameInfo.getInstance().getLocation().getId();
//				int index = locationIds.indexOf(curLocationId);
//				if(index==-1) {
//					Window.alert("Location not found :(");
//					return;
//				}
//				
//				long nextLocationId = locationIds.get((int)(index+1)%locationIds.size());
//				
////				GameInfo.getInstance().log("Traveling " + GameInfo.getInstance().getLocation().getName() + " -> " + nextLocationId + "...");
//			
//				AsyncCallback<MoveResult> callback = new AsyncCallback<MoveResult>() {
//					public void onFailure(Throwable caught) {
//						WindowInformation.inAjaxCall = false;
//						//Window.alert("Move failed - " + caught.getMessage());
//						GameInfo.getInstance().log("Move failed! " + caught.getMessage());
//						if(deactivateTestMoveFlag) {
//							deactivateTestMoveFlag = false;
//							return;
//						}
//					}
//					
//					public void onSuccess(MoveResult result) {
//						WindowInformation.inAjaxCall = false;
//						if(result.isSuccess()) {
//							GameInfo.getInstance().log(result.getMessage());
//							GameInfo.getInstance().updateInformation(result.getGameInformation());
////							GameInfo.getInstance().refreshAll(false);
//						}
//
//						if(deactivateTestMoveFlag) {
//							deactivateTestMoveFlag = false;
//							return;
//						}
//
//						if(result.isSuccess()) {
//							new Timer() {
//								@Override
//								public void run() {
//									if(deactivateTestMoveFlag) {
//										deactivateTestMoveFlag = false;
//										return;
//									}
//									testMoveButton.click();
//								}
//							}.schedule(result.getDuration()+1000);
//						}
//					}
//				};
//				WindowInformation.inAjaxCall = true;
//				GameServices.gameService.moveToLocation(GameInfo.getInstance().getPlayer(), nextLocationId, callback);
//			}
//		});
//	}
	
	public void updateGameTimer(long timeRemaining, long gameDuration) {
		if(gameDuration==0) {
			this.timerBar.setProgress(0);
		} else {
			this.timerBar.setProgress(timeRemaining*100/gameDuration);
		}

		Date zeroDate = new Date(2000,1,1,0,0,0);
		this.timeRemainingLabel.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(zeroDate.getTime() + timeRemaining)));
//		this.timerBar.setProgress(100);
//		this.timeRemaining = timeRemaining;
////		this.gameDuration = gameDuration;
//		Timer gameTimer = new Timer() {
//			@Override
//			public void run() {
//				TreasurePanel.this.timeRemaining-=1000;
//				if(TreasurePanel.this.timeRemaining<=0) {
//					TreasurePanel.this.timeRemaining = 0;
//				}
//				timerBar.setProgress((int) (TreasurePanel.this.timeRemaining * 100 / gameDuration));
//			}
//		};
//		gameTimer.scheduleRepeating(1000);
	}
	
	public void clearContent() {
//		this.inventoryGrid.clear();
		this.goalItemsGrid.clear();
//		this.scoresGrid.clear();
		
		//		this.clearLog();
		//		this.goalItemsInset.clear();
	}

	public void refresh() {		
		this.clearContent();

		UserDTO player = GameInfo.getInstance().getPlayer();
//		if(player==null) {
//			tabPanel.getTabBar().setTabText(0,"Backpack");
//		} else {
//			tabPanel.getTabBar().setTabText(0,player.getUsername() + "'s backpack");
//		}
		tabPanel.getTabBar().setTabText(0,"Item to Find");
		
		this.numPointsLabel.setText("Items found: " + player.getInventory().size());
		//ExternalHyperlink ehl = new ExternalHyperlink("Google","http://www.google.com");
		//this.add(ehl);
		for(ItemTypeDTO goalItem : GameInfo.getInstance().getPlayer().getItemsToCollect()) {
			tabPanel.ensureDebugId("ItemToFind-" + goalItem.getName());
			this.drawGoalItem(goalItem);
		}
//		inventoryGrid.setInventory(GameInfo.getInstance().getPlayer().getInventory());
//		inventoryGrid.refresh();
		
		goalItemsGrid.setItemsToCollect(GameInfo.getInstance().getPlayer().getItemsToCollect());
		goalItemsGrid.setInventory(GameInfo.getInstance().getPlayer().getInventory());
		goalItemsGrid.refresh();
		//		for(ItemDTO item : GameInfo.getInstance().getPlayer().getInventory()) {
		//			this.drawItem(item);
		//		}
		moneyLabel.setText("Gold left: " + GameInfo.getInstance().getPlayer().getScore());
		
		this.refreshScore();
	}

//	public void refreshScoreGrid() {
//		this.scoresGrid.resize(GameInfo.getInstance().getScores().size()+1, 2);
//		
//		Label playerLabel = new Label("Player");
//		playerLabel.setStylePrimaryName("largeText12");
//		this.scoresGrid.setWidget(0, 0, playerLabel);
//
//		Label scoreLabel = new Label("Score");
//		scoreLabel.setStylePrimaryName("largeText12");
//		this.scoresGrid.setWidget(0, 1, scoreLabel);
//		
//		int i=1;
//		for(UserDTO scorePlayer : GameInfo.getInstance().getScores()) {
//			this.scoresGrid.setText(i, 0, scorePlayer.getUsername());
//			this.scoresGrid.setText(i, 1, String.valueOf(scorePlayer.getScore()));
//			i++;
//		}
//	}
	
	public void refreshScore() {
		this.scoreLabel.setText("Score: " + GameInfo.getInstance().getPlayer().getScore());
	}

	public void drawGoalItem(ItemTypeDTO goalItem) {
		Image image = new Image(goalItem.getIconFilename());
		image.setWidth(WindowInformation.imageWidth);
		//Label descriptionLabel = new Label(goalItem.getName());

		VerticalPanel treasureSubPanel = new VerticalPanel();
		treasureSubPanel.setHorizontalAlignment(ALIGN_LEFT);
		treasureSubPanel.add(image);
		//treasureSubPanel.add(descriptionLabel);
		treasureSubPanel.setPixelSize(100, 100);

		//descriptionLabel.ensureDebugId("GoalItemLabel-" + goalItem.getName());
		image.ensureDebugId("GoalItemImage-" + goalItem.getName());
		
		//		this.goalItemsInset.add(treasureSubPanel);		
	}

	//	public void drawItem(final ItemDTO item) {
	//		Image image = new Image(item.getItemType().getIconFilename());
	//		//image.setSize("90", "90");
	//		
	//		//image.setWidth("200px");
	//		image.setWidth(WindowInformation.imageWidth);
	//		
	//		final AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
	//			public void onFailure(Throwable caught) {
	//				Window.alert("Error while calling Game Service: " + caught.getMessage());
	//			}
	//
	//			public void onSuccess(Integer result) {
	//				int price = result;
	//				boolean saleConfirmation = Window.confirm("You are offered " + price + " for your item. Accept?");
	//				if(!saleConfirmation) {
	//					return;
	//				} else {
	//					AsyncCallback<ActionResult> callback = new AsyncCallback<ActionResult>() {
	//						public void onFailure(Throwable caught) {
	//							Window.alert("Error while calling Game Service: " + caught.getMessage());
	//						}
	//
	//						public void onSuccess(ActionResult actionResult) {
	//							GameInfo.getInstance().updateInformation(actionResult.getGameInfo());
	//							GameInfo.getInstance().refreshAll(false);
	////							Window.alert("Item sold. Your gold is now " + GameInfo.getInstance().getPlayer().getScore());
	//						}
	//					};
	//					GameServices.gameService.sellItem(GameInfo.getInstance().getPlayer(), item.getId(), result, callback);
	//				}
	//			}
	//		};
	//		image.addClickHandler(new ClickHandler() {
	//			@Override
	//			public void onClick(ClickEvent event) {
	//				GameServices.gameService.getSalePrice(GameInfo.getInstance().getPlayer(), item.getId(), callback);
	//			}
	//		});
	//		
	//		Label descriptionLabel = new Label(item.getItemType().getName());
	//
	//		VerticalPanel treasureSubPanel = new VerticalPanel();
	//		treasureSubPanel.setHorizontalAlignment(ALIGN_LEFT);
	//		treasureSubPanel.add(image);
	//		treasureSubPanel.add(descriptionLabel);
	//		treasureSubPanel.setPixelSize(100,100);
	//		
	//		this.inventoryGrid.add(treasureSubPanel);
	//	}

	public void log(String text) {
		this.logPanel.addMessage(ChatPanel.escapeHtml(text));
//		this.logPanel.addMessage(String.valueOf(Window.getClientHeight()));
//		this.logPanel.addMessage(String.valueOf(Window.getClientWidth()));

//		this.logTextScrollPanel.setScrollPosition(this.logTextScrollPanel.getScrollPosition() + 19);
		//		String preHtml = this.logText.getHTML();
		//		String postHtml = preHtml + escapeHtml(text) + "<br>";
		//		this.logText.setHTML(postHtml);
		////		this.clearLog();
		//		this.logTextScrollPanel.setScrollPosition(this.logTextScrollPanel.getScrollPosition() + 18);
	}
	
	public void clearLog() {
		this.logPanel.clearMessages();
	}
}