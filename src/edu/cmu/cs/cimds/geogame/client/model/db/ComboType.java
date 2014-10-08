///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package edu.cmu.cs.cimds.geogame.client.model.db;
//
//import java.util.List;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.JoinColumn;
//import javax.persistence.JoinTable;
//import javax.persistence.ManyToMany;
//import javax.persistence.Table;
//
//
///**
// *
// * @author ajuarez
// */
//@Entity
//@Table(name="combo_type")
//public class ComboType extends PersistentEntity {
//
//	private static final long serialVersionUID = -3781045944224115779L;
//	
//	private String name;
//	private String description;
//	private String iconFilename;
//	private List<ItemType> itemTypes;
//	private int basePrice;
//
//	@Column(name="name", nullable=false)
//	public String getName() { return name; }
//	public void setName(String name) { this.name = name; }
//
//	@Column(name="description")
//	public String getDescription() { return description; }
//	public void setDescription(String description) { this.description = description; }
//
//	@Column(name="icon_filename")
//	public String getIconFilename() { return iconFilename; }
//	public void setIconFilename(String iconFilename) { this.iconFilename = iconFilename; }
//
//	@Column(name="base_price")
//	public int getBasePrice() { return basePrice; }
//	public void setBasePrice(int basePrice) { this.basePrice = basePrice; }
//	
//	@ManyToMany(targetEntity=edu.cmu.cs.cimds.geogame.client.model.db.ItemType.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
//	@JoinTable(
//		name="item_types_per_combo_type",
//		joinColumns=@JoinColumn(name="combo_type_id"),
//		inverseJoinColumns=@JoinColumn(name="item_type_id")
//	)
//	public List<ItemType> getItemTypes() { return itemTypes; }
//	public void setItemTypes(List<ItemType> itemTypes) { this.itemTypes = itemTypes; }
//}