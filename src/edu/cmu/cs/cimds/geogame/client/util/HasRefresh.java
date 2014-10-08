package edu.cmu.cs.cimds.geogame.client.util;

import com.google.gwt.view.client.HasData;
import edu.cmu.cs.cimds.geogame.client.ui.PlayerEntry;

public interface HasRefresh {

	void refresh(HasData<PlayerEntry> display);
}
