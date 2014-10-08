/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;

import edu.cmu.cs.cimds.geogame.client.model.db.Item;

/**
 *
 * @author ajuarez
 */
public class ItemDTO implements Serializable, Comparable<ItemDTO> {

	private static final long serialVersionUID = 1957464020261389955L;
	
	public ItemDTO() {}
	
	public ItemDTO(Item item) {
		this.id = item.getId();
		this.itemType = new ItemTypeDTO(item.getItemType());
//		this.price = item.getPrice();
		if(item.getOwner()!=null) {
			this.owner = item.getOwner().getUsername();
		}
		if(item.getLocation()!=null) {
			this.location = new LocationDTO(item.getLocation());
		}
	}
	
	private long id;
	private ItemTypeDTO itemType;
	private String owner;
	private LocationDTO location;
	private int price;

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public ItemTypeDTO getItemType() { return itemType; }
	public void setItemType(ItemTypeDTO itemType) { this.itemType = itemType; }
	
	public String getOwner() { return owner; }
	public void setOwner(String owner) { this.owner = owner; }
	
	public LocationDTO getLocation() { return location; }
	public void setLocation(LocationDTO location) { this.location = location; }
	
	public int getPrice() { return price; }	
	public void setPrice(int price) { this.price = price; }

	@Override
	public boolean equals(Object o) {
		if(o==null) {
			return false;
		}
		if(o.getClass()==Item.class && ((Item) o).getId()==this.id) {
			return true;
		}
		if(o.getClass()==ItemDTO.class && ((ItemDTO) o).getId()==this.id) {
			return true;
		}
		if(o.getClass()==ItemTypeDTO.class && ((ItemTypeDTO) o).getId()==this.getItemType().getId()) {
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(ItemDTO o) {
		return this.getItemType().getName().compareTo(o.getItemType().getName());
//		if(this.id > o.getId()) {
//			return 1;
//		} else if(this.id < o.getId()) {
//			return -1;
//		} else {
//			return 0;
//		}
	}
}