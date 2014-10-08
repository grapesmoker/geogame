package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.user.client.ui.Image;

import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;

public interface ItemTypeImageCreator {
	public Image createImage(final ItemTypeDTO itemType);
}