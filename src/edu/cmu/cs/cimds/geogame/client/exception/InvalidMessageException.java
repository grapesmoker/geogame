package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;

public class InvalidMessageException extends GeoGameException implements Serializable {
	
	private static final long serialVersionUID = -3593117598577195289L;
	
	public InvalidMessageException () {super();}
	public InvalidMessageException (String message) {super(message);}
	public InvalidMessageException(String message, Throwable cause) { super(message, cause); }
	public InvalidMessageException(Throwable cause) { super(cause); }
}
