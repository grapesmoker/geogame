/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO.GGLatLng;
import edu.cmu.cs.cimds.geogame.client.model.enums.RoadType;


/**
 *
 * @author ajuarez
 */
@Entity
@Table(name="road")
public class Road extends PersistentEntity {

	private static final long serialVersionUID = 631374156908146308L;
	
	private RoadType roadType;
	private String name;
	private Location location1;
	private Location location2;
	private String roadPointsString;
	private String code;
	private double danger;
	private double length;
	private int travelDuration;

	private boolean penalty;
	
	@Column(name="road_type")
	public RoadType getRoadType() { return roadType; }
	public void setRoadType(RoadType roadType) { this.roadType = roadType; }

	@Column(name="name", nullable=false)
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="location1_id", nullable=false)
	public Location getLocation1() { return location1; }
	public void setLocation1(Location location1) { this.location1 = location1; }

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="location2_id", nullable=false)
	public Location getLocation2() { return location2; }
	public void setLocation2(Location location2) { this.location2 = location2; }

	@Column(name="road_points_string")
	public String getRoadPointsString() { return roadPointsString; }
	public void setRoadPointsString(String roadPointsString) { this.roadPointsString = roadPointsString; }

	@Column(name="code")
	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }

	@Column(name="danger")
	public double getDanger() { return danger; }
	public void setDanger(double danger) { this.danger = danger; }
	
	@Column(name="length")
	public double getLength() { return length; }
	public void setLength(double length) { this.length = length; }

	@Column(name="travel_duration")
	public int getTravelDuration() { return travelDuration; }
	public void setTravelDuration(int travelDuration) { this.travelDuration = travelDuration; }

	@Column(name="penalty")
	public boolean isPenalty() { return penalty; }
	public void setPenalty(boolean penalty) { this.penalty = penalty; }

//	public List<LatLng> getRoadPoints() { return this.roadPoints; }
//	public void setRoadPoints(List<LatLng> roadPoints) {
//		this.roadPoints = roadPoints;
//		StringBuilder roadPointsString = new StringBuilder();
//		for(LatLng roadPoint : this.roadPoints) {
//			roadPointsString.append(roadPoint.getLatitude() + "," + roadPoint.getLongitude() + ";");
//		}
//		this.roadPointsString = roadPointsString.substring(0,roadPointsString.length()-1);
//	}
	
	@Transient
	public double calculateLength() {
		List<GGLatLng> roadPoints = stringToRoadPoints(this.getRoadPointsString());
		roadPoints.add(0, new GGLatLng(this.getLocation1().getLatitude(),this.getLocation1().getLongitude()));
		roadPoints.add(new GGLatLng(this.getLocation2().getLatitude(),this.getLocation2().getLongitude()));

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
	
	@Transient
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