package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.TradeStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

public class PlayerTradePanel extends VerticalPanel {

	SimplePanel titlePanel = new SimplePanel();

	VerticalPanel contentPanel = new VerticalPanel();
	VerticalPanel moneyPanel = new VerticalPanel();
	Label moneyLabel = new Label();
	InventoryGrid inventoryGrid = new InventoryGrid(2,4);

	VerticalPanel offerPanel = new VerticalPanel();
	TextBox goldOfferTextBox = new TextBox();
	InventoryGrid itemsOfferGrid = new InventoryGrid(2,4);
	
	private UserDTO player;
	private TradeStruct tradeStruct = new TradeStruct();
	
	public UserDTO getPlayer() { return player; }
	public void setPlayer(UserDTO player) { this.player = player; }

	public void init() {
		Image moneyImage = new Image(TreasurePanel.MONEY_ICON_FILENAME);
		moneyImage.setWidth("100px");
		
		this.setBorderWidth(1);
		this.moneyPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		this.moneyPanel.add(moneyImage);
		this.moneyPanel.add(moneyLabel);
		this.moneyPanel.setPixelSize(100, 100);
		
		Label inventoryLabel = new Label("Inventory");
		inventoryLabel.setStyleName("largeText15");
		
		this.contentPanel.add(inventoryLabel);
		this.contentPanel.add(moneyPanel);
		this.contentPanel.add(inventoryGrid);

		Label tradeLabel = new Label("Trade:");
		tradeLabel.setStyleName("largeText15");
		this.offerPanel.add(tradeLabel);
		this.offerPanel.add(goldOfferTextBox);
		this.offerPanel.add(itemsOfferGrid);
		
		this.add(titlePanel);
		this.add(contentPanel);
		this.add(offerPanel);

		Button refreshButton = new Button("Refresh");
		refreshButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				PlayerTradePanel.this.refresh();
			}
		});
		Button resetButton = new Button("Reset");
		resetButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				PlayerTradePanel.this.reset();
			}
		});
		this.add(refreshButton);
		this.add(resetButton);

		this.inventoryGrid.setBorderWidth(1);
		this.inventoryGrid.setImageCreator(new ItemImageCreator() {
			@Override
			public Image createImage(final ItemDTO item) {
				Image image = new Image(item.getItemType().getIconFilename());
				image.setWidth(WindowInformation.imageWidth);
				image.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						tradeStruct.inventoryItems.remove(item);
						tradeStruct.tradeItems.add(item);
						inventoryGrid.refresh();
						itemsOfferGrid.refresh();
					}
				});
				return image;
			}
		});

		this.itemsOfferGrid.setBorderWidth(1);
		this.itemsOfferGrid.setImageCreator(new ItemImageCreator() {
			@Override
			public Image createImage(final ItemDTO item) {
				Image image = new Image(item.getItemType().getIconFilename());
				image.setWidth(WindowInformation.imageWidth);
				image.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						tradeStruct.tradeItems.remove(item);
						tradeStruct.inventoryItems.add(item);
						inventoryGrid.refresh();
						itemsOfferGrid.refresh();
					}
				});
				return image;
			}
		});
	}
	
	public void clearContent() {
		this.titlePanel.clear();
		this.moneyLabel.setText("");
		this.inventoryGrid.clearContent();
		this.goldOfferTextBox.setText("0");
		this.itemsOfferGrid.clearContent();
	}

	public void refresh() {
		this.clearContent();
		
		//this.player = player;
		Label playerLabel = new Label(this.player.getUsername());
		playerLabel.setStyleName("largeText15");
		this.titlePanel.add(playerLabel);
		this.moneyLabel.setText(this.player.getScore() + "G");
		
		this.goldOfferTextBox.setText(String.valueOf(tradeStruct.gold));
		
		this.inventoryGrid.setInventory(this.tradeStruct.inventoryItems);
		this.inventoryGrid.refresh();
		
		this.itemsOfferGrid.setInventory(this.tradeStruct.tradeItems);
		this.itemsOfferGrid.refresh();
		
//		Collections.sort(this.tradeStruct.inventoryItems);
//		for(final ItemDTO item : this.tradeStruct.inventoryItems) {
//			createItemPanel(item, ItemMode.INVENTORY);
//		}
//		Collections.sort(this.tradeStruct.tradeItems);
//		for(final ItemDTO item : this.tradeStruct.tradeItems) {
//			createItemPanel(item, ItemMode.TRADE);
//		}
	}

	public void reset() {
		this.tradeStruct.reset(this.player);
		this.refresh();
	}
	
	public TradeStruct getTradeStruct() {
		try {
			this.tradeStruct.gold = Integer.valueOf(this.goldOfferTextBox.getText());
		} catch(Exception ex) {
			Window.alert("Integer conversion exception!");
			return null;
		}
		return tradeStruct;
	}
	
	enum ItemMode {
		INVENTORY,
		TRADE;
	}
	
//	private void createItemPanel(final ItemDTO item, ItemMode itemMode) {
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
//		ClickHandler clickHandler = null;
//		switch(itemMode) {
//		case INVENTORY:
//			this.inventoryPanel.add(itemPanel);
//			clickHandler = new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					inventoryPanel.remove(itemPanel);
//					tradeStruct.inventoryItems.remove(item);
//					tradeStruct.tradeItems.add(item);
//					createItemPanel(item, ItemMode.TRADE);
//				}
//			};
//			break;
//		case TRADE:
//			this.itemsOfferPanel.add(itemPanel);
//			clickHandler = new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					itemsOfferPanel.remove(itemPanel);
//					tradeStruct.tradeItems.remove(item);
//					tradeStruct.inventoryItems.add(item);
//					createItemPanel(item, ItemMode.INVENTORY);
//				}
//			};
//			break;
//		}
//		image.addClickHandler(clickHandler);
//	}
}