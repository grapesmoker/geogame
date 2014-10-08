/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.cimds.geogame.client.model.db.Road;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO.GGLatLng;
import edu.cmu.cs.cimds.geogame.client.model.enums.RoadType;


/**
 *
 * @author ajuarez
 */
public class RoadDTO implements Serializable {

	private static final long serialVersionUID = -5814390492569149297L;
	
	public RoadDTO() {}
	
	public RoadDTO(Road road) {
		this.id = road.getId();
		this.name = road.getName();
		this.roadType = road.getRoadType();
		
		//Load the objects from the database
		road.getLocation1().getName();
		road.getLocation2().getName();
		this.location1 = new LocationDTO(road.getLocation1());
		this.location2 = new LocationDTO(road.getLocation2());
		this.roadPointsString = road.getRoadPointsString();
		this.setCode(road.getCode());
	}
	
	private long id;
	private String name;
	private RoadType roadType;
	private LocationDTO location1;
	private LocationDTO location2;
	private String roadPointsString;
	private String code;

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public RoadType getRoadType() { return roadType; }
	public void setRoadType(RoadType roadType) { this.roadType = roadType; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public LocationDTO getLocation1() { return location1; }
	public void setLocation1(LocationDTO location1) { this.location1 = location1; }

	public LocationDTO getLocation2() { return location2; }
	public void setLocation2(LocationDTO location2) { this.location2 = location2; }

	public String getRoadPointsString() { return roadPointsString; }
	public void setRoadPointsString(String roadPointsString) { this.roadPointsString = roadPointsString; }
	
	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }

	@Override
	public boolean equals(Object obj) {
		if(obj==null || obj.getClass()!=RoadDTO.class) {
			return false;
		}
		return this.id==((RoadDTO)obj).getId();
	}
	
	public double getLength() {
		List<GGLatLng> roadPoints = stringToRoadPoints(this.getRoadPointsString());
		roadPoints.add(0, new GGLatLng(this.getLocation1()));
		roadPoints.add(new GGLatLng(this.getLocation2()));

		List<Double> distances = new ArrayList<Double>();
		double totalDistance = 0;
		for(int i=0;i<roadPoints.size()-1;i++) {
			GGLatLng roadPoint1 = roadPoints.get(i);
			GGLatLng roadPoint2 = roadPoints.get(i+1);
			double latDiff = roadPoint2.getLatitude()-roadPoint1.getLatitude();
			double longDiff = roadPoint2.getLongitude()-roadPoint1.getLongitude();
			
			distances.add(Math.sqrt(latDiff*latDiff+longDiff*longDiff));
			totalDistance += distances.get(i);
		}
		return totalDistance;
	}
	
	public static List<GGLatLng> stringToRoadPoints(String roadPointsString) {
		List<GGLatLng> roadPoints = new ArrayList<GGLatLng>();
		if(roadPointsString==null || "".equals(roadPointsString)) {
			return roadPoints;
		}
		String[] roadPointsStrings = roadPointsString.split(";");
		
		for(String roadPointString : roadPointsStrings) {
			String[] coordStrings = roadPointString.split(",");
			roadPoints.add(new GGLatLng(Double.valueOf(coordStrings[0]), Double.valueOf(coordStrings[1])));
		}
		return roadPoints;
	}
}