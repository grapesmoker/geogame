package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.user.client.ui.Image;

import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;

public class NoopItemImageCreator implements ItemImageCreator {
	public Image createImage(final ItemDTO item) {
		Image image = new Image(item.getItemType().getIconFilename());
		image.setWidth(WindowInformation.imageWidth);
		return image;
	}
}