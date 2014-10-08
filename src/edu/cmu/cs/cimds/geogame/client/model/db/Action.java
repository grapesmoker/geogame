package edu.cmu.cs.cimds.geogame.client.model.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.cmu.cs.cimds.geogame.client.model.enums.ActionType;

@Entity
@Table(name="action")
public class Action extends PersistentEntity {

	private static final long serialVersionUID = -4306152781293708732L;
	
	private ActionType actionType;
	private User user;
	private Date timeAction;
	private User destinationUser;
	private Location sourceLocation;
	private Location destinationLocation;
//	private Combo combo;
	private Item item;
	private ItemType itemType;
	private int scoreIncrease;
	private int newScore;
	private String locationItems;
//	private Integer price;
//	private Boolean burglary;
//	private Integer burglaryCost;

	public Action() {}
	
	public Action(User user, ActionType actionType) {
		this.user = user;
		this.actionType = actionType;
		this.timeAction = new Date();
	}
	
	@Column(name="action_type", nullable=false)
	public ActionType getActionType() { return actionType; }
	public void setActionType(ActionType actionType) { this.actionType = actionType; }
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="user_id")
	public User getUser() {  return user;}
	public void setUser(User user) { this.user = user; }

	@Column(name="time_action", nullable=false)
	public Date getTimeAction() { return timeAction; }
	public void setTimeAction(Date timeAction) { this.timeAction = timeAction; }
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="destination_user_id")
	public User getDestinationUser() { return destinationUser; }
	public void setDestinationUser(User destinationUser) { this.destinationUser = destinationUser; }
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="source_location_id")
	public Location getSourceLocation() { return sourceLocation; }
	public void setSourceLocation(Location sourceLocation) { this.sourceLocation = sourceLocation; }
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="destination_location_id")
	public Location getDestinationLocation() { return destinationLocation; }
	public void setDestinationLocation(Location destinationLocation) { this.destinationLocation = destinationLocation; }

//	@ManyToOne(fetch=FetchType.EAGER)
//	@JoinColumn(name="combo_id")
//	public Combo getCombo() { return combo; }
//	public void setCombo(Combo combo) { this.combo = combo; }

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="item_id")
	public Item getItem() { return item; }
	public void setItem(Item item) { this.item = item; }

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="item_type_id")
	public ItemType getItemType() { return itemType; }
	public void setItemType(ItemType itemType) { this.itemType = itemType; }

//	@Column(name="price")
// 	public Integer getPrice() { return price; }
//	public void setPrice(Integer price) { this.price = price; }
//
//	@Column(name="burglary")
//	public Boolean getBurglary() { return burglary; }
//	public void setBurglary(Boolean burglary) { this.burglary = burglary; }
//
//	@Column(name="burglary_cost")
//	public Integer getBurglaryCost() { return burglaryCost; }
//	public void setBurglaryCost(Integer burglaryCost) { this.burglaryCost = burglaryCost; }

	@Column(name="score_increase")
	public int getScoreIncrease() { return scoreIncrease; }
	public void setScoreIncrease(int scoreIncrease) { this.scoreIncrease = scoreIncrease; }

	@Column(name="new_score")
	public int getNewScore() { return newScore; }
	public void setNewScore(int newScore) { this.newScore = newScore; }

	@Column(name="location_items")
	public String getLocationItems() { return locationItems; }
	public void setLocationItems(String locationItems) { this.locationItems = locationItems; }
}