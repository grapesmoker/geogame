package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.user.client.ui.Image;

import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;

public class NoopItemTypeImageCreator implements ItemTypeImageCreator {
	public Image createImage(final ItemTypeDTO itemType) {
		Image image = new Image(itemType.getIconFilename());
		image.setWidth(WindowInformation.imageWidth);
		return image;
	}
}