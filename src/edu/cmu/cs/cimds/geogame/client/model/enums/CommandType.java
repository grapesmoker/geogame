package edu.cmu.cs.cimds.geogame.client.model.enums;

public enum CommandType {

	SHOW_SYNONYMS("Show synonyms"); //0
	
	private String descriptor;
	
	private CommandType(String descriptor) {
		this.descriptor = descriptor;
	}
	
	@Override
	public String toString() {
		return this.descriptor;
	}
}
