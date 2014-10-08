package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ChatPanel extends Composite {
	private static final int MAX_LINES = 20;
	
	private VerticalPanel messagePanel = new VerticalPanel();
	private int lineCount = 0;
	private int maxMessages = MAX_LINES;

	public ChatPanel() {
		sinkEvents(Event.ONPASTE);
		
		initWidget( this.messagePanel );
	}

	public ChatPanel(int maxMessages) {
		initWidget( this.messagePanel );
		this.setMaxMessages(maxMessages);
	}

	public void setMaxMessages(int maxMessages) { this.maxMessages = maxMessages; }

	public void addMessage( String message ) {
		this.messagePanel.add( new HTML( message ) );
		if(this.lineCount < this.maxMessages)
			this.lineCount++;
		else
			this.messagePanel.remove(0);
	}

	public void clearMessages() {
		this.messagePanel.clear();
		this.lineCount = 0;
	}

	public static String escapeHtml(String maybeHtml) {
		final Element div = DOM.createDiv();
		DOM.setInnerText(div, maybeHtml);
		return DOM.getInnerHTML(div);
	}
	
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONPASTE: {
			event.stopPropagation();  
            event.preventDefault();  
            break;
		}
		}
	}
}