package edu.cmu.cs.cimds.geogame.client.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.hibernate.Session;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.GameInfo;
import edu.cmu.cs.cimds.geogame.client.exception.AuthenticationException;
import edu.cmu.cs.cimds.geogame.client.model.db.User;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;
import edu.cmu.cs.cimds.geogame.server.AuthenticationUtil;
import edu.cmu.cs.cimds.geogame.server.MessageServiceImpl;
import edu.cmu.cs.cimds.geogame.server.PersistenceManager;

public class LanguageDisplay extends DialogBox {
	
	Grid itemGrid = new Grid(2, 1);
	VerticalPanel containerPanel = new VerticalPanel();
	Iterator<ItemTypeDTO> itemTypesIterator;
	Button okButton = new Button("Done");
	
	private static final String FILLER_FILENAME = "filler.png";
	
	public LanguageDisplay() {
		super(false, true);
	}
	
	public void init () {
		
		okButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				LanguageDisplay.this.hide();				
			}
		});
		
		AsyncCallback<List<ItemTypeDTO>> callback = new AsyncCallback<List<ItemTypeDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not get item list!");
				//Window.alert(caught.getMessage() + caught.getCause());
			}

			@Override
			public void onSuccess(List<ItemTypeDTO> itemTypes) {
				final UserDTO player = GameInfo.getInstance().getPlayer();
				//Window.alert("here : On Success");
				LanguageDisplay.this.itemTypesIterator = itemTypes.iterator();
				containerPanel.clear();
				containerPanel.setPixelSize(500, 250);
				containerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				containerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				
				Timer itemDisplayTimer = new Timer() {
					
					@Override
					public void run() {
						
						if (itemTypesIterator.hasNext()) {
						
							if(player.getItemNameSynPairs() != null) {
								//Window.alert("Hash map exists");
								ItemTypeDTO itemType = itemTypesIterator.next();
								Image image = new Image(itemType.getIconFilename());
								final Image filler = new Image(FILLER_FILENAME);
								
								HTML label = new HTML();
								
								label.setHTML("<h1>" + player.getItemNameSynPairs().get(itemType.getName()) + "</h1>");
								
								itemGrid.setWidget(0, 0, image);
								itemGrid.setWidget(1, 0, label);
								itemGrid.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
								itemGrid.getCellFormatter().setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
								containerPanel.add(itemGrid);
								
								LanguageDisplay.this.center();
								
								Timer fillerTimer = new Timer() {

									@Override
									public void run() {
										itemGrid.setWidget(0, 0, filler);
										itemGrid.setWidget(1, 0, new Label());
										itemGrid.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
										itemGrid.getCellFormatter().setAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
										containerPanel.add(itemGrid);
									}
									
								};
								fillerTimer.schedule(1000);
								
								//itemGrid.setWidget(0, 0, filler);
							}
							else {
								//Window.alert("Hash map doest exist");

								// Load form the Database.
								GameServices.gameService.getItemNameSynPairsForUser(player.getId(),  new AsyncCallback<HashMap<String, String>>() {
									@Override
									public void onFailure(Throwable caught) {
										Window.alert("Failure!");
									}
									@Override
									public void onSuccess(HashMap<String, String> itemNameSynPairs) {
										//Window.alert("Success!");
										player.setItemNameSynPairs(itemNameSynPairs);
									}
								});
																
							}
						}
						else {
							this.cancel();
							containerPanel.clear();
							itemGrid.clear();
							itemGrid.setWidget(0, 0, okButton);
							containerPanel.add(itemGrid);
							LanguageDisplay.this.center();
						}
					}
				};
				
				itemDisplayTimer.scheduleRepeating(2000);
			}
			
		};
		GameServices.gameService.getAllItemTypes(callback);
		
		this.setWidget(containerPanel);
	}
}
