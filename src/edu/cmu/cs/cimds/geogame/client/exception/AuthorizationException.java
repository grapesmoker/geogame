package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;

import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;


public class AuthorizationException extends GeoGameException implements Serializable {

	private static final long serialVersionUID = -8539753088909326784L;

	@SuppressWarnings("unused")
	private AuthorizationException() { }

	public AuthorizationException(UserDTO player, String message) {
		super(message);
		this.player = player;
	}
	
	private UserDTO player;

	public UserDTO getPlayer() { return player; }
	public void setPlayer(UserDTO player) { this.player = player; }
}