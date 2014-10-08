package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.user.client.ui.Button;

import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;

public class GetItemButton extends Button {
	
	// This class extends the normal Button class to provide a few extra functionalities.
	// Specifically, it allows you to associate a button with the specific item that you're
	// trying to pick up. The reason for doing this is because you can then keep track of
	// which button picks up which item, and thus also mimic button presses via the test
	// interface instead of calling the service directly.
	// -JV 02/14/2011
	
	
	public GetItemButton(String text) {
		super(text);
	}
	
	public GetItemButton() {
		super();
	}
	
	private String buttonName;
	private ItemDTO buttonItem;
	
	public void setButtonName (String buttonName) {
		this.buttonName = buttonName; 
	}
	
	public String getButtonName () {
		return this.buttonName;
	}
	
	public void setButtonItem (ItemDTO buttonItem) {
		this.buttonItem = buttonItem;
	}
	
	public ItemDTO getButtonItem () {
		return this.buttonItem;
	}
}
