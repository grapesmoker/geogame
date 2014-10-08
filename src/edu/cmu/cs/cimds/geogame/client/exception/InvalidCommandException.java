package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;

public class InvalidCommandException extends GeoGameException implements Serializable {
	
	private static final long serialVersionUID = -3593117598577195289L;
	
	public InvalidCommandException () {super();}
	public InvalidCommandException (String message) {super(message);}
	public InvalidCommandException(String message, Throwable cause) { super(message, cause); }
	public InvalidCommandException(Throwable cause) { super(cause); }
}