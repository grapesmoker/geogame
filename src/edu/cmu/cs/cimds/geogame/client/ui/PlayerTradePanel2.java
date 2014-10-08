package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.model.dto.TradeStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

public class PlayerTradePanel2 extends VerticalPanel {

	SimplePanel titlePanel = new SimplePanel();

//	VerticalPanel contentPanel = new VerticalPanel();
//	VerticalPanel moneyPanel = new VerticalPanel();
//	Label moneyLabel = new Label();
//	HorizontalPanel inventoryPanel = new HorizontalPanel();

	VerticalPanel offerPanel = new VerticalPanel();
	Label goldOfferLabel = new Label();
	InventoryGrid itemsOfferGrid = new InventoryGrid(2,4,new NoopItemImageCreator());
	
	private UserDTO player;
	private TradeStruct tradeStruct = new TradeStruct();

	public UserDTO getPlayer() { return player; }
	public void setPlayer(UserDTO player) { this.player = player; }

	public void init() {
		Image moneyImage = new Image(TreasurePanel.MONEY_ICON_FILENAME);
		moneyImage.setWidth("100px");
		
		this.setBorderWidth(1);
//		this.moneyPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
//		this.moneyPanel.add(moneyImage);
//		this.moneyPanel.add(moneyLabel);
//		this.moneyPanel.setPixelSize(100, 100);
//		
//		this.contentPanel.add(moneyPanel);
//		this.contentPanel.add(inventoryPanel);
		Label goldOfferTitleLabel = new Label("Gold:");
		goldOfferTitleLabel.setHorizontalAlignment(ALIGN_LEFT);
		goldOfferTitleLabel.setStyleName("largeText12");
		this.offerPanel.add(goldOfferTitleLabel);
		this.offerPanel.add(goldOfferLabel);
		itemsOfferGrid.setBorderWidth(1);
		this.offerPanel.add(itemsOfferGrid);
		
		this.add(titlePanel);
//		this.add(contentPanel);
		this.add(offerPanel);
	}
	
	public void clearContent() {
		this.titlePanel.clear();
//		this.moneyLabel.setText("");
//		this.inventoryPanel.clear();
		this.goldOfferLabel.setText("0");
		this.itemsOfferGrid.clearContent();
	}

	public void refresh() {
		this.clearContent();
		
		Label playerLabel = new Label(this.player.getUsername());
		playerLabel.setStyleName("largeText15");
		this.titlePanel.add(playerLabel);
//		this.moneyLabel.setText(this.player.getScore() + "G");
		
		this.goldOfferLabel.setText(String.valueOf(tradeStruct.gold));
		this.itemsOfferGrid.setInventory(this.tradeStruct.tradeItems);
		this.itemsOfferGrid.refresh();
//		for(final ItemDTO item : this.tradeStruct.inventoryItems) {
//			createItemPanel(item, ItemMode.INVENTORY);
//		}
//		Collections.sort(this.tradeStruct.tradeItems);
//		for(final ItemDTO item : this.tradeStruct.tradeItems) {
//			createItemPanel(item/*, ItemMode.TRADE*/);
//		}
	}

	public TradeStruct getTradeStruct() {
//		try {
//			this.tradeStruct.gold = Integer.valueOf(this.goldOfferLabel.getText());
//		} catch(NumberFormatException ex) {
//			Window.alert("Integer conversion exception!");
//			return null;
//		}
		return tradeStruct;
	}
	
//	enum ItemMode {
//		INVENTORY,
//		TRADE;
//	}
	
//	private void createItemPanel(final ItemDTO item/*, ItemMode itemMode*/) {
//		final Image image = new Image(item.getItemType().getIconFilename());
//		image.setWidth(WindowInformation.imageWidth);
//
//		Label descriptionLabel = new Label(item.getItemType().getName());
//		
//		final VerticalPanel itemPanel = new VerticalPanel();
//		itemPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
//		itemPanel.add(image);
//		itemPanel.add(descriptionLabel);
//		itemPanel.setPixelSize(100, 100);
//		
////		switch(itemMode) {
//////		case INVENTORY:
//////			this.inventoryPanel.add(itemPanel);
//////			break;
////		case TRADE:
//			this.itemsOfferPanel.add(itemPanel);
////			break;
////		}
//	}
}