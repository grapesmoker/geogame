package edu.cmu.cs.cimds.geogame.client.ui.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gwtwidgets.client.util.SimpleDateFormat;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.cmu.cs.cimds.geogame.client.GameInfo;
import edu.cmu.cs.cimds.geogame.client.MoveResult;
import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.RoadDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.RoadMovementDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;
import edu.cmu.cs.cimds.geogame.client.ui.WindowInformation;

public class MapUtil {

	private static final String playerIconFilename = "person.png";
	
	private static final double locationLatitudeSize = .1;
	private static final double locationLongitudeSize = .1;
	
	private static Marker playerMarker;
	
//	private static Marker testTextMarker;
	
	public static void refreshMap() {
		clearMap();
		//Refresh with information available on the client
		MapWidget map = WindowInformation.mapWidget;
		
		for(LocationDTO location: GameInfo.getInstance().getMapLocations()) {
			drawLocation(location);
		}
		for(RoadDTO road: GameInfo.getInstance().getMapRoads()) {
			drawRoad(road);
		}

		UserDTO player = GameInfo.getInstance().getPlayer();
//		LocationDTO currentLocation = player.getCurrentLocation();
//		if(currentLocation==null) {
//			return;
//		}

		if(player.getIconFilename()==null || "".equals(player.getIconFilename())) {
			player.setIconFilename(playerIconFilename);
		}
		MarkerOptions markerOptions = MarkerOptions.newInstance(Icon.newInstance(player.getIconFilename()));
		markerOptions.setTitle(player.getUsername());

		LatLng mapPosition = LatLng.newInstance(player.getLatitude(), player.getLongitude());
		playerMarker = new Marker(mapPosition, markerOptions);
		map.addOverlay(playerMarker);
		int minZoomLevel = 7;
		int maxZoomLevel = 10;
		if (map.getZoomLevel() < minZoomLevel) {
		} else if (map.getZoomLevel() > maxZoomLevel) {
			map.setZoomLevel(9);
		}
		
		map.panTo(mapPosition);
	}

	public static void clearMap() {
		MapWidget map = WindowInformation.mapWidget;
		map.clearOverlays();
	}
	
	public static void setStandbyMap() {
		MapWidget map = WindowInformation.mapWidget;
		map.setZoomLevel(1);
	}

