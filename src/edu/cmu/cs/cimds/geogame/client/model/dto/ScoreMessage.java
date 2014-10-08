package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;

import edu.cmu.cs.cimds.geogame.client.model.db.Item;

public class ScoreMessage implements Serializable {
	
	private static final long serialVersionUID = -8891762555421273707L;
	
	private UserDTO actor;
	private UserDTO receiver;
	private ItemDTO itemObtained;
	private int scoreObtained;

	public ScoreMessage() {}
	public ScoreMessage(UserDTO actor, UserDTO receiver, Item item, int scoreObtained) {
		this.actor = actor;//new UserDTO(actor,false);
		this.receiver = receiver;//new UserDTO(receiver,false);
		this.itemObtained = new ItemDTO(item);
		this.scoreObtained = scoreObtained;
	}
	public UserDTO getActor() { return actor; }
	public void setActor(UserDTO actor) { this.actor = actor; }

	public UserDTO getReceiver() { return receiver; }
	public void setReceiver(UserDTO receiver) { this.receiver = receiver; }
	
	public ItemDTO getItemObtained() { return itemObtained; }
	public void setItemObtained(ItemDTO itemObtained) { this.itemObtained = itemObtained; }
	
	public int getScoreObtained() { return scoreObtained; }
	public void setScoreObtained(int scoreObtained) { this.scoreObtained = scoreObtained; }
}