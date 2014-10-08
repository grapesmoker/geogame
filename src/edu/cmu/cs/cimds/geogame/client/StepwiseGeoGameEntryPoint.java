package edu.cmu.cs.cimds.geogame.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MapZoomEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.cmu.cs.cimds.geogame.client.ui.AcceptPanel;
import edu.cmu.cs.cimds.geogame.client.ui.CommsPanel;
import edu.cmu.cs.cimds.geogame.client.ui.LocationInfoPanel;
import edu.cmu.cs.cimds.geogame.client.ui.LoginPanel;
import edu.cmu.cs.cimds.geogame.client.ui.TreasurePanel;
import edu.cmu.cs.cimds.geogame.client.ui.WindowInformation;
import edu.cmu.cs.cimds.geogame.client.ui.map.DevUtil;

public class StepwiseGeoGameEntryPoint implements EntryPoint {

	public interface ControlImageBundle extends ImageBundle {
		@Resource("minus2.png")
		AbstractImagePrototype minus();

		@Resource("center2.png")
		AbstractImagePrototype center();

		@Resource("plus2.png")
		AbstractImagePrototype plus();
	}

	private static class ImageZoomControl extends CustomControl {
		public ImageZoomControl() {
			super(new ControlPosition(ControlAnchor.TOP_RIGHT, 7, 7));
		}

