package edu.cmu.cs.cimds.geogame.client.model.enums;

public enum LocationType {
	CITY ("City"),
	TOWN ("Town"),
	CASTLE ("Castle"),
	STATION ("Station"),
	INTERSECTION ("Intersection"),
	AIRPORT ("Airport");
	
	private String descriptor;
	private LocationType(String descriptor) {
		this.descriptor = descriptor;
	}
	
	@Override
	public String toString() {
		return this.descriptor;
	}
}