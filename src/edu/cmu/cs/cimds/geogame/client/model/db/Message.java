package edu.cmu.cs.cimds.geogame.client.model.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.cmu.cs.cimds.geogame.client.model.dto.MessageDTO;

@Entity	
@Table(name="message")
public class Message extends PersistentEntity {
	
	private static final long serialVersionUID = -4068422072357599546L;
	
	private Date timeSent;
	private User sender;
//	private List<User> receivers;
	private Boolean broadcast;
	private Boolean global = Boolean.FALSE;
	private String content;
	private boolean delivered;
	
	public Message() {
		super();
		this.timeSent = new Date();
	}

	public Message(MessageDTO message) {
		super();
		this.timeSent = new Date();
		this.broadcast = message.isBroadcast();
		this.content = message.getContent();
	}
	
	@Column(name="time_sent", nullable=false)
	public Date getTimeSent() { return timeSent; }
	//For Hibernate use only
	@SuppressWarnings("unused")
	private void setTimeSent(Date timeSent) { this.timeSent = timeSent; }

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="sender_id")
	public User getSender() { return sender; }
	public void setSender(User sender) { this.sender = sender; }

//	@ManyToMany(targetEntity=edu.cmu.cs.cimds.geogame.client.model.db.User.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
//	@JoinTable(
//		name="message_graph",
//		joinColumns=@JoinColumn(name="message_id"),
//		inverseJoinColumns=@JoinColumn(name="receiver_id")
//	)
//	public List<User> getReceivers() { return receivers; }
//	public void setReceivers(List<User> receivers) { this.receivers = receivers; }

	@Column(name="broadcast", nullable=false)
	public Boolean isBroadcast() { return broadcast; }
	public void setBroadcast(Boolean broadcast) { this.broadcast = broadcast; }

	@Column(name="global")
	public Boolean isGlobal() { return global; }
	public void setGlobal(Boolean global) { this.global = global; }

	@Column(name="content", nullable=false)
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }

	@Column(name="delivered")
	public boolean isDelivered() { return delivered; }
	public void setDelivered(boolean delivered) { this.delivered = delivered; }
}