		@Override
		protected Widget initialize(final MapWidget map) {
			ControlImageBundle imgBundle = GWT.create(ControlImageBundle.class);
			Image centerImage = imgBundle.center().createImage();
			Image zoomInImage = imgBundle.plus().createImage();
			Image zoomOutImage = imgBundle.minus().createImage();

			centerImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if(GameInfo.getInstance().getPlayer()!=null) {
						//		        		map.setCenter(MapUtil.toLatLng(GameInformation.getInstance().getPlayer().getCurrentLocation().getMapPosition()));
						map.setCenter(LatLng.newInstance(GameInfo.getInstance().getPlayer().getCurrentLocation().getLatitude(), GameInfo.getInstance().getPlayer().getCurrentLocation().getLongitude()));
						//		        		map.setZoomLevel(9);
					}
				}
			});
			zoomInImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					map.zoomIn();
				}
			});
			zoomOutImage.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					map.zoomOut();
				}
			});

			Grid container = new Grid(4, 1);
			container.setWidget(0, 0, centerImage);
			container.setWidget(2, 0, zoomInImage);
			container.setWidget(3, 0, zoomOutImage);

			return container;
		}

		@Override
		public boolean isSelectable() {
			return false;
		}
	}

	private LatLng startLocation = LatLng.newInstance(26.771974,-4.523621);
	//	private LatLngBounds outside = LatLngBounds.newInstance(
	//			LatLng.newInstance(25.771974,-5.523621),
	//			LatLng.newInstance(27.771974,-3.523621));

	public static String titleString = "Geogame";
	public static String footerString = "Carnegie Mellon, Department of Psychology and Robotics Institute (C) 2009";

	/**
	 * Creates a new instance of MainEntryPoint
	 */
	public StepwiseGeoGameEntryPoint() { }

	/**
	 * The entry point method, called automatically by loading a module
	 * that declares an implementing class as an entry-point
	 */

	public void onModuleLoad() {
		
		// Define widget elements
		final DockPanel dock = new DockPanel();
		dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
		dock.setBorderWidth(3);

		final Label titleLabel = new Label(titleString);
		final Label footerLabel = new Label(footerString);

		final TreasurePanel treasurePanel = new TreasurePanel();
		final LocationInfoPanel locationPanel = new LocationInfoPanel();
		final MapWidget map = new MapWidget(startLocation, 1);
		final LoginPanel loginPanel = new LoginPanel();
		final CommsPanel commsPanel = new CommsPanel();
		final AcceptPanel acceptPanel = new AcceptPanel();
		
		loginPanel.setWidth("200px");
		loginPanel.setHeight("600px");

		locationPanel.setWidth("150px");
		//		locationPanel.setHeight("800px");

		map.setSize("800px", "380px");
		map.setGoogleBarEnabled(false);
		map.setCurrentMapType(MapType.getSatelliteMap());
		map.setScrollWheelZoomEnabled(true);
		map.setPinchToZoom(true);
		
		final String refererURL = Document.get().getReferrer();
		

		ImageZoomControl izm = new ImageZoomControl();
		izm.initialize(map);
		map.addControl(izm);

		//		map.addMapMoveHandler(new MapMoveHandler() {
		//
		//			public void onMove(MapMoveEvent event) {
		//				LatLngBounds rect = map.getBounds();
		//				LatLng rectSize = rect.toSpan();
		//
		//				if (rect.getNorthEast().getLatitude() >= outside.getNorthEast().getLatitude()) {
		//					map.setCenter(LatLng.newInstance(
		//							outside.getNorthEast().getLatitude() - rectSize.getLatitude()/2,
		//							map.getCenter().getLongitude()
		//							));
		//				} else if (rect.getNorthEast().getLongitude() >= outside.getNorthEast().getLongitude()) {
		//					map.setCenter(LatLng.newInstance(
		//							map.getCenter().getLatitude(),
		//							outside.getNorthEast().getLongitude() - rectSize.getLongitude()/2
		//							));
		//				} else if (rect.getSouthWest().getLatitude() <= outside.getSouthWest().getLatitude()) {
		//					map.setCenter(LatLng.newInstance(
		//							outside.getSouthWest().getLatitude() + rectSize.getLatitude()/2,
		//							map.getCenter().getLongitude()
		//							));
		//				} else if (rect.getSouthWest().getLongitude() <= outside.getSouthWest().getLongitude()) {
		//					map.setCenter(LatLng.newInstance(
		//							map.getCenter().getLatitude(),
		//							outside.getSouthWest().getLongitude() + rectSize.getLongitude()/2
		//							));
		//				}
		//			}
		//		});

		//		map.addMapZoomEndHandler(new MapZoomEndHandler() {
		//
		//			public void onZoomEnd(MapZoomEndEvent event) {
		//				if (map.getZoomLevel() <= map.getBoundsZoomLevel(outside)) {
		//					map.setZoomLevel(map.getBoundsZoomLevel(outside) + 1);
		//				}
		//			}
		//		});
		//
		map.addMapZoomEndHandler(new MapZoomEndHandler() {
			public void onZoomEnd(MapZoomEndEvent event) {
				int minZoomLevel = 7;
				int maxZoomLevel = 10;

				if (map.getZoomLevel()>1 && map.getZoomLevel() < minZoomLevel) {
					map.setZoomLevel(minZoomLevel);
				} else if (map.getZoomLevel()>1 && map.getZoomLevel() > maxZoomLevel) {
					map.setZoomLevel(maxZoomLevel);
				}
				GameInfo.getInstance().refreshAll(false);
			}
		});

		map.addMapClickHandler(new MapClickHandler() {
			public void onClick(MapClickEvent event) {
				if(event.getOverlay()!=null) {
					return;
					//					//Then the click was over an overlay
					//					if(event.getOverlay().getClass()==GGLocationOverlay.class) {
					//						//Then it is a location :D
					//						GGLocationOverlay locationOverlay = (GGLocationOverlay)event.getOverlay();
					//						LocationDTO location = locationOverlay.getLocation();
					//						if(WindowInformation.locationPanel.isCreateMode() && WindowInformation.locationPanel.isRoadMode()) {
					//							//Then we should handle it in development mode (to start/end a road)
					//							DevUtil.handleDevLocationClick(location);
					//						} else {
					//							//Then we should try to move to that location
					//							AsyncCallback<ActionResult> callback = new AsyncCallback<ActionResult>() {
					//								public void onFailure(Throwable caught) {
					//									Window.alert("Move failed - " + caught.getMessage());
					//								}
					//								
					//								public void onSuccess(ActionResult result) {
					//									if(result.isSuccess()) {
					//										Window.alert(result.getMessage());
					//										GameInformation.getInstance().updateInformation(result.getPlayer(), result.getMapInformation());
					//										MapUtil.refreshMap(false);
					//										WindowInformation.locationPanel.refresh();
					//										WindowInformation.treasurePanel.refresh();
					//									}
					//								}
					//							};
					//							GameServices.gameService.moveToLocation(GameInformation.getInstance().getPlayer(), location.getId(), callback);
					//						}
					//						return;
					//					}
				} else {
					//Then it is a click on any point on the map
					if(WindowInformation.locationPanel.isCreateMode()) {
						DevUtil.handleDevMapClick(event.getLatLng());
					}
				}
			}
		});

		dock.add(titleLabel, DockPanel.NORTH);
		dock.add(footerLabel, DockPanel.SOUTH);
		dock.add(loginPanel, DockPanel.EAST);
		dock.add(map, DockPanel.CENTER);

		RootPanel.get().add(dock);

		WindowInformation.dockPanel = dock;
		WindowInformation.loginPanel = loginPanel;
		WindowInformation.treasurePanel = treasurePanel;
		WindowInformation.locationPanel = locationPanel;
		WindowInformation.commsPanel = commsPanel;
		WindowInformation.acceptPanel = acceptPanel;
		WindowInformation.titleLabel = titleLabel;
		WindowInformation.footerLabel = footerLabel;
		WindowInformation.mapWidget = map;
		WindowInformation.refererURL = refererURL;
		
		String AMTVisitor = Window.Location.getParameter("AMTVisitor");
		
		if (AMTVisitor == "true") {
			WindowInformation.AMTVisitor = true;
		}
		
		
		//		WindowInformation.locationInformationWindow = new LocationInformationWindow();
		//		WindowInformation.tradeWindow = new TradeWindow();
		//		WindowInformation.locationInformationWindow.setVisible(true);
		//		WindowInformation.tradeWindow.setVisible(true);
		
		WindowInformation.loginPanel.select();
		WindowInformation.loginPanel.maybeLoginThroughCookies();
	}
}