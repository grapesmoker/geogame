//package edu.cmu.cs.cimds.geogame.client.ui;
//
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.DialogBox;
//import com.google.gwt.user.client.ui.HasHorizontalAlignment;
//import com.google.gwt.user.client.ui.HorizontalPanel;
//import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.VerticalPanel;
//
//import edu.cmu.cs.cimds.geogame.client.ActionResult;
//import edu.cmu.cs.cimds.geogame.client.GameInfo;
//import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;
//import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
//import edu.cmu.cs.cimds.geogame.client.services.GameServices;
//
///**
// * Client entry point for the LocationInformationWindow.
// *
// * @author Antonio Juarez <ajuarez@andrew.cmu.edu>
// */
//public class LocationInformationWindow extends DialogBox {
//
//	public static String titleString = "***LOCATION NAME HERE***";
//	
//	public static String defaultLocationImageFilename = "town_icon.gif";
//
//	private Label titleLabel = new Label(titleString);
//	private Image prettyImage = new Image(defaultLocationImageFilename);
//	private HorizontalPanel locationContents = new HorizontalPanel();
//	private VerticalPanel itemsPanel = new VerticalPanel();
//	//private HorizontalPanel itemsContentsPanel = new HorizontalPanel();
//	private InventoryGrid itemsContentsGrid = new InventoryGrid(2,4);
////	private VerticalPanel combosPanel = new VerticalPanel();
////	private VerticalPanel combosContentsPanel = new VerticalPanel();
//	/**
//	 * Creates a new instance of LocationInformationWindow
//	 */
//	public LocationInformationWindow() {
//		super(false,false);
//		this.init();
//	}
//
//	/**
//	 * The entry point method, called automatically by loading a module
//	 * that declares an implementing class as an entry-point
//	 */
//	
//	public void init() {
//		// Define widget elements
//		final VerticalPanel mainPanel = new VerticalPanel();
//		titleLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//		titleLabel.setStyleName("largeText20");
//		mainPanel.add(titleLabel);
//		mainPanel.add(prettyImage);
//
//		Label itemsLabel = new Label("Items");
//		itemsLabel.setStyleName("largeText15");
//		itemsPanel.add(itemsLabel);
//		this.itemsContentsGrid.setBorderWidth(1);
//		itemsPanel.add(itemsContentsGrid);
//
////		Label combosLabel = new Label("Combos");
////		combosLabel.setStyleName("largeText15");
////		combosPanel.add(combosLabel);
////		this.combosContentsPanel.setBorderWidth(1);
////		combosPanel.add(combosContentsPanel);
//		
//		locationContents.add(itemsPanel);
////		locationContents.add(combosPanel);
//		
//		mainPanel.add(locationContents);
//		
//		Button closeButton = new Button("Close");
//		closeButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				LocationInformationWindow.this.hide();
//			}
//		});
//		mainPanel.add(closeButton);
//		
//		this.itemsContentsGrid.setImageCreator(new ItemImageCreator() {
//			@Override
//			public Image createImage(final ItemDTO item) {
//				Image image = new Image(item.getItemType().getIconFilename());
//				image.setWidth(WindowInformation.imageWidth);
//				
//				final AsyncCallback<ActionResult> callback = new AsyncCallback<ActionResult>() {
//					public void onFailure(Throwable caught) {
//						//***Window.alert("Error while calling Game Service: " + caught.getMessage());
//					}
//
//					public void onSuccess(ActionResult actionResult) {
//						GameInfo.getInstance().updateInformation(actionResult.getGameInformation());
//						GameInfo.getInstance().refreshAll(false);
////						Window.alert("Item purchased. Your gold is now " + GameInformation.getInstance().getPlayer().getScore());
//						refresh(GameInfo.getInstance().getLocation());
//					}
//				};
//				image.addClickHandler(new ClickHandler() {
//					@Override
//					public void onClick(ClickEvent event) {
//						if(GameInfo.getInstance().getPlayer().getScore()<item.getPrice()) {
//							Window.alert("Not enough gold!");
//						} else {
//							GameServices.gameService.takeItem(GameInfo.getInstance().getPlayer(), item.getId(), callback);
//						}
//					}
//				});
//				return image;
//			}
//		});
//		
//		this.setWidget(mainPanel);
//	}
//	
//	public void clearContent() {
//		this.itemsContentsGrid.clear();
////		this.combosContentsPanel.clear();
//	}
//
//	public void refresh(LocationDTO location) {
//		this.clearContent();
//		this.titleLabel.setText(location.getName());
//		
//		this.itemsContentsGrid.setInventory(location.getItems());
//		this.itemsContentsGrid.refresh();
//		
////		if(location.getCombos().isEmpty()) {
////			this.combosContentsPanel.add(new Label("No combos available!"));
////		}
////		for(final ComboDTO combo : location.getCombos()) {
////			HorizontalPanel comboPanel = new HorizontalPanel();
////			for(final ItemTypeDTO itemType : combo.getComboType().getItemTypes()) {
////				VerticalPanel imagePanel = new VerticalPanel();
////				
////				Image image = new Image(itemType.getIconFilename());
////				image.setWidth(WindowInformation.imageWidth);
////				imagePanel.add(image);
////				
////				Label descriptionLabel = new Label(itemType.getName() + ": " + itemType.getBasePrice() + "G");
////				imagePanel.add(descriptionLabel);
////				
////				comboPanel.add(imagePanel);
////			}
////			
////			final AsyncCallback<ActionResult> callback = new AsyncCallback<ActionResult>() {
////				public void onFailure(Throwable caught) {
////					//***Window.alert("Error while calling Game Service: " + caught.getMessage());
////				}
////
////				public void onSuccess(ActionResult actionResult) {
////					GameInformation.getInstance().updateInformation(actionResult.getGameInformation());
////					GameInformation.getInstance().refreshAll(false);
////					Window.alert("Combo sold! Your gold is now " + GameInformation.getInstance().getPlayer().getScore());
////					GameInformation.getInstance().log("Combo sold! Your gold is now " + GameInformation.getInstance().getPlayer().getScore());
////					refresh(GameInformation.getInstance().getLocation());
////				}
////			};
////			
////			Label descriptionLabel = new Label(combo.getComboType().getName() + ": " + combo.getPrice() + "G");
////			comboPanel.add(descriptionLabel);
////
////			Button sellButton = new Button("Sell");
////			sellButton.addClickHandler(new ClickHandler() {
////				@Override
////				public void onClick(ClickEvent event) {
////					GameServices.gameService.sellCombo(GameInformation.getInstance().getPlayer(), combo.getId(), callback);
////				}
////			});
////			if(GameInformation.getInstance().getPlayer().getInventory().containsAll(combo.getComboType().getItemTypes())) {
////				sellButton.setEnabled(true);
////			} else {
////				sellButton.setEnabled(false);
////			}
////			comboPanel.add(sellButton);
////
////			this.combosContentsPanel.add(comboPanel);
////		}
//	}
//}