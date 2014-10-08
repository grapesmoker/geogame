package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;

import edu.cmu.cs.cimds.geogame.client.model.db.Location;

public class InvalidMoveException extends GeoGameException implements Serializable {

	private static final long serialVersionUID = 7736186009132970224L;

	public InvalidMoveException() { super(); }
	public InvalidMoveException(String message) { super(message); }
	public InvalidMoveException(String message, Throwable cause) { super(message, cause); }
	public InvalidMoveException(Throwable cause) { super(cause); }

	public InvalidMoveException(Location location1, Location location2, String message) {
		super("Cannot move from " + location1.getName() + " to " + location2.getName() + " - " + message);
	}
}