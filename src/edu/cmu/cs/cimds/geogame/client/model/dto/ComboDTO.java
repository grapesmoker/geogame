///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package edu.cmu.cs.cimds.geogame.client.model.dto;
//
//import java.io.Serializable;
//
//import javax.persistence.Entity;
//
//import edu.cmu.cs.cimds.geogame.client.model.db.Combo;
//
///**
// *
// * @author ajuarez
// */
//@Entity
//public class ComboDTO implements Serializable {
//
//	private static final long serialVersionUID = -5144971074386122738L;
//
//	public ComboDTO() {}
//	
//	public ComboDTO(Combo combo) {
//		this.id = combo.getId();
//		this.comboType = new ComboTypeDTO(combo.getComboType());
//		this.price = combo.getPrice();
//		if(combo.getLocation()!=null) {
//			this.location = new LocationDTO(combo.getLocation());
//		}
//	}
//	
//	private long id;
//	private ComboTypeDTO comboType;
//	private LocationDTO location;
//	private int price;
//
//	public long getId() { return id; }
//	public void setId(long id) { this.id = id; }
//
//	public ComboTypeDTO getComboType() { return comboType; }
//	public void setComboType(ComboTypeDTO comboType) { this.comboType = comboType; }
//	
//	public LocationDTO getLocation() { return location; }
//	public void setLocation(LocationDTO location) { this.location = location; }
//	
//	public int getPrice() { return price; }	
//	public void setPrice(int price) { this.price = price; }
//
//	@Override
//	public boolean equals(Object o) {
//		if(o!=null && o.getClass()==ComboDTO.class && ((ComboDTO) o).getId()==this.id) {
//			return true;
//		} else {
//			return false;
//		}
//	}
//}