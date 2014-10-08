/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.db;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 *
 * @author ajuarez
 */
@Entity
@Table(name="item")
public class Item extends PersistentEntity {
	
	private static final long serialVersionUID = 6645235987955864150L;
	
	private ItemType itemType;
	private User owner;
	private Location location;
//	private int price;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="item_type_id")
	public ItemType getItemType() { return itemType; }
	public void setItemType(ItemType itemType) { this.itemType = itemType; }
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="owner_id")
	public User getOwner() { return owner; }
	public void setOwner(User owner) { this.owner = owner; }
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="location_id")
	public Location getLocation() { return location; }
	public void setLocation(Location location) { this.location = location; }

//	@Column(name="price")
//	public int getPrice() { return price; }	
//	public void setPrice(int price) { this.price = price; }
	
	@Override
	public boolean equals(Object o) {
		if(o.getClass()==ItemType.class) {
			return this.getItemType().equals(o);
		} else {
			return super.equals(o);
		}
	}
}