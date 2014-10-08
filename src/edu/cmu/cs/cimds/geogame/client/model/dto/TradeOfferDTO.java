///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package edu.cmu.cs.cimds.geogame.client.model.dto;
//
//import java.io.Serializable;
//import java.util.Date;
//import java.util.List;
//
//import edu.cmu.cs.cimds.geogame.client.model.db.TradeOffer;
//
//
///**
// *
// * @author ajuarez
// */
//public class TradeOfferDTO implements Serializable {
//	
//	private static final long serialVersionUID = 8187701855516770809L;
//	
//	private long id;
//	private TradeOffer.Status status;
//	private UserDTO player1;
//	private UserDTO player2;
//	private int money1;
//	private int money2;
//	private List<ItemDTO> items1;
//	private List<ItemDTO> items2;
//	private Date timeCreated;
//	private Date timeReplied;
//
//	public TradeOfferDTO() { }
//	
//	public TradeOfferDTO(TradeOffer tradeOffer) {
//		this.id = tradeOffer.getId();
//		this.status = tradeOffer.getStatus();
//		this.player1 = new UserDTO(tradeOffer.getPlayer1(), false);
//		this.player2 = new UserDTO(tradeOffer.getPlayer2(), false);
//		this.money1 = tradeOffer.getMoney1();
//		this.money2 = tradeOffer.getMoney2();
////		this.items1 = tradeOffer.getItemsString1();
////		this.items2 = tradeOffer.getItemsString2();
////		this.items1 = tradeOffer.getItems1();
////		this.items2 = tradeOffer.getItems2();
//		this.timeCreated = tradeOffer.getTimeCreated();
//		this.timeReplied = tradeOffer.getTimeReplied();
//	}
//
//	public long getId() { return id; }
//	public void setId(long id) { this.id = id; }
//
//	public TradeOffer.Status getStatus() { return status; }
//	public void setStatus(TradeOffer.Status status) { this.status = status; }
//
//	public UserDTO getPlayer1() { return player1; }
//	public void setPlayer1(UserDTO player1) { this.player1 = player1; }
//
//	public UserDTO getPlayer2() { return player2; }
//	public void setPlayer2(UserDTO player2) { this.player2 = player2; }
//
//	public int getMoney1() { return money1; }
//	public void setMoney1(int money1) { this.money1 = money1; }
//
//	public int getMoney2() { return money2; }
//	public void setMoney2(int money2) { this.money2 = money2; }
//
//	public List<ItemDTO> getItems1() { return items1; }
//	public void setItems1(List<ItemDTO> items1) { this.items1 = items1; }
//
//	public List<ItemDTO> getItems2() { return items2; }
//	public void setItems2(List<ItemDTO> items2) { this.items2 = items2; }
//
//	public Date getTimeCreated() { return timeCreated; }
//	public void setTimeCreated(Date timeCreated) { this.timeCreated = timeCreated; }
//
//	public Date getTimeReplied() { return timeReplied; }
//	public void setTimeReplied(Date timeReplied) { this.timeReplied = timeReplied; }
//}