/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

import edu.cmu.cs.cimds.geogame.client.MainEntryPoint;
import edu.cmu.cs.cimds.geogame.client.ui.map.MapUtil;



/**
 *
 * @author Antonio
 */
public class WindowInformation {
	
	public static final String imageWidth = "60px";
	public static final int AJAX_TIMEOUT = 10000;
	public static boolean inAjaxCall = false;
	
	public static boolean AMTVisitor = false;
	public static String refererURL = "";
	
	public static Label titleLabel;
	public static Label footerLabel;

	public static DockPanel dockPanel;
	public static LoginPanel loginPanel;
	public static TreasurePanel treasurePanel;
	public static CommsPanel commsPanel;
	public static AcceptPanel acceptPanel;
	
	public static RootPanel adminRootPanel;
	public static HorizontalPanel adminPanel;
	
	public static LocationInfoPanel locationPanel;

	public static MapWidget mapWidget;
	
//	public static LocationInformationWindow locationInformationWindow;
//	public static TradeWindow tradeWindow;

	public static NewUserWindow newUserWindow = new NewUserWindow();
	public static LanguageDisplay languageDisplay = new LanguageDisplay();
	public static CreateUserNetworkWindow createUserNetworkWindow = new CreateUserNetworkWindow();
	public static FormsAdminWindow formsAdminWindow = new FormsAdminWindow();
	public static AdminMessageWindow adminMessageWindow = new AdminMessageWindow();
	
	public static void reset() {
		DockPanel parentPanel = WindowInformation.dockPanel;
		CommsPanel commsPanel = WindowInformation.commsPanel;
		TreasurePanel treasurePanel = WindowInformation.treasurePanel;
		LocationInfoPanel locationPanel = WindowInformation.locationPanel;
		LoginPanel loginPanel = WindowInformation.loginPanel;
		AcceptPanel acceptPanel = WindowInformation.acceptPanel;
		parentPanel.add(loginPanel, DockPanel.EAST);
		parentPanel.add(WindowInformation.mapWidget, DockPanel.WEST);
		parentPanel.remove(acceptPanel);
		parentPanel.remove(commsPanel);
		parentPanel.remove(treasurePanel);
		parentPanel.remove(locationPanel);
		parentPanel.remove(acceptPanel);
		treasurePanel.clearLog();
		WindowInformation.titleLabel.setText(MainEntryPoint.titleString);
		//Window.alert("Log out successful");
		WindowInformation.commsPanel.clearContent();
		WindowInformation.loginPanel.clearContent();
		WindowInformation.treasurePanel.clearContent();
		WindowInformation.locationPanel.clearContent();
		MapUtil.clearMap();
//		MapUtil.setStandbyMap();
		WindowInformation.loginPanel.select();
		WindowInformation.newUserWindow.clearContent();
	}
}