package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;

public class InvalidArgsException extends GeoGameException implements Serializable {

	private static final long serialVersionUID = -9143628187449816435L;

	@SuppressWarnings("unused")
	private InvalidArgsException() { }

	public InvalidArgsException(String message) {
		super(message);
	}
}