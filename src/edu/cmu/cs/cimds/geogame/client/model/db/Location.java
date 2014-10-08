/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.db;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.model.enums.LocationType;

/**
 *
 * @author ajuarez
 */
@Entity
@Table(name="location")
public class Location extends PersistentEntity {

	private static final long serialVersionUID = 5716028963497847907L;
	
	private String name;
	private String iconFilename;
	private LocationType locationType;
	private Double latitude;
	private Double longitude;
	
	private List<Item> items = new ArrayList<Item>();
	private List<User> players = new ArrayList<User>();
//	private List<Combo> combos;
	
	private String code;
	
	/*public Location(LocationDTO location) {
		this.id = location.getId();
		this.name = location.getName();
		this.iconFilename = location.getIconFilename();
		this.locationType = location.getLocationType();
//		this.mapPosition = new GGLatLng(location.getLatitude(), location.getLongitude());
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		for(ItemDTO itemDTO : location.getItems()) {
			Item item = new Item();
			item.setItemType(new ItemType(itemDTO.getItemType()));
			if(item.getOwner()!=null) {
				itemDTO.setOwner(item.getOwner().getUsername());
			}
			if(item.getLocation()!=null) {
				itemDTO.setLocation(this);
			}
			itemDTO.setId(item.getId());
//			itemDTO.setPrice(item.getPrice());
			this.items.add(itemDTO);
		}
		for(User player : location.getPlayers()) {
			this.players.add(player.getUsername());
		}
//		for(Combo combo : location.getCombos()) {
//			ComboDTO comboDTO = new ComboDTO();
//			comboDTO.setId(combo.getId());
//			comboDTO.setComboType(new ComboTypeDTO(combo.getComboType()));
//			comboDTO.setLocation(this);
//			comboDTO.setPrice(combo.getPrice());
//			this.combos.add(comboDTO);
//		}
	}*/
	
	@Column(name="icon_filename")
	public String getIconFilename() { return iconFilename; }
	public void setIconFilename(String iconFilename) { this.iconFilename = iconFilename; }

	@Column(name="name")
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	@Column(name="location_type")
	public LocationType getLocationType() { return locationType; }
	public void setLocationType(LocationType locationType) { this.locationType = locationType; }

//	@Transient
//	public LatLng getPosition() { return LatLng.newInstance(latitude, longitude); }
//	public void setPosition(LatLng position) {
//		this.latitude = position.getLatitude();
//		this.longitude = position.getLongitude();
//	}
	
	//For Hibernate use only
	@Column(name="latitude")
	public Double getLatitude() { return latitude; }
	public void setLatitude(Double latitude) { this.latitude = latitude; }
	
	//For Hibernate use only
	@Column(name="longitude")
	public Double getLongitude() { return longitude; }
	public void setLongitude(Double longitude) { this.longitude = longitude; }

	@OneToMany(mappedBy="location", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	public List<Item> getItems() { return items; }
	public void setItems(List<Item> items) { this.items = items; }
	public void addItem(Item item) {
		this.items.add(item);
		item.setLocation(this);
	}
	public void removeItem(Item item) {
		this.items.remove(item);
		item.setLocation(null);
	}

	@OneToMany(mappedBy="currentLocation", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	public List<User> getPlayers() { return players; }
	public void setPlayers(List<User> players) { this.players = players; }
	
	@Column(name="code")
	public String getCode() { return code; }
	public void setCode(String code) { this.code = code; }
	
//	@OneToMany(mappedBy="location", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
//	public List<Combo> getCombos() { return combos; }
//	public void setCombos(List<Combo> combos) { this.combos = combos; }
}