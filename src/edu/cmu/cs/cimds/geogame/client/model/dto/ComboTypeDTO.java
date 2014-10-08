///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package edu.cmu.cs.cimds.geogame.client.model.dto;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.persistence.Entity;
//
//import edu.cmu.cs.cimds.geogame.client.model.db.ComboType;
//import edu.cmu.cs.cimds.geogame.client.model.db.ItemType;
//
///**
// *
// * @author ajuarez
// */
//@Entity
//public class ComboTypeDTO implements Serializable {
//
//	private static final long serialVersionUID = 5170645284308786716L;
//
//	public ComboTypeDTO() {}
//	
//	public ComboTypeDTO(ComboType comboType) {
//		this.name = comboType.getName();
//		this.description = comboType.getDescription();
//		this.iconFilename = comboType.getIconFilename();
//		this.setBasePrice(comboType.getBasePrice());
//		for(ItemType itemType : comboType.getItemTypes()) {
//			this.itemTypes.add(new ItemTypeDTO(itemType));
//		}
//	}
//	
//	private String name;
//	private String description;
//	private String iconFilename;
//	private int basePrice;
//	private List<ItemTypeDTO> itemTypes = new ArrayList<ItemTypeDTO>();
//
//	public String getName() { return name; }
//	public void setName(String name) { this.name = name; }
//
//	public String getDescription() { return description; }
//	public void setDescription(String description) { this.description = description; }
//
//	public String getIconFilename() { return iconFilename; }
//	public void setIconFilename(String iconFilename) { this.iconFilename = iconFilename; }
//
//	public int getBasePrice() { return basePrice; }
//	public void setBasePrice(int basePrice) { this.basePrice = basePrice; }
//	
//	public List<ItemTypeDTO> getItemTypes() { return itemTypes; }
//	public void setItemTypes(List<ItemTypeDTO> itemTypes) { this.itemTypes = itemTypes; }
//}