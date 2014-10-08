package edu.cmu.cs.cimds.geogame.client.test;

import java.util.Date;

import org.gwtwidgets.client.util.SimpleDateFormat;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.ui.ChatPanel;

public class TestEntryPoint implements EntryPoint {
	
	public interface ControlImageBundle extends ImageBundle {
		@Resource("minus2.png")
		AbstractImagePrototype minus();

		@Resource("center2.png")
		AbstractImagePrototype center();

		@Resource("plus2.png")
		AbstractImagePrototype plus();
	}

	public static String titleString = "Geogame";
	public static String footerString = "Carnegie Mellon, Department of Psychology and Robotics Institute (C) 2009";
	
	/**
	 * Creates a new instance of TestEntryPoint
	 */
	public TestEntryPoint() { }

	/**
	 * The entry point method, called automatically by loading a module
	 * that declares an implementing class as an entry-point
	 */
	
	private static final int DEFAULT_NUM_TEST_PLAYERS = 50;
	private static final int GET_MESSAGES_POLL_PERIOD = 1000;
	private static final int INITIAL_LOG_IN_INTERVAL = 30000;
	
	ChatPanel logPanel = new ChatPanel();
	Grid playersGrid = new Grid(1,1);
	TestPlayer[] testPlayers = new TestPlayer[300];
	CheckBox delayCheckBox = new CheckBox("Delay logins");
	CheckBox moveCheckBox = new CheckBox("Move when login");
	CheckBox pickupStuffCheckBox = new CheckBox("Pick up stuff when login");
	CheckBox talkCheckBox = new CheckBox("Talk when login");
	
	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private void log(String message) {
		logPanel.addMessage(dateFormat.format(new Date()) + " -  " + message);
	}
	
	private int numTestPlayers = DEFAULT_NUM_TEST_PLAYERS;
	private int getMessagesPollPeriod = GET_MESSAGES_POLL_PERIOD;
	
	public void onModuleLoad() {
		// Define widget elements
		final DockPanel dock = new DockPanel();
		dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		dock.setBorderWidth(3);

		final Label titleLabel = new Label(titleString);
		final Label footerLabel = new Label(footerString);

		RootPanel.get().add(titleLabel);
		RootPanel.get().add(footerLabel);
		RootPanel.get().add(logPanel);
		
//		delayCheckBox.setValue(true);
		RootPanel.get().add(delayCheckBox);

//		moveCheckBox.setValue(true);
		moveCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(TestPlayer player : TestEntryPoint.this.testPlayers) {
					player.setMovementFlag(moveCheckBox.getValue());
				}
			}
		});
		RootPanel.get().add(moveCheckBox);

//		talkCheckBox.setValue(true);
		talkCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(TestPlayer player : TestEntryPoint.this.testPlayers) {
					player.setTalkFlag(talkCheckBox.getValue());
				}
			}
		});
		RootPanel.get().add(talkCheckBox);

//		pickupStuffCheckBox.setValue(true);
		pickupStuffCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(TestPlayer player : TestEntryPoint.this.testPlayers) {
					player.setPickupStuffFlag(pickupStuffCheckBox.getValue());
				}
			}
		});
		RootPanel.get().add(pickupStuffCheckBox);

		RootPanel.get().add(new Label("Num Test Players (max 300):"));
		final TextBox numPlayersTextBox = new TextBox();
		numPlayersTextBox.setText(String.valueOf(this.numTestPlayers));
		RootPanel.get().add(numPlayersTextBox);
		Button changeNumUsersButton = new Button("Set Number of Users");
		changeNumUsersButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					int numUsers = Integer.valueOf(numPlayersTextBox.getText());
					if(numUsers<0) {
						throw new NumberFormatException();
					}
					changeNumTestPlayers(numUsers);
				} catch(NumberFormatException ex) {
					Window.alert(numPlayersTextBox.getText() + " is not a valid number of test users");
				}
			}
		});
		RootPanel.get().add(changeNumUsersButton);
		
		RootPanel.get().add(new Label("Polling period:"));
		final TextBox pollPeriodTextBox = new TextBox();
		pollPeriodTextBox.setText(String.valueOf(this.getMessagesPollPeriod));
		RootPanel.get().add(pollPeriodTextBox);
		Button changePollPeriodButton = new Button("Set Poll Period");
		changePollPeriodButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					int pollPeriod = Integer.valueOf(pollPeriodTextBox.getText());
					if(pollPeriod<=0) {
						throw new NumberFormatException();
					}
					changePollPeriod(pollPeriod);
				} catch(NumberFormatException ex) {
					Window.alert(pollPeriodTextBox.getText() + " is not a valid poll period value");
				}
			}
		});
		RootPanel.get().add(changePollPeriodButton);
		
		RootPanel.get().add(new Label("Start/Stop"));
		Button beginButton = new Button("Start");
		beginButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				runTestCycle();
			}
		});
		RootPanel.get().add(beginButton);
		
		Button stopButton = new Button("Stop");
		stopButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				stopTestCycle();
			}
		});
		RootPanel.get().add(stopButton);

		playersGrid.setBorderWidth(1);
		RootPanel.get().add(this.playersGrid);