	public static void drawLocation(final LocationDTO location) {
		MapWidget map = WindowInformation.mapWidget;
		if(location.getIconFilename()==null) {
			return;
		}
		LatLngBounds bounds = map.getBounds();
		
//		double mapLatitudeRange = bounds.getNorthEast().getLatitude() - bounds.getSouthWest().getLatitude();
		double mapLongitudeRange = bounds.getNorthEast().getLongitude() - bounds.getSouthWest().getLongitude();
//		double mapHeight = map.getSize().getHeight();
		double mapWidth = map.getSize().getWidth();
		
//		//.05, .05
		int locationPixelsHeight = (int)(locationLatitudeSize*mapWidth/mapLongitudeRange);
		int locationPixelsWidth = (int)(locationLongitudeSize*mapWidth/mapLongitudeRange);
		
		Icon icon = Icon.newInstance(location.getIconFilename());
		icon.setIconSize(Size.newInstance(locationPixelsWidth, locationPixelsHeight));
		icon.setIconAnchor(Point.newInstance(0, 0));
		MarkerOptions markerOptions = MarkerOptions.newInstance(icon);
		markerOptions.setTitle(location.getName());
		//LatLng locationSouthWest = LatLng.newInstance(location.getLatitude()-(locationLatitudeSize/2), location.getLongitude()-(locationLongitudeSize/2));
		//LatLng locationNorthEast = LatLng.newInstance(location.getLatitude()+(locationLatitudeSize/2), location.getLongitude()+(locationLongitudeSize/2));

		//LatLngBounds locationBounds = LatLngBounds.newInstance(locationSouthWest, locationNorthEast);
		//GGLocationOverlay locationOverlay = new GGLocationOverlay(location.getIconFilename(), locationBounds);
		//locationOverlay.setLocation(location);
		//locationOverlay.
//		LatLng topLeftLocationCorner = LatLng.newInstance(location.getMapPosition().getLatitude(), location.getMapPosition().getLongitude() - (locationLongitudeSize/2));
		LatLng topLeftLocationCorner = LatLng.newInstance(location.getLatitude(), location.getLongitude() - (locationLongitudeSize/2));
		Marker locationMarker = new Marker(topLeftLocationCorner, markerOptions);
		
		//Marker locationMarker;
		//locationOverlay.
		locationMarker.addMarkerClickHandler(new MarkerClickHandler() {
			public void onClick(MarkerClickEvent event) {
				if(WindowInformation.inAjaxCall || GameInfo.getInstance().isPlayerMoving()) {
					return;
				}
				if(WindowInformation.locationPanel.isCreateMode() && WindowInformation.locationPanel.isRoadMode()) {
					DevUtil.handleDevLocationClick(location);
//					if(MainEntryPoint.location1ID==-1) {
//						MainEntryPoint.location1ID = location.getId();
//						Window.alert("Location 1 added - Add road points");
//					} else {
//						String roadPointsString = roadPointsToString(MainEntryPoint.roadPoints);
//						GameServices.devService.createRoad(GameInformation.getInstance().getPlayer(), "Road" + roadNum++, MainEntryPoint.location1ID, location.getId(), roadPointsString, new AsyncCallback<Void>() {
//							public void onFailure(Throwable caught) {
//								Window.alert("Failure because: " + caught.getMessage());
//							}
//							
//							public void onSuccess(Void result) {
////								Window.alert("Success");
//								MapUtil.refreshMap(true);
//							}
//						});
//						MainEntryPoint.location1ID = -1;
//						MainEntryPoint.roadPoints.clear();
//						Window.alert("Location 2 added - Road sent for creation");
//					}
				} else {
//					GameInfo.getInstance().log("Traveling " + GameInfo.getInstance().getLocation().getName() + " -> " + location.getName() + "...");
//					if(location.getId()==GameInformation.getInstance().getLocation().getId()) {
//						LocationInfoPanel.openLocationDialog(location);
//					}
					AsyncCallback<MoveResult> callback = new AsyncCallback<MoveResult>() {
						public void onFailure(Throwable caught) {
							WindowInformation.inAjaxCall = false;
							//Window.alert("Move failed - " + caught.getMessage());
							GameInfo.getInstance().log("Move failed! " + caught.getMessage());
							GameInfo.getInstance().setNeedsRefresh(true);
						}
						
						public void onSuccess(MoveResult result) {
							WindowInformation.inAjaxCall = false;
							if(result.isSuccess()) {
//								GameInfo.getInstance().log(/*result.getCounter() + ": " + */result.getMessage());
								GameInfo.getInstance().log(new SimpleDateFormat("HH:mm:ss").format(result.getTimestamp()) + " - Moving from " + result.getSource().getName() + " to " + result.getDestination().getName() + ". Duration: " + Math.rint(result.getDuration()/100)/10 + " seconds");
								GameInfo.getInstance().updateInformation(result.getGameInformation());
								MapUtil.updatePosition();
//								WindowInformation.locationPanel.refresh();
//								WindowInformation.treasurePanel.refresh();

								
								//TODO:RM
//								final RoadMovementDTO roadMovement = GameInfo.getInstance().getPlayer().getCurrentRoadMovement();
//								final int periodMillisRefresh = 200;
//								
//								if(GameInfo.getInstance().moveTimer!=null) {
//									GameInfo.getInstance().moveTimer.cancel();
//								}
//								
//								GameInfo.getInstance().moveTimer = new Timer() {
//									@Override
//									public void run() {
//										if(!GameInfo.getInstance().isPlayerMoving()) {
//											this.cancel();
//											return;
//										}
//										UserDTO player = GameInfo.getInstance().getPlayer();
//										double oldLat = player.getLatitude();
//										double oldLong = player.getLongitude();
//										
//										double hypot = roadMovement.getSpeed() * (periodMillisRefresh/1000);
//										double diffLat = Math.asin(roadMovement.getSlope())*hypot;
//										double diffLong = Math.acos(roadMovement.getSlope())*hypot;
//										MapUtil.updatePosition(oldLat + diffLat, oldLong + diffLong);
////										MapUtil.updatePosition(player.getCurrentLocation());
//									}
//								};
//TODO: Timer is deactivated...
//								GameInfo.getInstance().moveTimer.scheduleRepeating(periodMillisRefresh);
							}
						}
					};
					WindowInformation.inAjaxCall = true;
					GameServices.gameService.moveToLocationNew(GameInfo.getInstance().getPlayer(), location.getId(), callback);
					GameInfo.getInstance().setNeedsRefresh(true);
					
					//Timeout for the call - if it takes more than "10 seconds", release the lock on AJAX Calls.
					new Timer() {
						@Override
						public void run() {
							WindowInformation.inAjaxCall = false;
						}
					}.schedule(WindowInformation.AJAX_TIMEOUT);
				}
			}
		});
		map.addOverlay(locationMarker);
		if(WindowInformation.locationPanel.isShowTownLabels()) {
			int zoomOffset = 9-WindowInformation.mapWidget.getZoomLevel();
			double zoomFactor = Math.pow(2, zoomOffset);
			LatLng locationLabelPosition = LatLng.newInstance(topLeftLocationCorner.getLatitude() + zoomFactor*locationLatitudeSize/6, topLeftLocationCorner.getLongitude());
			
			String labelUrl = "text.png.text2img?text=" + location.getName() + "&backColor=ff8800";
			Icon labelIcon = Icon.newInstance(labelUrl);
			labelIcon.setIconAnchor(Point.newInstance(0, 20));
			
			MarkerOptions labelMarkerOptions = MarkerOptions.newInstance(labelIcon);
			Marker locationLabelMarker = new Marker(locationLabelPosition, labelMarkerOptions);
			locationLabelMarker.addMarkerClickHandler(new MarkerClickHandler() {
				@Override
				public void onClick(MarkerClickEvent event) {
					WindowInformation.commsPanel.appendToChatWindow(location.getName());
					WindowInformation.commsPanel.focusOnChat();
				}
			});

			map.addOverlay(locationLabelMarker);
		}
	}

