package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;

public abstract class GeoGameException extends Exception implements Serializable {

	private static final long serialVersionUID = 1429519860687998317L;

	public GeoGameException() { super(); }
	public GeoGameException(String message) { super(message); }
	public GeoGameException(String message, Throwable cause) { super(message, cause); }
	public GeoGameException(Throwable cause) { super(cause); }
}