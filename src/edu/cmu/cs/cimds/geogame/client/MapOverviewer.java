package edu.cmu.cs.cimds.geogame.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtwidgets.client.util.SimpleDateFormat;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.ControlAnchor;
import com.google.gwt.maps.client.control.ControlPosition;
import com.google.gwt.maps.client.control.Control.CustomControl;
import com.google.gwt.maps.client.event.MapZoomEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.GroundOverlay;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.cmu.cs.cimds.geogame.client.model.dto.GameStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.RoadDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;
import edu.cmu.cs.cimds.geogame.client.ui.ChatPanel;
import edu.cmu.cs.cimds.geogame.client.ui.map.MapUtil;

public class MapOverviewer implements EntryPoint {

	private static final int REFRESH_PERIOD = 1000;

	private static final double locationLatitudeSize = .08;
	private static final double locationLongitudeSize = locationLatitudeSize*3.4;

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

//			centerImage.addClickHandler(new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					if(GameInfo.getInstance().getPlayer()!=null) {
//						//		        		map.setCenter(MapUtil.toLatLng(GameInformation.getInstance().getPlayer().getCurrentLocation().getMapPosition()));
//						map.setCenter(LatLng.newInstance(GameInfo.getInstance().getPlayer().getCurrentLocation().getLatitude(), GameInfo.getInstance().getPlayer().getCurrentLocation().getLongitude()));
//						//		        		map.setZoomLevel(9);
//					}
//				}
//			});
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

	public static String titleString = "Geogame Overviewer";
	public static String footerString = "Carnegie Mellon, Department of Psychology and Robotics Institute (C) 2009";

	private MapWidget map;
	private ChatPanel logPanel;

	private Map<UserDTO,Marker> playersMap;
	private Map<LocationDTO,GroundOverlay> locationsMap = new HashMap<LocationDTO, GroundOverlay>();
	private Map<LocationDTO,Marker> locationLabelsMap = new HashMap<LocationDTO, Marker>();
	private Map<RoadDTO,Polyline> roadsMap = new HashMap<RoadDTO, Polyline>();
	private Timer refreshTimer;
	private MapInformation mapInformation;
	
	private CheckBox showTownNamesCheckBox = new CheckBox("Show Town Names");
	
	public boolean isShowTownLabels() {
		return showTownNamesCheckBox.getValue();
	}

	/**
	 * Creates a new instance of MainEntryPoint
	 */
	public MapOverviewer() { }

	/**
	 * The entry point method, called automatically by loading a module
	 * that declares an implementing class as an entry-point
	 */

	public void onModuleLoad() {
		// Define widget elements
		final HorizontalPanel mainPanel = new HorizontalPanel();

		final Label titleLabel = new Label(titleString);
		final Label footerLabel = new Label(footerString);

		this.map = new MapWidget(startLocation, 7);

		this.map.setSize("800px", "380px");
		this.map.setGoogleBarEnabled(false);
		this.map.setCurrentMapType(MapType.getSatelliteMap());
		this.map.setScrollWheelZoomEnabled(true);
		this.map.setPinchToZoom(true);
		mainPanel.add(this.map);

		ImageZoomControl izm = new ImageZoomControl();
		izm.initialize(this.map);
		this.map.addControl(izm);

		this.map.addMapZoomEndHandler(new MapZoomEndHandler() {
			@Override
			public void onZoomEnd(MapZoomEndEvent event) {
//				MapOverviewer.this.resizeLocationLabels();
			}
		});
		this.logPanel = new ChatPanel();
		mainPanel.add(this.logPanel);
		
		this.showTownNamesCheckBox.setValue(true);
		this.showTownNamesCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setLocationLabelsVisible(MapOverviewer.this.showTownNamesCheckBox.getValue());
			}
		});
		mainPanel.add(this.showTownNamesCheckBox);

		RootPanel.get().add(titleLabel);
		RootPanel.get().add(mainPanel);
		RootPanel.get().add(footerLabel);

		visualizeMap();
	}

	private void visualizeMap() {
		refreshMap();
	}

	private void refreshLocationsAndRoads() {
		this.refreshLocations();
		this.refreshRoads();
	}

