package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;

public class GameNotStartedException extends GeoGameException implements Serializable {

	private static final long serialVersionUID = -6709047939032675167L;

	public GameNotStartedException() {
		super("Game has not yet started!");
	}
}