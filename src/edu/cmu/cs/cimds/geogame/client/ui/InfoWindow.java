package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InfoWindow extends DialogBox {
	
	VerticalPanel containerPanel = new VerticalPanel();
	Button okButton = new Button("OK");
	Button cancelButton = new Button("Cancel");
	Boolean choice = true;
	HTML content = new HTML();
	Grid grid = new Grid(2,1);
	
	public InfoWindow() {
		super(false, true);
		this.init(200, 50, "", false);
	}
	
	public InfoWindow(int width, int height) {
		super(false, true);
		this.init(width, height, "", false);
	}
	
	public InfoWindow(int width, int height, String content) {
		super(false, true);
		this.init(width, height, content, false);
	}
	
	public InfoWindow(int width, int height, String content, Boolean confirm) {
		super(false, true);
		this.init(width, height, content, confirm);
	}
	
	public void init(int width, int height, String contentString, Boolean confirm) {

		grid.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		grid.getCellFormatter().setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		
		setContent(contentString);
		grid.setWidget(0, 0, content);
		HorizontalPanel hPanel = new HorizontalPanel();
		if (confirm) {
			hPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			hPanel.add(okButton);
			hPanel.add(cancelButton);
			grid.setWidget(1, 0, hPanel);
		}
		else {
			grid.setWidget(1, 0, okButton);
		}
		
		okButton.ensureDebugId("infoWindowOk");
		content.ensureDebugId("infoWindowText");
		
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				okButtonClicked();
			}
		});
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelButtonClicked();
			}
		});
		
		containerPanel.add(grid);
		containerPanel.setPixelSize(width, height);
		this.setWidget(containerPanel);
	}
	
	public void setContent(String contentString) {
		//content.setText(contentString);
		content.setHTML(contentString);
	}
	
	public void okButtonClicked() {
		this.choice = true;
		InfoWindow.this.hide();
	}
	
	public void cancelButtonClicked() {
		this.choice = false;
		InfoWindow.this.hide();
	}
	
	public Boolean getChoice() {
		return this.choice;
	}

}
