///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package edu.cmu.cs.cimds.geogame.client.model.db;
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
//@Table(name="combo")
//public class Combo extends PersistentEntity {
//	
//	private static final long serialVersionUID = 6645235987955864150L;
//	
//	private ComboType comboType;
//	private Location location;
//	private int price;
//	
//	@ManyToOne(fetch=FetchType.EAGER)
//	@JoinColumn(name="combo_type_id")
//	public ComboType getComboType() { return comboType; }
//	public void setComboType(ComboType comboType) { this.comboType = comboType; }
//	
//	@ManyToOne(fetch=FetchType.EAGER)
//	@JoinColumn(name="location_id")
//	public Location getLocation() { return location; }
//	public void setLocation(Location location) { this.location = location; }
//
//	@Column(name="price")
//	public int getPrice() { return price; }	
//	public void setPrice(int price) { this.price = price; }
//
//}