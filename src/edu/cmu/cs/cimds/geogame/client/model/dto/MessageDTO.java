/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.cmu.cs.cimds.geogame.client.model.db.Message;
import edu.cmu.cs.cimds.geogame.client.model.db.User;

public class MessageDTO implements Serializable {

	private static final long serialVersionUID = 2502923451933052689L;
	
	public MessageDTO() {}
	
	public MessageDTO(Message message, List<User> receivers) {
		this.id = message.getId();
		this.timeSent = message.getTimeSent();
		this.sender = message.getSender().getUsername();
		//We don't want players to know the list of people the message was sent to.
		this.receivers = new ArrayList<String>();
		for(User receiver : receivers) {
			this.receivers.add(receiver.getUsername());
		}
		this.broadcast = message.isBroadcast();
		this.content = message.getContent();
	}
	
	public MessageDTO (Message message) {
		this.id = message.getId();
		this.timeSent = message.getTimeSent();
		this.sender = message.getSender().getUsername();
		this.broadcast = message.isBroadcast();
		this.content = message.getContent();
	}
	
	public MessageDTO (MessageDTO aMessage) {
		this.broadcast = aMessage.isBroadcast();
		this.content = aMessage.getContent();
		this.timeSent = aMessage.getTimeSent();
		this.id = aMessage.getId();
		this.sender = aMessage.getSender();
	}
	
	private Long id;
	private Date timeSent;
	private String sender;
	private List<String> receivers;
	private boolean broadcast;
	private String content;

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public Date getTimeSent() { return timeSent; }
	public void setTimeSent(Date timeSent) { this.timeSent = timeSent; }

	public String getSender() { return sender; }
	public void setSender(String sender) { this.sender = sender; }

	public List<String> getReceivers() { return receivers; }
	public void setReceivers(List<String> receivers) { this.receivers = receivers; }

	public Boolean isBroadcast() { return broadcast; }
	public void setBroadcast(Boolean broadcast) { this.broadcast = broadcast; }

	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MessageDTO))
			return false;
		return this.hashCode()==obj.hashCode();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + (this.getTimeSent() != null ? this.getTimeSent().hashCode() : 0);
		hash = 67 * hash + (this.getSender() != null ? this.getSender().hashCode() : 0);
		hash = 67 * hash + (this.content != null ? this.content.hashCode() : 0);
		return hash;
	}
}