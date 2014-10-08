package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.DisplayResult;
import edu.cmu.cs.cimds.geogame.client.GameInfo;
import edu.cmu.cs.cimds.geogame.client.LoginResult;
import edu.cmu.cs.cimds.geogame.client.model.dto.AcceptanceFormDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;

public class LoginPanel extends VerticalPanel {

	TextBox usernameTextBox;
	PasswordTextBox passwordTextBox;
	Button submitButton;

	Button newUserButton;
	protected String username;
	protected String password;
	
	boolean adminLogin = false;
	
	public void clearContent() {
		this.usernameTextBox.setText("");
		this.passwordTextBox.setText("");
	}

	public LoginPanel() {
		// this.setBorderWidth(1);

		// depending on what page we're on we should either use the regular or the admin login
		
		if (Window.Location.getHref().contains("GeoAdmin.html")) {
			adminLogin = true;
		}
		
		usernameTextBox = new TextBox();
		passwordTextBox = new PasswordTextBox();

		usernameTextBox.ensureDebugId("Username");
		passwordTextBox.ensureDebugId("Password");
		
		usernameTextBox.setMaxLength(50);

		
		passwordTextBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				// TODO Auto-generated method stub
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					submitButton.click();
				}
			}
		});

		submitButton = new Button("Login");
		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (adminLogin) {
					sendAdminLogin(usernameTextBox.getText(), passwordTextBox.getText());
				}
				else {
					sendLogin(usernameTextBox.getText(), passwordTextBox.getText());
				}
				WindowInformation.loginPanel.clearContent();
			}
		});

		newUserButton = new Button("New User?");
		newUserButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				openNewUserDialog();
			}
		});

		this.add(new Label("Username"));
		this.add(usernameTextBox);
		this.add(new Label("Password"));
		this.add(passwordTextBox);
		// this.add(submitButton);
		// TODO: Fix the way that the login panel takes up space...
		this.add(submitButton);
		this.add(newUserButton);
		VerticalPanel spaceEater = new VerticalPanel();
		spaceEater.setHeight("550px");
		spaceEater.add(new Label(" "));
		this.add(spaceEater);
	}

	public void maybeLoginThroughCookies() {
		String username = Cookies.getCookie("username");
		String password = Cookies.getCookie("password");
		if(username!=null && password!=null && !"".equals(username) && !"".equals(password)) {
			this.sendLogin(username, password);
		}
		else if (WindowInformation.AMTVisitor){
			// if they came to us from the Turk and they're not being automatically logged in
			String welcomeMsg = "Welcome to the GeoGame! You are seeing this message because you came here from the Amazon Mechanical Turk." +
					" We ask that you create an account in our system in order to play the GeoGame. After you have created an account and logged in" +
					" you will receive a set of instructions for playing the game. The game will restart automatically every 15 minutes.";
			InfoWindow welcome = new InfoWindow(400, 300, welcomeMsg);
			welcome.center();
		}
	}
	
	public void maybeAdminLoginThroughCookies() {
		String adminUsername = Cookies.getCookie("adminUsername");
		String adminPassword = Cookies.getCookie("adminPassword");
		if(adminUsername!=null && adminPassword!=null && !"".equals(adminUsername) && !"".equals(adminPassword)) {
			this.sendAdminLogin(adminUsername, adminPassword);
		}
	}

	public void sendAdminLogin (final String username, final String password) {
		this.username = username;
		this.password = password;
		AsyncCallback<LoginResult> callback = new AsyncCallback<LoginResult> () {
			public void onFailure(Throwable caught) {
				Window.alert("Error while calling the Login service: " + caught.getMessage());
			}
			public void onSuccess(LoginResult result) {
				if (result.isSuccess()) {
					Cookies.setCookie("adminUsername", username);
					Cookies.setCookie("adminPassword", password);
					Cookies.setCookie("admin", "true");
					
					GameInfo.getInstance().setPlayer(result.getPlayer());
					GameInfo.getInstance().updatePlayerInformation(result.getPlayer());
					
					requestNextAdminDisplay();
				}
				else {
					Window.alert("Invalid admin login! Please try again.");
				}
			}
		};
		GameServices.loginService.sendAdminLogin(username, password, callback);
	}
	
	public void sendLogin(final String username, final String password) {
		this.username = username;
		this.password = password;
		AsyncCallback<LoginResult> callback = new AsyncCallback<LoginResult>() {

			public void onFailure(Throwable caught) {
				Window.alert("Error while calling Login Service: "
						+ caught.getMessage());
			}

			public void onSuccess(LoginResult result) {
				if (result.isSuccess()) {
					Cookies.setCookie("username", username);
					Cookies.setCookie("password", password);

					DockPanel parentPanel = WindowInformation.dockPanel;
					final LoginPanel loginPanel = WindowInformation.loginPanel;

					parentPanel.remove(loginPanel);
					parentPanel.remove(WindowInformation.mapWidget);

					result.getPlayer().setAMTVisitor(WindowInformation.AMTVisitor);
					
					GameInfo.getInstance().setPlayer(result.getPlayer());
					GameInfo.getInstance().updatePlayerInformation(result.getPlayer());

					requestNextDisplay();
				} else {
					Window.alert("Invalid login. Please try again.");
				}
			}
		};
		GameServices.loginService.sendLogin(username, password, callback);
	}
	
	public void loginNextTestPlayer () {
		//if (!GameInfo.getInstance().getPlayer().isLoggedIn()) {
			AsyncCallback <Integer> testLoginCallback = new AsyncCallback<Integer>() {
				public void onFailure (Throwable caught) {
					Window.alert("Error while calling Test Service: " + caught.getMessage());
				}
				
				public void onSuccess(Integer players) {
					String userName = "TestUser" + (players);
					sendLogin(userName, "test");
				}
			};
			GameServices.testService.getNextTestPlayer(testLoginCallback);
		//}
	}

	public void sendRefreshLogin(UserDTO player) {
		AsyncCallback<LoginResult> callback = new AsyncCallback<LoginResult>() {

			public void onFailure(Throwable caught) {
				//Window.alert("Error while calling Login Service: " + caught.getMessage());
				//something bad happened
				//Window.Location.reload();
			}

			public void onSuccess(LoginResult result) {
				if (result.isSuccess()) {
					DockPanel parentPanel = WindowInformation.dockPanel;
					final LoginPanel loginPanel = WindowInformation.loginPanel;

					parentPanel.remove(loginPanel);
					parentPanel.remove(WindowInformation.mapWidget);

					GameInfo.getInstance().setPlayer(result.getPlayer());
					GameInfo.getInstance().updatePlayerInformation(result.getPlayer());

					requestNextDisplay();
				} else {
					Window.alert("Invalid login. Please try again.");
				}
			}
		};
		// GameServices.loginService.sendLogin(username, password, callback);
		GameServices.loginService.sendRefreshLogin(player, callback);
	}

	public void showGame(long peekFormId) {
		DockPanel parentPanel = WindowInformation.dockPanel;
		CommsPanel commsPanel = WindowInformation.commsPanel;
		LocationInfoPanel locationInfoPanel = WindowInformation.locationPanel;
		AcceptPanel acceptPanel = WindowInformation.acceptPanel;

		parentPanel.remove(acceptPanel);

		parentPanel.add(commsPanel, DockPanel.EAST);
		parentPanel.add(WindowInformation.mapWidget, DockPanel.CENTER);
		parentPanel.add(locationInfoPanel, DockPanel.WEST);
		parentPanel.add(WindowInformation.treasurePanel, DockPanel.SOUTH);

		WindowInformation.titleLabel.setText("GeoGame" + " - " + GameInfo.getInstance().getPlayer().getUsername());

		commsPanel.setWidth("200px");
		commsPanel.setHeight("600px");
		
		commsPanel.initialize();
		GameInfo.getInstance().setPeekFormId(peekFormId);
		GameInfo.getInstance().refreshAll(true);
		
	}
	
	public void showAdminInterface() {
		RootPanel.get().remove(WindowInformation.loginPanel);
		RootPanel.get().add(WindowInformation.adminPanel);
	}

	public void select() {
		this.usernameTextBox.setFocus(true);
	}

	public static void openNewUserDialog() {
		NewUserWindow newUserDialog = WindowInformation.newUserWindow;
		newUserDialog.setTitle("New User");
		newUserDialog.clearContent();
		newUserDialog.show();
		//newUserDialog.setHeight("500px");
		//newUserDialog.setWidth("300px");
		//newUserDialog.setPixelSize(500, 500);

		newUserDialog.setPopupPosition(50, 50);
	}

	private void requestNextAdminDisplay() {
		AsyncCallback<DisplayResult> callback = new AsyncCallback<DisplayResult>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("requestNextAdminDisplay call failed! " + caught.getMessage());
			}
			
			public void onSuccess(final DisplayResult displayResult) {
				showAdminInterface();
			}
		};
		GameServices.loginService.requestNextAdminDisplay(GameInfo.getInstance().getPlayer(), callback);
	}
	
	private void requestNextDisplay() {
		// asynchronous callback for loginService.requestNextDisplay
		AsyncCallback<DisplayResult> callback = new AsyncCallback<DisplayResult>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("requestNextDisplay call failed!");
			}

			@Override
			public void onSuccess(final DisplayResult displayResult) {
				if (displayResult.getAcceptanceForm() != null) {
					final AcceptanceFormDTO form = displayResult.getAcceptanceForm();

					DockPanel parentPanel = WindowInformation.dockPanel;
					final AcceptPanel acceptPanel = WindowInformation.acceptPanel;
					acceptPanel.setHeight(800);
					acceptPanel.setWidth(900);


					parentPanel.add(acceptPanel, DockPanel.CENTER);


					acceptPanel.setContent(form.getContent());
					acceptPanel.setButton1Text(form.getButton1Text());
					acceptPanel.setButton2Text(form.getButton2Text());

					Window.scrollTo(0, 0);
					
					// async callback for acceptPanel.setCallback - ???
					AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
						@Override
						public void onFailure(Throwable caught) {
							// should do something with this
						}

						@Override
						public void onSuccess(Boolean result) {
							// async callback for gameService.sendFormAcceptance
							AsyncCallback<Boolean> callback;
							if (result) {
								callback = new AsyncCallback<Boolean>() {
									@Override
									public void onFailure(Throwable caught) {
									}

									@Override
									public void onSuccess(Boolean result) {
										// Window.alert("Calling requestNextDisplay3");
										requestNextDisplay();
									}
								};
							} else {
								callback = new AsyncCallback<Boolean>() {
									@Override
									public void onFailure(Throwable caught) {
									}

									// if the acceptPanel callback returns false AND
									// sendFormAcceptance succeeds, THEN
									// log the player out?!
									@Override
									public void onSuccess(Boolean result) {
										WindowInformation.commsPanel.sendLogout();
									}
								};
							}

							GameServices.gameService.sendFormAcceptance(GameInfo.getInstance().getPlayer(), form.getId(), result, callback);
						}
					};

					acceptPanel.setCallback(callback);
					acceptPanel.setVisible(true);
					return;
				} else if (displayResult.isShowGame()) {
					showGame(displayResult.getPeekFormId());
				}
			}
		};
		GameServices.loginService.requestNextDisplay(GameInfo.getInstance().getPlayer(), callback);
	}
}