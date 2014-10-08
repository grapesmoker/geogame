///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package edu.cmu.cs.cimds.geogame.client.model.db;
//
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//
///**
// *
// * @author ajuarez
// */
//@Entity
//@Table(name="trade_offer")
//public class TradeOffer extends PersistentEntity {
//	
//	private static final long serialVersionUID = -388348173652195072L;
//	
//	public enum Status {
//		PENDING ("Pending"),
//		ACCEPTED ("Accepted"),
//		REFUSED ("Refused"),
//		STALE ("Stale");
//
//		private String descriptor;
//		private Status(String descriptor) {
//			this.descriptor = descriptor;
//		}
//		
//		@Override
//		public String toString() {
//			return this.descriptor;
//		}
//	}
//	
//	private Status status;
//	private User player1;
//	private User player2;
//	private int money1;
//	private int money2;
//	private String itemsString1;
//	private String itemsString2;
//	private Date timeCreated =  new Date();
//	private Date timeReplied;
//	private boolean delivered;
//	
//	@Column(name="status")
//	public Status getStatus() { return status; }
//	public void setStatus(Status status) { this.status = status; }
//
//	@ManyToOne(fetch=FetchType.EAGER)
//	@JoinColumn(name="player1_id")
//	public User getPlayer1() { return player1; }
//	public void setPlayer1(User player1) { this.player1 = player1; }
//	
//	@ManyToOne(fetch=FetchType.EAGER)
//	@JoinColumn(name="player2_id")
//	public User getPlayer2() { return player2; }
//	public void setPlayer2(User player2) { this.player2 = player2; }
//	
//	@Column(name="player1_money")
//	public int getMoney1() { return money1; }
//	public void setMoney1(int money1) { this.money1 = money1; }
//	
//	@Column(name="player2_money")
//	public int getMoney2() { return money2; }
//	public void setMoney2(int money2) { this.money2 = money2; }
//	
//	@Column(name="player1_items")
//	public String getItemsString1() { return itemsString1; }
//	public void setItemsString1(String itemsString1) { this.itemsString1 = itemsString1; }
//	
//	@Column(name="player2_items")
//	public String getItemsString2() { return itemsString2; }
//	public void setItemsString2(String itemsString2) { this.itemsString2 = itemsString2; }
//	
//	@Column(name="time_created")
//	public Date getTimeCreated() { return timeCreated; }
//	public void setTimeCreated(Date timeCreated) { this.timeCreated = timeCreated; }
//	
//	@Column(name="time_replied")
//	public Date getTimeReplied() { return timeReplied; }
//	public void setTimeReplied(Date timeReplied) { this.timeReplied = timeReplied; }
//	
//	@Column(name="delivered")
//	public boolean isDelivered() { return delivered; }
//	public void setDelivered(boolean delivered) { this.delivered = delivered; }
//}