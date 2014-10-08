package edu.cmu.cs.cimds.geogame.client;

import java.io.Serializable;

import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

/**
 *
 * @author ajuarez
 */
public class LoginResult implements Serializable {

	private static final long serialVersionUID = -4761894794831096572L;
	
	private Boolean success;
	private UserDTO player;

	public boolean isSuccess() { return success; }
	public void setSuccess(boolean success) { this.success = success; }

	public UserDTO getPlayer() { return player; }
	public void setPlayer(UserDTO player) { this.player = player; }
}