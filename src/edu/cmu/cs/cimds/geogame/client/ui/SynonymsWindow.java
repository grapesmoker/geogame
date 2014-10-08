package edu.cmu.cs.cimds.geogame.client.ui;

import java.util.ArrayList;
import java.util.List;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;

public class SynonymsWindow extends DialogBox {
	
	VerticalPanel containerPanel = new VerticalPanel();
	Grid grid = new Grid(8, 10);
	Button okButton = new Button("OK");
	Button cancelButton = new Button("Cancel");
	List<ItemTypeDTO> currentItemTypes = new ArrayList<ItemTypeDTO>();
	
	public SynonymsWindow() {
		super(false, true);
	}

	public void init() {
		
		AsyncCallback<List<ItemTypeDTO>> callback = new AsyncCallback<List<ItemTypeDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("Could not retrieve item list!");
			}

			@Override
			public void onSuccess(List<ItemTypeDTO> itemTypes) {
				currentItemTypes = itemTypes;
				
				grid.resize(8, itemTypes.size() / 3);
				for (int i = 0; i < grid.getColumnCount(); i++) {
					if (i % 2 == 0) {
						grid.setWidget(0, i, new Label("Item Name"));
					}
					else {
						grid.setWidget(0, i, new Label("Synonym"));
					}
				}
				
				int i = 1;
				int j = 0;
				for (ItemTypeDTO itemType : itemTypes) {
					Label itemTypeName = new Label(itemType.getName());
					TextBox itemTypeSyn = new TextBox();
					String text = new String();
					if (itemType.getSynonymNames().size() > 0) {
						for (String name : itemType.getSynonymNames()) {
							text += name + ",";
						}
						text = text.substring(0, text.length() - 1);
						itemTypeSyn.setText(text);
					}
					
					grid.setWidget(i, j, itemTypeName);
					grid.setWidget(i, j + 1, itemTypeSyn);
					
					if (i % (grid.getRowCount() - 2) == 0) {
						j += 2;
						i = 1;
					}
					else {
						i++;
					}
				}
				
				okButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {

						for (int j = 1; j < grid.getColumnCount(); j += 2) {
							for (int i = 1; i < grid.getRowCount() - 1; i++) {
								TextBox newSyn = (TextBox)grid.getWidget(i, j);
								List<String> synList = new ArrayList<String>();
								String[] synNames = (newSyn.getText()).split(",");
								for (String syn : synNames) {
									if (syn.trim() != "") {
										synList.add(syn);
									}
								}
								int index = (i - 1) + 6 * (j/2);
								currentItemTypes.get(index).setSynonymNames(synList);
							}
						}
						
						AsyncCallback<Void> saveCallback = new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed to save synonyms!");
							}

							@Override
							public void onSuccess(Void result) {
								SynonymsWindow.this.hide();								
							}
							
						};
						GameServices.gameService.setSynonyms(currentItemTypes, saveCallback);
					}
				});
				grid.setWidget(7, 0, okButton);
				SynonymsWindow.this.containerPanel.add(grid);
				
			}
			
		};
		GameServices.gameService.getAllItemTypes(callback);
		this.setWidget(containerPanel);
	}
	
}
