package edu.cmu.cs.cimds.geogame.client.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;

public class ItemTypeGrid extends Grid {
	
//	public static final String MONEY_ICON_FILENAME = "coinbag.jpg";
	public static final String BLANK_ICON_FILENAME = "blank.png";

	private int numCells;
	
	private VerticalPanel[] blanks;
	private List<ItemTypeDTO> itemTypes = new ArrayList<ItemTypeDTO>();
	private ItemTypeImageCreator imageCreator;
	private String imageWidth = null;
	
	public ItemTypeGrid(int numRows, int numColumns) {
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

	public ItemTypeGrid(int numRows, int numColumns, ItemTypeImageCreator imageCreator) {
		this(numRows, numColumns);
		this.imageCreator = imageCreator;
	}

	public List<ItemTypeDTO> getItemTypes() { return itemTypes; }
	public void setItemTypes(List<ItemTypeDTO> itemTypes) { this.itemTypes = itemTypes; }

	public ItemTypeImageCreator getImageCreator() { return imageCreator; }
	public void setImageCreator(ItemTypeImageCreator imageCreator) { this.imageCreator = imageCreator; }

	public void setImageWidth(String imageWidth) { this.imageWidth = imageWidth; }

	public void setWidget(int numCell, Widget w) {
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
		
		Collections.sort(this.itemTypes);
		for(int i=0;i<this.itemTypes.size();i++) {
			final ItemTypeDTO itemType = this.itemTypes.get(i);
			Image image = this.imageCreator.createImage(itemType);
			if(this.imageWidth!=null) {
				image.setWidth(this.imageWidth);
			}
			//Label descriptionLabel = new Label(itemType.getName(), true);
			
			VerticalPanel itemPanel = new VerticalPanel();
			itemPanel.add(image);
			//itemPanel.add(descriptionLabel);
			this.setWidget(i, itemPanel);
		}
	}
}