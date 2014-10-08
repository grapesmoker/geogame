//package edu.cmu.cs.cimds.geogame.client.ui;
//
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Image;
//
//import edu.cmu.cs.cimds.geogame.client.ActionResult;
//import edu.cmu.cs.cimds.geogame.client.GameInfo;
//import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;
//import edu.cmu.cs.cimds.geogame.client.services.GameServices;
//
//public class SaleItemImageCreator implements ItemImageCreator{
//	public Image createImage(final ItemDTO item) {
//		Image image = new Image(item.getItemType().getIconFilename());
//		image.setWidth(WindowInformation.imageWidth);
//		
//		final AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
//			public void onFailure(Throwable caught) {
//				//***Window.alert("Error while calling Game Service: " + caught.getMessage());
//			}
//
//			public void onSuccess(Integer result) {
//				final int price = result;
//				boolean saleConfirmation = Window.confirm("You are offered " + price + " for your item. Accept?");
//				if(!saleConfirmation) {
//					return;
//				} else {
//					AsyncCallback<ActionResult> callback = new AsyncCallback<ActionResult>() {
//						public void onFailure(Throwable caught) {
//							//***Window.alert("Error while calling Game Service: " + caught.getMessage());
//						}
//
//						public void onSuccess(ActionResult actionResult) {
//							GameInfo.getInstance().updateInformation(actionResult.getGameInformation());
//							GameInfo.getInstance().refreshAll(false);
//							GameInfo.getInstance().log("Item sold for " + price + "G!");
//						}
//					};
//					GameServices.gameService.sellItem(GameInfo.getInstance().getPlayer(), item.getId(), result, callback);
//				}
//			}
//		};
//		image.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				GameServices.gameService.getSalePrice(GameInfo.getInstance().getPlayer(), item.getId(), callback);
//			}
//		});
//		return image;
//	}
//}