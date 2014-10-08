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
//import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.VerticalPanel;
//
//import edu.cmu.cs.cimds.geogame.client.ActionResult;
//import edu.cmu.cs.cimds.geogame.client.GameInformation;
//import edu.cmu.cs.cimds.geogame.client.model.dto.TradeOfferDTO;
//import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
//import edu.cmu.cs.cimds.geogame.client.services.GameServices;
//
///**
// * Client entry point for the TradeWindow.
// *
// * @author Antonio Juarez <ajuarez@andrew.cmu.edu>
// */
//public class TradeWindow extends DialogBox {
//
//	public static String titleString = "***TRADE BETWEEN PLAYER1 AND PLAYER2 HERE***";
//	
//	private Label titleLabel = new Label(titleString);
//	private PlayerTradePanel player1Panel = new PlayerTradePanel();
//	private PlayerTradePanel player2Panel = new PlayerTradePanel();
//	private HorizontalPanel tradePanel = new HorizontalPanel();
//	/**
//	 * Creates a new instance of TradeWindow
//	 */
//	public TradeWindow() {
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
//		mainPanel.add(titleLabel);
//		
//		Button tradeButton = new Button("Trade");
//		tradeButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
////				Window.alert("TRADE!");
//				TradeWindow.this.sendTradeOffer();
//			}
//		});
//
//		player1Panel.init();
//		player2Panel.init();
//		tradePanel.add(player1Panel);
//		tradePanel.add(player2Panel);
//		mainPanel.add(tradePanel);
//		mainPanel.add(tradeButton);
//		
//		
//		Button cancelButton = new Button("Cancel");
//		cancelButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				TradeWindow.this.hide();
//			}
//		});
//		mainPanel.add(cancelButton);
//		this.setWidget(mainPanel);
//	}
//	
//	public void clearContent() {
////		this.player1Panel.clearContent();
////		this.player2Panel.clearContent();
//	}
//
//	public void refresh(UserDTO player1, UserDTO player2) {
//		this.clearContent();
////		this.titleLabel.setText(player1.getUsername() + " trades with " + player2.getUsername());
//		this.titleLabel.setText("Make your offer");
//		this.titleLabel.setStyleName("largeText20");
//		
//		this.player1Panel.setPlayer(player1);
//		this.player2Panel.setPlayer(player2);
//		this.player1Panel.reset();
//		this.player2Panel.reset();
//	}
//	
//	public void sendTradeOffer() {
//		TradeOfferDTO tradeOffer = new TradeOfferDTO();
//		tradeOffer.setMoney1(player1Panel.getTradeStruct().gold);
//		tradeOffer.setMoney2(player2Panel.getTradeStruct().gold);
//		tradeOffer.setItems1(player1Panel.getTradeStruct().tradeItems);
//		tradeOffer.setItems2(player2Panel.getTradeStruct().tradeItems);
//		
//		//Verify that players have enough money for the trade
//		if(tradeOffer.getMoney1() > player1Panel.getPlayer().getScore()) {
//			Window.alert(player1Panel.getPlayer().getUsername() + " does not have enough gold!");
//			return;
//		}
//		if(tradeOffer.getMoney2() > player2Panel.getPlayer().getScore()) {
//			Window.alert(player2Panel.getPlayer().getUsername() + " does not have enough gold!");
//			return;
//		}
//		
//		
//		AsyncCallback<ActionResult> callback = new AsyncCallback<ActionResult>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				Window.alert("Failed to post trade offer! - " + caught.getMessage());
//			}
//
//			@Override
//			public void onSuccess(ActionResult result) {
////				Window.alert("Succeeded in posting trade offer!");
//				Window.alert("Trade offer posted");
//				TradeWindow.this.hide();
//			}
//		};
//		
//		
//		GameServices.gameService.sendTradeOffer(GameInformation.getInstance().getPlayer(), player2Panel.getPlayer().getId(), tradeOffer, callback);
//	}
//}