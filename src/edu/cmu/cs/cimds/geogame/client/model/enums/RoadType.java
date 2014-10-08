package edu.cmu.cs.cimds.geogame.client.model.enums;

public enum RoadType {
	HIGHWAY ("Highway"),
	GOOD ("Good"),
	BAD ("Bad");
	
	private String descriptor;
	private RoadType(String descriptor) {
		this.descriptor = descriptor;
	}
	
	@Override
	public String toString() {
		return this.descriptor;
	}
}