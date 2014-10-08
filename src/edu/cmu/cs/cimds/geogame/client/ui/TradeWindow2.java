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
//public class TradeWindow2 extends DialogBox {
//
//	public static String titleString = "***TRADE BETWEEN PLAYER1 AND PLAYER2 HERE***";
//	
//	private Label titleLabel = new Label(titleString);
//	private PlayerTradePanel2 player1Panel = new PlayerTradePanel2();
//	private PlayerTradePanel2 player2Panel = new PlayerTradePanel2();
//	private HorizontalPanel tradePanel = new HorizontalPanel();
//	
//	private TradeOfferDTO tradeOffer;
//	/**
//	 * Creates a new instance of TradeWindow2
//	 */
//	public TradeWindow2(TradeOfferDTO tradeOffer) {
//		super(false,false);
//		this.init();
//		
//		this.tradeOffer = tradeOffer;
//		//This display is different because it cannot be changed - it is simply display and an accept/reject button
//		//Will be dealt with later.
//		this.player1Panel.getTradeStruct().gold = tradeOffer.getMoney1();
//		this.player1Panel.getTradeStruct().tradeItems = tradeOffer.getItems1();
//		this.player2Panel.getTradeStruct().gold = tradeOffer.getMoney2();
//		this.player2Panel.getTradeStruct().tradeItems = tradeOffer.getItems2();
//		this.refresh(tradeOffer.getPlayer1(), tradeOffer.getPlayer2());
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
//		Button acceptButton = new Button("Accept");
//		acceptButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
////				Window.alert("ACCEPT!");
//				TradeWindow2.this.sendTradeReply(true);
//				TradeWindow2.this.hide();
//			}
//		});
//
//		Button refuseButton = new Button("Refuse");
//		refuseButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
////				Window.alert("REFUSE!");
//				TradeWindow2.this.sendTradeReply(false);
//				TradeWindow2.this.hide();
//			}
//		});
//		
//		player1Panel.init();
//		player2Panel.init();
//		tradePanel.add(player1Panel);
//		tradePanel.add(player2Panel);
//		mainPanel.add(tradePanel);
//		mainPanel.add(acceptButton);
//		mainPanel.add(refuseButton);
//		
//		
//		Button closeButton = new Button("Close");
//		closeButton.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				TradeWindow2.this.hide();
//			}
//		});
//		mainPanel.add(closeButton);
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
//		//this.titleLabel.setText(player1.getUsername() + " trades with " + player2.getUsername());
//		this.titleLabel.setText(player1.getUsername() + " has sent you an offer:");
//		this.titleLabel.setStyleName("largeText20");
//
//		this.player1Panel.setPlayer(player2);
//		this.player2Panel.setPlayer(player1);
//		this.player1Panel.refresh();
//		this.player2Panel.refresh();
//	}
//	
//	public void sendTradeReply(boolean accept) {
//		AsyncCallback<ActionResult> callback = new AsyncCallback<ActionResult>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				Window.alert("Failed to post trade reply! - " + caught.getMessage());
//			}
//
//			@Override
//			public void onSuccess(ActionResult result) {
//				GameInformation.getInstance().updateInformation(result.getGameInformation());
//				//Come back to refreshMap with updated Map Information.
//				GameInformation.getInstance().refreshAll(false);
////				Window.alert("Succeeded in replying to trade offer!");
//			}
//		};
//		GameServices.gameService.replyTradeOffer(GameInformation.getInstance().getPlayer(), this.tradeOffer.getId(), accept, callback);
//	}
//}