//	private void resizeLocationLabels() {
//		LatLngBounds bounds = this.map.getBounds();
//		
//		double mapLatitudeRange = bounds.getNorthEast().getLatitude() - bounds.getSouthWest().getLatitude();
//		double mapHeight = this.map.getSize().getHeight();
//
//		for(Map.Entry<LocationDTO, Marker> entry : this.locationLabelsMap.entrySet()) {
//			LocationDTO location = entry.getKey();
//			Marker locationLabelMarker = entry.getValue();
//			map.removeOverlay(locationLabelMarker);
//			LatLng newPosition = LatLng.newInstance(location.getLatitude() + 12*mapLatitudeRange/mapHeight, location.getLongitude());
////			locationLabelMarker.setLatLng();
//			MarkerOptions mo = MarkerOptions.newInstance(locationLabelMarker.getIcon());
//			Marker newLocationLabelMarker = new Marker(newPosition, mo);
//			map.addOverlay(newLocationLabelMarker);
//		}
//		this.refreshLocations();
//		this.refreshRoads();
//	}

	private void setLocationLabelsVisible(boolean visible) {
		for(Map.Entry<LocationDTO, Marker> entry : this.locationLabelsMap.entrySet()) {
			entry.getValue().setVisible(visible);
		}
	}

	private void refreshLocations() {
		for(LocationDTO location : this.mapInformation.locations) {
			this.drawLocation(location);
		}
	}
	
	private void refreshRoads() {
		for(RoadDTO road : this.mapInformation.roads) {
			this.drawRoad(road);
		}
	}
	
