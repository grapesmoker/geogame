package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;
import java.util.Date;

import edu.cmu.cs.cimds.geogame.client.MapInformation;

/**
 *
 * @author Antonio
 */
public class GameStruct implements Serializable {

	private static final long serialVersionUID = -977077591268095178L;
	
	public GameStruct() {}
	public GameStruct(UserDTO player, MapInformation mapInformation) {
		this.player = player;
		this.mapInformation = mapInformation;
	}
	
	public UserDTO player;
	public MapInformation mapInformation;
	//A date passed by the server for synchronization purposes
	//Should only be used when the user logs in.
	public Date sync = new Date();
	public long timeRemaining;
	public long gameDuration;
	public boolean gameStarted;
	public boolean gameFinished;
}