package edu.cmu.cs.cimds.geogame.client.model.db;

import javax.persistence.*;

import edu.cmu.cs.cimds.geogame.client.model.dto.GeoGameCommandDTO;
import edu.cmu.cs.cimds.geogame.client.model.enums.CommandType;

@Entity
@Table(name="command")
public class GeoGameCommand extends PersistentEntity {

	/**
	 * The purpose of this class is to act as a general-purpose command structure between
	 * the game administrator and the clients. In other words, if you need to have something
	 * happen during the game on the client side, this class would act as the bearer of that
	 * information.
	 * 
	 * JV 11/20/11
	 */
	private static final long serialVersionUID = 465264127537450678L;

	private CommandType commandType;
	private long sender;
	
	public GeoGameCommand(GeoGameCommandDTO command) {
		// TODO Auto-generated constructor stub
		this.commandType = command.getCommandType();
		this.sender = command.getSender();
	}

	@Column(name="command_type")
	public CommandType getCommandType() {
		return commandType;
	}

	public void setCommandType(CommandType commandType) {
		this.commandType = commandType;
	}

	@Column(name="sender_id")
	public long getSender() {
		return sender;
	}

	public void setSender(long sender) {
		this.sender = sender;
	}
	
	
}
