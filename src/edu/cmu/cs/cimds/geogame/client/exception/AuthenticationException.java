package edu.cmu.cs.cimds.geogame.client.exception;

import java.io.Serializable;

import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;


public class AuthenticationException extends GeoGameException implements Serializable {

	private static final long serialVersionUID = 4270448250523928729L;

	@SuppressWarnings("unused")
	private AuthenticationException() { }

	public AuthenticationException(UserDTO player) {
		super("Player " + player.getUsername() + " incorrectly authenticated!");
		this.player = player;
	}
	
	private UserDTO player;

	public UserDTO getPlayer() { return player; }
	public void setPlayer(UserDTO player) { this.player = player; }
}