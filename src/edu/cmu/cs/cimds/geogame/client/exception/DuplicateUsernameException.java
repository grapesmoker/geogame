package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;


public class DuplicateUsernameException extends GeoGameException implements Serializable {

	private static final long serialVersionUID = 7736186009132970224L;

	private String username;
	
	public DuplicateUsernameException() {
		super("User already exists!");
	}
	
	public DuplicateUsernameException(String username) {
		super("User " + username + " already exists");	
		this.username = username;
	}

	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
}