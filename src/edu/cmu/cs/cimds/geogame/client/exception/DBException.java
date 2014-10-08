package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;

public class DBException extends GeoGameException implements Serializable {

	private static final long serialVersionUID = -5018922146122239561L;

	@SuppressWarnings("unused")
	private DBException() { }
	
	public DBException(Exception ex) {
		super(ex.getMessage(), ex.getCause());
		super.setStackTrace(ex.getStackTrace());
	}
}