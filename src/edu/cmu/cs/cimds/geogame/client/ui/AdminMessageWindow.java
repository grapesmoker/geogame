package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.GameInfo;
import edu.cmu.cs.cimds.geogame.client.model.dto.MessageDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;

public class AdminMessageWindow extends DialogBox {

	Grid widgetGrid = new Grid(4,1);
	
	Label messageTextLabel = new Label("Message to send:");
	TextArea messageText = new TextArea();
	
	Button messageSend = new Button("Send message");
	Button cancelButton = new Button("Cancel");
	
	public AdminMessageWindow() {
		super(false, true);
		this.init();
	}
	
	public void init () {
		VerticalPanel containerPanel = new VerticalPanel();
		
		messageSend.setWidth("100px");
		cancelButton.setWidth("100px");
		messageText.setVisibleLines(5);
		messageText.setCharacterWidth(80);
		
		widgetGrid.setWidget(0, 0, messageTextLabel);
		widgetGrid.setWidget(1, 0, messageText);
		widgetGrid.setWidget(2, 0, messageSend);
		widgetGrid.setWidget(3, 0, cancelButton);
		
		messageText.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if(event.getCharCode()==KeyCodes.KEY_ENTER) {
					messageSend.click();
				}
			}
		});
		
		messageSend.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String warning = "Are you sure you want to send this message? It will be disseminated to all users!";
				if (Window.confirm(warning)) {
					String message = messageText.getText();
					UserDTO adminUser = GameInfo.getInstance().getPlayer();
					
					MessageDTO newMessage = new MessageDTO();
					newMessage.setContent(message);
					newMessage.setSender(adminUser.getUsername());
					newMessage.setBroadcast(true);
					
					GameServices.messageService.sendAdminMessage(adminUser, newMessage, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Could not send admin message!\n" + caught.getMessage());
						}
						
						public void onSuccess(Void result) {
							Window.alert("Sent admin message successfully!");
						}
					});
				}
			}
		});
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminMessageWindow.this.hide();
			}
		});
		
		containerPanel.add(widgetGrid);
		
		this.setWidget(containerPanel);
	}
	
	public void clearContent() {
		this.messageText.setText("");
	}
}
