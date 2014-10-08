package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.user.client.ui.Image;

import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;

public interface ItemImageCreator {
	public Image createImage(final ItemDTO item);
}