package edu.cmu.cs.cimds.geogame.client.ui.map;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.cmu.cs.cimds.geogame.client.GameInfo;
import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;
import edu.cmu.cs.cimds.geogame.client.ui.InfoWindow;
import edu.cmu.cs.cimds.geogame.client.ui.WindowInformation;

public class DevUtil {

	public static int locationCounter = 1;
	public static List<LatLng> roadPoints = new ArrayList<LatLng>();
	public static long location1ID = -1;
	private static int roadNum = 10;

	public static void handleDevLocationClick(LocationDTO location) {
		if(location1ID==-1) {
			location1ID = location.getId();
		} else {
			String roadPointsString = MapUtil.roadPointsToString(roadPoints);
			GameServices.devService.createRoad(GameInfo.getInstance().getPlayer(), "Road" + roadNum++, location1ID, location.getId(), roadPointsString, new AsyncCallback<Void>() {
				public void onFailure(Throwable caught) {
					//Window.alert("Failure because: " + caught.getMessage());
					InfoWindow alert = new InfoWindow(200, 100, "Failure because: " + caught.getMessage());
					alert.center();
				}
				
				public void onSuccess(Void result) {
//					Window.alert("Success");
					//TODO: Keep the auto-refresh when creating roads?
					//GameInformation.getInstance().refreshAll(true);
				}
			});
			location1ID = -1;
			roadPoints.clear();
			//Window.alert("Location 2 added - Road sent for creation");
		}
	}
	
	public static void resetDevState() {
		locationCounter = 1;
		roadPoints.clear();
		location1ID = -1;
		roadNum = 10;		
	}
	
	public static void handleDevMapClick(LatLng position) {
		if(WindowInformation.locationPanel.isRoadMode()) {
			if(location1ID!=-1 && position!=null) {
				roadPoints.add(position);
				//Window.alert("Road point added");
			}
		} else {
			String locationName = Window.prompt("Name for this location:","NULL" + locationCounter++);
			if(locationName!=null) {
				GameServices.devService.createLocation(GameInfo.getInstance().getPlayer(), locationName, position.getLatitude(), position.getLongitude(), new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
					}
					public void onSuccess(Void result) {
						//TODO: Keep the auto-refresh when creating locations?
						//GameInformation.getInstance().refreshAll(true);
					}
				});
			}
		}
	}
}