	public static void drawRoad(final RoadDTO road) {
		final MapWidget map = WindowInformation.mapWidget;
		List<LatLng> roadPoints = stringToRoadPoints(road.getRoadPointsString());
//		int numRoadPoints = road.getRoadPointsLatitudes().size();
//		LatLng[] roadPoints = new LatLng[numRoadPoints + 2];
//		roadPoints[0] = road.getLocation1().getPosition();
//		roadPoints[numRoadPoints + 1] = road.getLocation2().getPosition();
//		for(int i=1;i<numRoadPoints+1;i++) {
//			roadPoints[i] = LatLng.newInstance(road.getRoadPointsLatitudes().get(i),road.getRoadPointsLongitudes().get(i));
//		}
		LatLng[] roadPointsArray = new LatLng[roadPoints.size() + 2];
//		roadPointsArray[0] = MapUtil.toLatLng(road.getLocation1().getMapPosition());
//		roadPointsArray[roadPoints.size()+1] = MapUtil.toLatLng(road.getLocation2().getMapPosition());
		roadPointsArray[0] = LatLng.newInstance(road.getLocation1().getLatitude(), road.getLocation1().getLongitude());
		roadPointsArray[roadPoints.size()+1] = LatLng.newInstance(road.getLocation2().getLatitude(), road.getLocation2().getLongitude());
		for(int i=0;i<roadPoints.size();i++) {
			roadPointsArray[i+1] = roadPoints.get(i);
		}
		Polyline roadPolyline = new Polyline(roadPointsArray);

//		roadPolyline.addPolylineClickHandler(new PolylineClickHandler() {
//			@Override
//			public void onClick(PolylineClickEvent event) {
//				InfoWindow infoWindow = map.getInfoWindow();
//				InfoWindowContent content = new InfoWindowContent(road.getName());
//				infoWindow.open(event.getLatLng(), content);
//			}			

//			@Override
//			public void onMouseOver(PolylineMouseOverEvent event) {
//				InfoWindow infoWindow = map.getInfoWindow();
//				InfoWindowContent content = new InfoWindowContent(road.getCode());
//				infoWindow.open(event.get, content)
//			}			
//		});
		map.addOverlay(roadPolyline);

		if(WindowInformation.locationPanel.isShowRoadLabels()) {
			LatLng roadMidPoint = getRoadMidpoint(roadPointsArray);
			Marker roadLabelMarker = new Marker(roadMidPoint,MarkerOptions.newInstance(Icon.newInstance("text.png.text2img?text=" + road.getName() + "&backColor=ffff00")));
			
			roadLabelMarker.addMarkerClickHandler(new MarkerClickHandler() {
				@Override
				public void onClick(MarkerClickEvent event) {
					WindowInformation.commsPanel.appendToChatWindow(road.getName());
					WindowInformation.commsPanel.focusOnChat();
				}
			});
			map.addOverlay(roadLabelMarker);
		}
	}