//		WindowInformation.treasurePanel.log(String.valueOf(Window.getClientHeight()));
//		WindowInformation.treasurePanel.log(String.valueOf(Window.getClientWidth()));
	}

	private void changeNumTestPlayers(int numTestPlayers) {
//		if(this.numTestPlayers==numTestPlayers) {
//			Window.alert("Number of players is the same!");
//			return;
//		}
		this.numTestPlayers = numTestPlayers;
	}
	
	private void changePollPeriod(int pollPeriod) {
//		if(this.getMessagesPollPeriod==pollPeriod) {
//			Window.alert("Poll period is the same!");
//			return;
//		}
		this.getMessagesPollPeriod = pollPeriod;
	}

	public void runTestCycle() {
		stopTestCycle();
		loginPlayers(TestEntryPoint.this.numTestPlayers);
	}
	
	public void stopTestCycle() {
		for(TestPlayer testPlayer : this.testPlayers) {
			if(testPlayer!=null && testPlayer.isLoggedIn()) {
				testPlayer.logout();
			}
		}
	}

	public void loginPlayers(int numTestPlayers) {
		log("Creating " + numTestPlayers + " players");
		this.playersGrid.resize(numTestPlayers+4, 11);
		
		this.playersGrid.setText(0, 0, "Username");
		this.playersGrid.setText(0, 1, "Logout");
		this.playersGrid.setText(0, 2, "Last lag");
		this.playersGrid.setText(0, 3, "Max lag");
		this.playersGrid.setText(0, 4, "Movement");
		this.playersGrid.setText(0, 5, "Location");
		this.playersGrid.setText(0, 6, "Pick up");
		this.playersGrid.setText(0, 7, "Talk");
		this.playersGrid.setText(0, 8, "Score");
		this.playersGrid.setText(0, 9, "Log");
		this.playersGrid.setText(0, 10, "# Items");
		
		for(int i=0;i<numTestPlayers;i++) {
			String playerName = "TestUser" + i;
			int startTime = delayCheckBox.getValue() ? (int)(Random.nextDouble()*INITIAL_LOG_IN_INTERVAL) : 1;
			if(this.testPlayers[i]==null) {
				this.testPlayers[i] = new TestPlayer();
			}
			final TestPlayer player = this.testPlayers[i];
			
			final ChatPanel playerLogPanel = new ChatPanel(30);
			playerLogPanel.setVisible(false);

			log(playerName + " set to delay " + (double)startTime/1000 + " s.");
			player.init(playerName, "test", this.getMessagesPollPeriod, playerLogPanel, startTime);
			
			
			this.playersGrid.setText(i+1, 0, playerName);
			Button logoutButton = new Button("Logout");
			logoutButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					player.logout();
				}
			});
			this.playersGrid.setWidget(i+1, 1, logoutButton);
			Label gridLabel1 = new Label();
			Label gridLabel2 = new Label();
			Label gridLabel3 = new Label();
			Button moveButton = new Button("Move");
			moveButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					player.toggleMovementFlag();
				}
			});
			Button pickupButton = new Button("Pick up stuff");
			pickupButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					player.togglePickupStuffFlag();
				}
			});
			Button talkButton = new Button("Talk");
			talkButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					player.toggleTalkFlag();
				}
			});
			Label scoreLabel = new Label();

			Button toggleLogButton = new Button("Toggle Log");
			toggleLogButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					playerLogPanel.setVisible(!playerLogPanel.isVisible());
				}
			});

			VerticalPanel panel = new VerticalPanel();
			panel.add(toggleLogButton);
			panel.add(playerLogPanel);
			
			Label numItemsLabel = new Label();
			
			this.playersGrid.setWidget(i+1, 2, gridLabel1);
			this.playersGrid.setWidget(i+1, 3, gridLabel2);
			this.playersGrid.setWidget(i+1, 4, moveButton);
			this.playersGrid.setWidget(i+1, 5, gridLabel3);
			this.playersGrid.setWidget(i+1, 6, pickupButton);
			this.playersGrid.setWidget(i+1, 7, talkButton);
			this.playersGrid.setWidget(i+1, 8, scoreLabel);
			this.playersGrid.setWidget(i+1, 9, panel);
			this.playersGrid.setWidget(i+1, 10, numItemsLabel);
			
			player.setTimeLabels(gridLabel1, gridLabel2, gridLabel3, moveButton, pickupButton, talkButton, scoreLabel, numItemsLabel);
			player.setMovementFlag(moveCheckBox.getValue());
		}
		this.playersGrid.setText(numTestPlayers+1, 0, "Avg");
		this.playersGrid.setText(numTestPlayers+2, 0, "Min");
		this.playersGrid.setText(numTestPlayers+3, 0, "Max");
		for(int i=1;i<8;i++) {
			this.playersGrid.setText(numTestPlayers+1, i, "");
			this.playersGrid.setText(numTestPlayers+2, i, "");
			this.playersGrid.setText(numTestPlayers+3, i, "");
		}

		for(int i=0;i<numTestPlayers;i++) {
			TestPlayer player = this.testPlayers[i];
			player.beginCycle();
		}
	}
}