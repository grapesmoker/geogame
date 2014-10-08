package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;

import edu.cmu.cs.cimds.geogame.client.model.db.Item;

public class InvalidPurchaseException extends GeoGameException implements Serializable {

	private static final long serialVersionUID = -8109065537306116996L;
	
	public InvalidPurchaseException() { super(); }
	public InvalidPurchaseException(String message) { super(message); }
	public InvalidPurchaseException(String message, Throwable cause) { super(message, cause); }
	public InvalidPurchaseException(Throwable cause) { super(cause); }
	public InvalidPurchaseException(Item item, String message) {
		super("Cannot purchase " + item.getItemType().getName() + ": " + message);
	}
}