	public static LatLng getRoadMidpoint(LatLng[] roadPoints) {
		double halfRoadLength = calculateRoadLength(roadPoints) / 2;
		
		double roadLength = 0;
		for(int i=0;i<roadPoints.length-1;i++) {
			double lineLength = calculateLineLength(roadPoints[i], roadPoints[i+1]);
			if(roadLength + lineLength >= halfRoadLength) {
				double remainingDistance = halfRoadLength - roadLength;
				double smallRemainingRatio = remainingDistance / lineLength;
				double latDiff = roadPoints[i+1].getLatitude()-roadPoints[i].getLatitude();
				double longDiff = roadPoints[i+1].getLongitude()-roadPoints[i].getLongitude();
				
				latDiff*=smallRemainingRatio;
				longDiff*=smallRemainingRatio;
				return LatLng.newInstance(roadPoints[i].getLatitude() + latDiff, roadPoints[i].getLongitude() + longDiff);
			}
			roadLength += lineLength;
		}
		return null;
	}
	
	private static double calculateRoadLength(LatLng[] roadPoints) {
		double roadLength = 0;
		for(int i=0;i<roadPoints.length-1;i++) {
			roadLength += calculateLineLength(roadPoints[i], roadPoints[i+1]);
		}
		return roadLength;
	}
	
	private static double calculateLineLength(LatLng source, LatLng destination) {
		double latDiff = destination.getLatitude()-source.getLatitude();
		double longDiff = destination.getLongitude()-source.getLongitude();
		return Math.sqrt(latDiff*latDiff+longDiff*longDiff);
	}
	
	public static String roadPointsToString(List<LatLng> roadPoints) {
		StringBuilder roadPointsString = new StringBuilder();
		for(LatLng roadPoint : roadPoints) {
			roadPointsString.append(roadPoint.getLatitude() + "," + roadPoint.getLongitude() + ";");
		}
		return roadPointsString.toString();
	}

