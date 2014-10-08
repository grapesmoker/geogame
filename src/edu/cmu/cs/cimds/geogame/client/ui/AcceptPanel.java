package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AcceptPanel extends VerticalPanel {
	
	RichTextArea textArea;
	Button yesButton;
	Button noButton;
	AsyncCallback<Boolean> callback;
	
	public void clearContent() {
		this.textArea.setText("");
	}

	public AcceptPanel() {
		this.textArea = new RichTextArea();
		this.textArea.setEnabled(false);
		this.yesButton = new Button("I accept");
		this.noButton = new Button("I do not accept");
		
		this.yesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AcceptPanel.this.callback.onSuccess(true);
			}
		});
		this.noButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AcceptPanel.this.callback.onSuccess(false);
			}
		});
		
		this.add(this.textArea);
		this.add(this.yesButton);
		this.add(this.noButton);
	}
	
	public void setContent(String content) {
//		RichTextArea newTextArea = new RichTextArea();
//		newTextArea.setHeight(this.textArea.getOffsetHeight() + "px");
//		newTextArea.setWidth(this.textArea.getOffsetWidth() + "px");
//		newTextArea.setHTML(content);
		
//		this.remove(this.textArea);
//		this.insert(newTextArea, 0);
//		this.textArea = newTextArea;
		this.textArea.setHTML(content);
	}
	
	public void setButton1Text(String button1Text) {
		if(button1Text==null) {
			this.yesButton.setVisible(false);
		} else {
			this.yesButton.setVisible(true);
			this.yesButton.setText(button1Text);
		}
	}
	
	public void setButton2Text(String button2Text) {
		if(button2Text==null) {
			this.noButton.setVisible(false);
		} else {
			this.noButton.setVisible(true);
			this.noButton.setText(button2Text);
		}
	}

	public void setCallback(AsyncCallback<Boolean> callback) {
		this.callback = callback;
	}
	
	public void setHeight(int height) {
		this.textArea.setHeight((height-50) + "px");
		super.setHeight(height + "px");
	}

	public void setWidth(int width) {
		this.textArea.setWidth((width-10) + "px");
		super.setWidth(width + "px");
	}
}