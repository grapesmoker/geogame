package edu.cmu.cs.cimds.geogame.client.model.enums;

public enum UserStatus {
	INACTIVE ("Inactive"),
	LATENT ("Active"),
	MOVING ("Moving"),
	DELETED ("Deleted");
	
	private String descriptor;
	private UserStatus(String descriptor) {
		this.descriptor = descriptor;
	}
	
	@Override
	public String toString() {
		return this.descriptor;
	}
}