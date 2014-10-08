/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;
import java.util.List;

import edu.cmu.cs.cimds.geogame.client.model.db.ItemType;

/**
 *
 * @author ajuarez
 */
public class ItemTypeDTO implements Serializable, Comparable<ItemTypeDTO> {

	private static final long serialVersionUID = -5220146215958074718L;

	public ItemTypeDTO() {}
	
	public ItemTypeDTO(ItemType itemType) {
		this.id = itemType.getId();
		this.name = itemType.getName();
		this.description = itemType.getDescription();
		this.iconFilename = itemType.getIconFilename();
		this.basePrice = itemType.getBasePrice();
		this.itemSynonyms = itemType.getSynonymNames();
	}
	
	private long id;
	private String name;
	private String description;
	private String iconFilename;
	private int basePrice;
	private List<String> itemSynonyms;

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public String getIconFilename() { return iconFilename; }
	public void setIconFilename(String iconFilename) { this.iconFilename = iconFilename; }

	public int getBasePrice() { return basePrice; }
	public void setBasePrice(int basePrice) { this.basePrice = basePrice; }

	@Override
	public boolean equals(Object o) {
		if(o==null) {
			return false;
		}
		if(o.getClass()==ItemTypeDTO.class && ((ItemTypeDTO) o).getId()==this.id) {
			return true;
		}
		if(o.getClass()==ItemDTO.class && ((ItemDTO) o).getItemType().getId()==this.id) {
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(ItemTypeDTO o) {
		return this.getName().compareTo(o.getName());
	}
	
	public List<String> getSynonymNames() {
		return this.itemSynonyms;
	}
	
	public void setSynonymNames(List<String> newSynonyms) {
		this.itemSynonyms = newSynonyms;
	}
}