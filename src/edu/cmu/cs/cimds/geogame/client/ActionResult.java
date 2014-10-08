package edu.cmu.cs.cimds.geogame.client;

import java.io.Serializable;
import java.util.Date;

import edu.cmu.cs.cimds.geogame.client.model.dto.GameStruct;



/**
 *
 * @author ajuarez
 */
public class ActionResult implements Serializable {

	private static final long serialVersionUID = -3503320337300022985L;
	
	private Boolean success;
	private String message;
	private GameStruct gameInformation;
	private Date timestamp;
//	private int counter;

	public ActionResult() {
		this.timestamp = new Date();
	}
	
	public Boolean isSuccess() { return success; }
	public void setSuccess(boolean success) { this.success = success; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }
	
	public GameStruct getGameInformation() { return gameInformation; }
	public void setGameInformation(GameStruct gameInformation) { this.gameInformation = gameInformation; }

	public Date getTimestamp() { return timestamp; }
	
//	public int getCounter() { return counter; }
//	public void setCounter(int counter) { this.counter = counter; }
}