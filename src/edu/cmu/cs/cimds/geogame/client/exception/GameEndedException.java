package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;

public class GameEndedException extends GeoGameException implements Serializable {

	private static final long serialVersionUID = -6709047939032675167L;

	public GameEndedException() {
		super("Game has already ended!");
	}
}