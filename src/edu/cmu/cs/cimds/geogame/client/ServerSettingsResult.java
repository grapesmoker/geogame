package edu.cmu.cs.cimds.geogame.client;

import java.io.Serializable;
import java.util.Date;

import edu.cmu.cs.cimds.geogame.client.model.dto.GameStruct;

public class ServerSettingsResult implements Serializable {

	/**
	 * A class to hold the result of sending the settings to the server.
	 */
	private static final long serialVersionUID = 6254114931976740673L;

	private Boolean success;
	private String message;
	private GameStruct gameInformation;
	private Date timestamp;

	public ServerSettingsResult() {
		this.timestamp = new Date();
	}
	
	public Boolean isSuccess() { return success; }
	public void setSuccess(boolean success) { this.success = success; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	
	public GameStruct getGameInformation() { return gameInformation; }
	public void setGameInformation(GameStruct gameInformation) { this.gameInformation = gameInformation; }

	public Date getTimestamp() { return timestamp; }
	
}
