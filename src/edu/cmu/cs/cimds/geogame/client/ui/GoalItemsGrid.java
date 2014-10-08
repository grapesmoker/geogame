package edu.cmu.cs.cimds.geogame.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;

public class GoalItemsGrid extends Grid {
	
//	public static final String MONEY_ICON_FILENAME = "coinbag.jpg";
	public static final String BLANK_ICON_FILENAME = "blank.png";

	private int numCells;
	
	private VerticalPanel[] blanks;
//	private List<ItemDTO> itemsCollected = new ArrayList<ItemDTO>();
	private List<ItemDTO> inventory = new ArrayList<ItemDTO>();
	private List<ItemTypeDTO> itemsToCollect = new ArrayList<ItemTypeDTO>();

	private ItemTypeImageCreator imageCreator;
	private String imageWidth = null;
	
	public GoalItemsGrid(int numRows, int numColumns) {
		super(numRows, numColumns);
		this.numCells = numRows * numColumns;
		this.blanks = new VerticalPanel[this.numCells];
		for(int i=0;i<numCells;i++) {
			this.blanks[i] = new VerticalPanel();
			this.blanks[i].add(new Image(BLANK_ICON_FILENAME));
			this.setWidget(i, this.blanks[i]);
		}
		this.clearContent();
	}

	public GoalItemsGrid(int numRows, int numColumns, ItemTypeImageCreator imageCreator) {
		this(numRows, numColumns);
		this.imageCreator = imageCreator;
	}

	public ItemTypeImageCreator getImageCreator() { return imageCreator; }
	public void setImageCreator(ItemTypeImageCreator imageCreator) { this.imageCreator = imageCreator; }

	public List<ItemDTO> getInventory() { return inventory; }
	public void setInventory(List<ItemDTO> inventory) { this.inventory = inventory; }

	public List<ItemTypeDTO> getItemsToCollect() { return itemsToCollect; }
	public void setItemsToCollect(List<ItemTypeDTO> itemsToCollect) {  this.itemsToCollect = itemsToCollect;}

	public void setImageWidth(String imageWidth) { this.imageWidth = imageWidth; }

	private void setWidget(int numCell, Widget w) {
//		if(numCell >= this.numCells) {
//			throw new IndexOutOfBoundsException();
//		}
		super.setWidget((int)Math.floor(numCell/this.numColumns), numCell%this.numColumns, w);
	}
	
	public void clearContent() {
		for(int i=0;i<numCells;i++) {
			this.setWidget(i, this.blanks[i]);
		}
	}

	public void refresh() {
		this.clearContent();
		
		List<ItemTypeDTO> itemTypesCopy = new ArrayList<ItemTypeDTO>(this.itemsToCollect);
		for(ItemDTO item : this.inventory) {
			itemTypesCopy.remove(item);
		}
//		itemTypesCopy.removeAll(this.inventory);
		
		Collections.sort(this.itemsToCollect);
		int i;
		for(i=0;i<itemTypesCopy.size();i++) {
//		for(i=0;i<this.itemsToCollect.size();i++) {
//			final ItemTypeDTO itemType = this.itemsToCollect.get(i);
			final ItemTypeDTO itemType = itemTypesCopy.get(i);
			Image image = this.imageCreator.createImage(itemType);
			if(this.imageWidth!=null) {
				image.setWidth(this.imageWidth);
			}
			//Label descriptionLabel = new Label(itemType.getName(), true);
			
			VerticalPanel itemPanel = new VerticalPanel();
			SimplePanel colorPanel = new SimplePanel();
//			if(!itemTypesCopy.contains(itemType)) {
//				colorPanel.setStyleName("takenItemBackground");
//			} else {
				colorPanel.setStyleName("searchItemBackground");
//			}
			colorPanel.setWidget(itemPanel);
			itemPanel.add(image);
			//itemPanel.add(descriptionLabel);
			this.setWidget(i, colorPanel);
		}
	}
}