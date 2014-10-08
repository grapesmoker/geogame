/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.cs.cimds.geogame.client.model.db.Item;
import edu.cmu.cs.cimds.geogame.client.model.db.Location;
import edu.cmu.cs.cimds.geogame.client.model.db.User;
import edu.cmu.cs.cimds.geogame.client.model.enums.LocationType;

/**
 *
 * @author ajuarez
 */
public class LocationDTO implements Serializable {

	private static final long serialVersionUID = 6254137908211165531L;

	public LocationDTO() {}
	
	public LocationDTO(Location location) {
		this.id = location.getId();
		this.name = location.getName();
		this.iconFilename = location.getIconFilename();
		this.locationType = location.getLocationType();
//		this.mapPosition = new GGLatLng(location.getLatitude(), location.getLongitude());
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		for(Item item : location.getItems()) {
			ItemDTO itemDTO = new ItemDTO();
			itemDTO.setItemType(new ItemTypeDTO(item.getItemType()));
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
	}

	private long id;
	private String name;
	private String iconFilename;
	private LocationType locationType;
	private List<ItemDTO> items = new ArrayList<ItemDTO>();
	private List<String> players = new ArrayList<String>();
//	private List<ComboDTO> combos = new ArrayList<ComboDTO>();
	
	private double latitude;
	private double longitude;
//	private GGLatLng mapPosition;

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public String getIconFilename() { return iconFilename; }
	public void setIconFilename(String iconFilename) { this.iconFilename = iconFilename; }

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public LocationType getLocationType() { return locationType; }
	public void setLocationType(LocationType locationType) { this.locationType = locationType; }

//	public GGLatLng getMapPosition() { return this.mapPosition; }
//	public void setMapPosition(GGLatLng mapPosition) { this.mapPosition = mapPosition; }
	
	public double getLatitude() { return latitude; }
	public void setLatitude(double latitude) { this.latitude = latitude; }
	
	public double getLongitude() { return longitude; }
	public void setLongitude(double longitude) { this.longitude = longitude; }

	public List<ItemDTO> getItems() { return items; }
	public void setItems(List<ItemDTO> items) { this.items = items; }

	public List<String> getPlayers() { return players; }
	public void setPlayers(List<String> players) { this.players = players; }

	@Override
	public boolean equals(Object obj) {
		if(obj==null || obj.getClass()!=LocationDTO.class) {
			return false;
		}
		return this.id==((LocationDTO)obj).getId();
	}

//	public List<ComboDTO> getCombos() { return combos; }
//	public void setCombos(List<ComboDTO> combos) { this.combos = combos; }
}