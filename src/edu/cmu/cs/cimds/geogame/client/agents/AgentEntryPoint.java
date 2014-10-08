package edu.cmu.cs.cimds.geogame.client.agents;

import java.util.Date;

import org.gwtwidgets.client.util.SimpleDateFormat;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import edu.cmu.cs.cimds.geogame.client.services.GameServices;
import edu.cmu.cs.cimds.geogame.client.ui.ChatPanel;

public class AgentEntryPoint implements EntryPoint {
	
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
	public AgentEntryPoint() { }

	/**
	 * The entry point method, called automatically by loading a module
	 * that declares an implementing class as an entry-point
	 */
	
	private static final int DEFAULT_NUM_TEST_PLAYERS = 50;
	private static final int GET_MESSAGES_POLL_PERIOD = 1000;
	private static final int INITIAL_LOG_IN_INTERVAL = 30000;
	
	ChatPanel logPanel = new ChatPanel();
	Grid playersGrid = new Grid(1,1);
	CheckBox delayCheckBox = new CheckBox("Delay logins");
	CheckBox moveCheckBox = new CheckBox("Move when login");
	CheckBox pickupStuffCheckBox = new CheckBox("Pick up stuff when login");
	CheckBox talkCheckBox = new CheckBox("Talk when login");
	
	Timer loginTimer;
	
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

		playersGrid.setBorderWidth(1);
		RootPanel.get().add(this.playersGrid);
		
		Button loginPollButton = new Button("click to start agent polling");
		loginPollButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				loginPoll();
			}
		});
		RootPanel.get().add(loginPollButton);

//		WindowInformation.treasurePanel.log(String.valueOf(Window.getClientHeight()));
//		WindowInformation.treasurePanel.log(String.valueOf(Window.getClientWidth()));
	}
	
	public void pollOnce () {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				// do a thing
			}
			
			@Override
			public void onSuccess(String command) {
				// if we got a login command we process it here
				Window.alert(command);
			}
		};
		GameServices.agentService.getLoginCommand(callback);
	}
	
	public void loginPoll () {
		loginTimer = new Timer() {
			@Override
			public void run() {
				AsyncCallback<String> callback = new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// do a thing
					}
					
					@Override
					public void onSuccess(String command) {
						// if we got a login command we process it here
						Window.alert(command);
					}
				};
				GameServices.agentService.getLoginCommand(callback);
			}
		};
		
		loginTimer.scheduleRepeating(1000);
	}
	
	
}