	public static List<LatLng> stringToRoadPoints(String roadPointsString) {
		List<LatLng> roadPoints = new ArrayList<LatLng>();
		if(roadPointsString==null || "".equals(roadPointsString)) {
			return roadPoints;
		}
		String[] roadPointsStrings = roadPointsString.split(";");
		
		for(String roadPointString : roadPointsStrings) {
			String[] coordStrings = roadPointString.split(",");
			roadPoints.add(LatLng.newInstance(Double.valueOf(coordStrings[0]), Double.valueOf(coordStrings[1])));
		}
		return roadPoints;
	}

//	public static void updatePosition(LocationDTO currentLocation) {
//		playerMarker.setLatLng(LatLng.newInstance(currentLocation.getLatitude(), currentLocation.getLongitude()));
//	}

	public static void updatePosition() {
		UserDTO player = GameInfo.getInstance().getPlayer();
		playerMarker.setLatLng(LatLng.newInstance(player.getLatitude(),player.getLongitude()));
//		WindowInformation.treasurePanel.log("Player coords: " + commStruct.player.getLatitude() + "," + commStruct.player.getLongitude());
	}

	public static void updatePosition(double latitude, double longitude) {
		playerMarker.setLatLng(LatLng.newInstance(latitude,longitude));
//		WindowInformation.treasurePanel.log("Player coords: " + commStruct.player.getLatitude() + "," + commStruct.player.getLongitude());
	}

//	public static LatLng toLatLng(GGLatLng position) {
//		return LatLng.newInstance(position.getLatitude(), position.getLongitude());
//	}


	public static LatLng getRoadPosition(RoadMovementDTO roadMovement) {
		long timeLeft = roadMovement.getTimeLeft();
		long timeElapsed = roadMovement.getDuration() - timeLeft;
		
		LatLng roadPosition;
		if(timeLeft<=0) {
			//Warning, should never be here... right?
			if(roadMovement.isForward()) {
				roadPosition = toLatLng(roadMovement.getRoad().getLocation2());
			} else {
				roadPosition = toLatLng(roadMovement.getRoad().getLocation1());
			}
			return roadPosition;
		} else {
			List<LatLng> roadPoints = stringToRoadPoints(roadMovement.getRoad().getRoadPointsString());
			List<Double> distances = new ArrayList<Double>();
			double totalDistance = 0;
			for(int i=0;i<roadPoints.size()-1;i++) {
				LatLng roadPoint1 = roadPoints.get(i);
				LatLng roadPoint2 = roadPoints.get(i+1);
				double latDiff = roadPoint2.getLatitude()-roadPoint1.getLatitude();
				double longDiff = roadPoint2.getLongitude()-roadPoint1.getLongitude();
				
				distances.add(Math.sqrt(latDiff*latDiff+longDiff*longDiff));
				totalDistance += distances.get(i);
			}
			totalDistance *= (timeElapsed / roadMovement.getDuration());
			if(!roadMovement.isForward()) {
				Collections.reverse(roadPoints);
				Collections.reverse(distances);
			}
			double distance = 0;
			for(int i=0;i<distances.size();i++) {
				if(distance+distances.get(i)>=totalDistance) {
					double miniRatio = (distance+distances.get(i)-totalDistance)/distances.get(i);
					
					LatLng roadPoint1 = roadPoints.get(i);
					LatLng roadPoint2 = roadPoints.get(i+1);
					double latDiff = roadPoint2.getLatitude()-roadPoint1.getLatitude();
					double longDiff = roadPoint2.getLongitude()-roadPoint1.getLongitude();

					double newLat = roadPoint1.getLatitude() + (miniRatio*latDiff);
					double newLong = roadPoint1.getLatitude() + (miniRatio*longDiff);
					
					roadPosition = LatLng.newInstance(newLat,newLong);
					return roadPosition;
				}
				distance+=distances.get(i);
			}
			//It should never come here
			return null;
		}
	}
	
	private static LatLng toLatLng(LocationDTO location) {
		return LatLng.newInstance(location.getLatitude(), location.getLongitude());
	}
	
	public static void setZoomLevel(int zoomLevel) {
		WindowInformation.mapWidget.setZoomLevel(zoomLevel);
	}
}