// 	private void clearMap() {
//		this.map.clearOverlays();
//		if(deletePlayerMarkers) {
//			this.playersMap.clear();
//		} else {
//			for(Map.Entry<UserDTO, Marker> entry : this.playersMap.entrySet()) {
//				this.map.addOverlay(entry.getValue());
//			}
//		}
//	}
	
	private void drawLocation(final LocationDTO location) {
		if(location.getIconFilename()==null) {
			return;
		}
		LatLngBounds bounds = this.map.getBounds();
		
		double mapLongitudeRange = bounds.getNorthEast().getLongitude() - bounds.getSouthWest().getLongitude();
		double mapLatitudeRange = bounds.getNorthEast().getLatitude() - bounds.getSouthWest().getLatitude();
		double mapWidth = this.map.getSize().getWidth();
		double mapHeight = this.map.getSize().getHeight();
		int locationPixelsHeight = (int)(locationLatitudeSize*mapHeight/mapLatitudeRange);
		int locationPixelsWidth = (int)(locationLongitudeSize*mapWidth/mapLongitudeRange);
		
		Icon icon = Icon.newInstance(location.getIconFilename());
		icon.setIconSize(Size.newInstance(locationPixelsWidth, locationPixelsHeight));
		MarkerOptions markerOptions = MarkerOptions.newInstance(icon);
		markerOptions.setTitle(location.getName());
		
		LatLng topLeftLocationCorner = LatLng.newInstance(location.getLatitude(), location.getLongitude()/* - (locationLongitudeSize/2)*/);
//		Marker locationMarker = new Marker(topLeftLocationCorner, markerOptions);

		LatLng southWestPoint = LatLng.newInstance(location.getLatitude() - locationLatitudeSize, location.getLongitude());
		LatLng northEastPoint = LatLng.newInstance(location.getLatitude()						, location.getLongitude() + locationLongitudeSize);
		LatLngBounds overlayBounds = LatLngBounds.newInstance(southWestPoint, northEastPoint);
		GroundOverlay locationMarker = new GroundOverlay(location.getIconFilename(), overlayBounds);
		
		this.map.addOverlay(locationMarker);
		this.locationsMap.put(location, locationMarker);
		
//		int zoomOffset = 9-this.map.getZoomLevel();
//		double zoomFactor = Math.pow(2, zoomOffset);

		LatLng locationLabelPosition = LatLng.newInstance(topLeftLocationCorner.getLatitude()/* + 12*mapLatitudeRange/mapHeight*//*zoomFactor*locationLatitudeSize/3*/, topLeftLocationCorner.getLongitude());
		Icon locationLabelIcon = Icon.newInstance("text.png.text2img?text=" + location.getName() + "&backColor=ff8800");
		Point labelAnchor = Point.newInstance(0,16);
		locationLabelIcon.setIconAnchor(labelAnchor);
		MarkerOptions labelMarkerOptions = MarkerOptions.newInstance(locationLabelIcon);
		Marker locationLabelMarker = new Marker(locationLabelPosition, labelMarkerOptions);

		this.map.addOverlay(locationLabelMarker);
		this.locationLabelsMap.put(location, locationLabelMarker);
	}
	
	private void drawRoad(final RoadDTO road) {
		List<LatLng> roadPoints = MapUtil.stringToRoadPoints(road.getRoadPointsString());
		LatLng[] roadPointsArray = new LatLng[roadPoints.size() + 2];

		roadPointsArray[0] = LatLng.newInstance(road.getLocation1().getLatitude(), road.getLocation1().getLongitude());
		roadPointsArray[roadPoints.size()+1] = LatLng.newInstance(road.getLocation2().getLatitude(), road.getLocation2().getLongitude());
		for(int i=0;i<roadPoints.size();i++) {
			roadPointsArray[i+1] = roadPoints.get(i);
		}
		Polyline roadPolyline = new Polyline(roadPointsArray);
		this.map.addOverlay(roadPolyline);
		this.roadsMap.put(road, roadPolyline);
	}

	private void refreshMap() {
		AsyncCallback<GameStruct> callback = new AsyncCallback<GameStruct>() {
			@Override
			public void onFailure(Throwable caught) {
				logPanel.addMessage("Failure getting game information - " + caught.getMessage());
			}

			@Override
			public void onSuccess(GameStruct result) {
				MapOverviewer.this.mapInformation = result.mapInformation;
				refreshLocationsAndRoads();
			}
		};
		GameServices.gameService.getGameInformationNew(null, callback);

		refreshTimer = new Timer() {
			@Override
			public void run() {

				AsyncCallback<List<UserDTO>> callback = new AsyncCallback<List<UserDTO>>() {
					@Override
					public void onFailure(Throwable caught) {
						log("Poll failed - " + caught.getMessage());
					}

					@Override
					public void onSuccess(List<UserDTO> result) {
						log("Poll successful - " + result.size() + " players obtained");
						if(playersMap==null) {
							createMarkers(result);
							map.panTo(LatLng.newInstance(result.get(0).getLatitude(), result.get(0).getLongitude()));
						} else {
							updateMarkers(result);
						}
					}
				};
				log("Polling allPlayers");
				GameServices.gameService.getAllPlayers(callback);
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_PERIOD);
	}

	private void createMarkers(List<UserDTO> players) {
		playersMap = new HashMap<UserDTO, Marker>();
		for(UserDTO player : players) {
			MarkerOptions mo = MarkerOptions.newInstance(Icon.newInstance(player.getIconFilename()));

			Marker playerMarker = new Marker(LatLng.newInstance(player.getLatitude(), player.getLongitude()), mo);
			map.addOverlay(playerMarker);
			playersMap.put(player, playerMarker);
		}
	}

	private void updateMarkers(List<UserDTO> players) {
		for(UserDTO player : players) {
			if(playersMap.containsKey(player)) {
				playersMap.get(player).setLatLng(LatLng.newInstance(player.getLatitude(), player.getLongitude()));
			}
		}
	}
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	private void log(String message) {
		logPanel.addMessage(dateFormat.format(new Date()) + " -  " + message);
	}
}