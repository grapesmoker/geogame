package edu.cmu.cs.cimds.geogame.server;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;
import edu.cmu.cs.cimds.geogame.client.model.db.Item;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;

public class Util {

	public static String getIdsString(List<ItemDTO> entityList) {
		if(entityList.isEmpty()) {
			return "";
		}
		StringBuilder itemsStringBuilder = new StringBuilder(String.valueOf(entityList.get(0).getId()));
		for(int i=1;i<entityList.size();i++) {
			itemsStringBuilder.append("," + String.valueOf(entityList.get(i).getId()));
		}
		return itemsStringBuilder.toString();
	}

	@SuppressWarnings("unchecked")
	public static List<ItemDTO> getItemsFromIdsString(Session session, String idsString) throws GeoGameException {
		List<Long> idList = new ArrayList<Long>();
		List<ItemDTO> itemList = new ArrayList<ItemDTO>();
		if(idsString.isEmpty()) {
			return itemList;
		}
		String[] idStrings = idsString.split(",");
		for(String idString : idStrings) {
			idList.add(Long.valueOf(idString));
		}

		List<Item> items = (List<Item>)session.createCriteria(Item.class)
		.add(Restrictions.in("id", idList))
		.list();
		for(Item item : items) {
			itemList.add(new ItemDTO(item));
		}
		return itemList;
	}
}