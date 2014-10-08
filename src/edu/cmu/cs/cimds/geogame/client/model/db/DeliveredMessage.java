package edu.cmu.cs.cimds.geogame.client.model.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="message_graph")
public class DeliveredMessage extends PersistentEntity {
	
	private static final long serialVersionUID = -3050581116975017148L;
	
	private Message message;
	private User receiver;
	private Date timeReceived;
	
	public DeliveredMessage() {
		super();
	}

	@Column(name="time_received")
	public Date getTimeReceived() { return timeReceived; }
	//For Hibernate use only
//	@SuppressWarnings("unused")
	public void setTimeReceived(Date timeReceived) { this.timeReceived = timeReceived; }

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="message_id", nullable=false)
	public Message getMessage() { return message; }
	public void setMessage(Message message) { this.message = message; }

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="receiver_id", nullable=false)
	public User getReceiver() { return receiver; }
	public void setReceiver(User receiver) { this.receiver = receiver; }
}