package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.cimds.geogame.client.model.db.GeoGameCommand;
import edu.cmu.cs.cimds.geogame.client.model.enums.CommandType;

public class GeoGameCommandDTO implements Serializable {

	/**
	 * The DTO for the GeoGameCommand class.
	 * 
	 * JV 11/20/11
	 */
	private static final long serialVersionUID = 3251371425720337408L;

	private CommandType commandType;
	private long sender;
	private List<UserDTO> targetUsers = new ArrayList<UserDTO>();
	
	public GeoGameCommandDTO () {}
	
	public GeoGameCommandDTO (GeoGameCommand command) {
		this.commandType = command.getCommandType();
	}
	
	public CommandType getCommandType() {
		return commandType;
	}

	public void setCommandType(CommandType commandType) {
		this.commandType = commandType;
	}

	public long getSender() {
		return sender;
	}

	public void setSender(long sender) {
		this.sender = sender;
	}
	
	public List<UserDTO> getTargetUsers() {
		return targetUsers;
	}
	
	public void setTargetUsers(List<UserDTO> targetUsers) {
		this.targetUsers = targetUsers;
	}
	
	public void addTargetUser(UserDTO targetUser) {
		targetUsers.add(targetUser);
	}
	
}
