package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Antonio
 */
public class TradeStruct implements Serializable {

	private static final long serialVersionUID = 5620913541614104818L;

	public TradeStruct() {}

	public int gold = 0;
	public List<ItemDTO> inventoryItems = new ArrayList<ItemDTO>();
	public List<ItemDTO> tradeItems = new ArrayList<ItemDTO>();
	
	public void reset(UserDTO player) {
		this.gold = 0;
		this.inventoryItems.clear();
		this.tradeItems.clear();
		
		this.inventoryItems.addAll(player.getInventory());
	}
}