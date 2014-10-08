package edu.cmu.cs.cimds.geogame.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cmu.cs.cimds.geogame.client.model.dto.AcceptanceFormDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;

/**
 * Client entry point for the LocationInformationWindow.
 *
 * @author Antonio Juarez <ajuarez@andrew.cmu.edu>
 */
public class FormsAdminWindow extends DialogBox {

//	Grid widgetGrid = new Grid();
//	Grid pairsGrid = new Grid(1,2);
		
	List<AcceptanceFormDTO> formsList = new ArrayList<AcceptanceFormDTO>();
	Map<AcceptanceFormDTO, TextArea> formContentMap = new HashMap<AcceptanceFormDTO, TextArea>();
	Grid formsGrid = new Grid(1,6);
	
	public FormsAdminWindow() {
		super(false,true);
		
		VerticalPanel containerPanel = new VerticalPanel();
		
		formsGrid.setBorderWidth(1);
		containerPanel.add(formsGrid);
		Button submitButton = new Button("Save changes");
		submitButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(int i=0;i<formsList.size();i++) {
					AcceptanceFormDTO formDTO = formsList.get(i);
					formDTO.setActive(((CheckBox)formsGrid.getWidget(i+1, 1)).getValue());
					formDTO.setContent(formContentMap.get(formDTO).getText());
				}
				GameServices.loginService.updateAcceptanceForms(formsList, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
//						Window.alert("Failure! - 7 - " + caught.getMessage());
//						Window.alert("error occurred in updateAcceptanceForms");
					}
					@Override
					public void onSuccess(Void result) {
//						Window.alert("Success!");
						FormsAdminWindow.this.refresh();
					}
				});
			}
		});
		containerPanel.add(submitButton);

		Button cancelButton = new Button("Cancel");
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FormsAdminWindow.this.hide();
			}
		});
		containerPanel.add(cancelButton);

		this.setWidget(containerPanel);
	}

	public void clearContent() {
		formsList.clear();
		formsGrid.clear();
		formsGrid.resize(1,6);
	}
	
	public void refresh() {
		this.clearContent();
		AsyncCallback<List<AcceptanceFormDTO>> callback = new AsyncCallback<List<AcceptanceFormDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<AcceptanceFormDTO> forms) {
				formContentMap.clear();
				
				FormsAdminWindow.this.formsList = forms;
				formsGrid.resize(forms.size()+1, 5);
				
				formsGrid.setText(0, 0, "Name");
				formsGrid.setText(0, 1, "Active");
				formsGrid.setText(0, 2, "Order");
				formsGrid.setText(0, 3, "Content");
				formsGrid.setText(0, 3, "Reset");
				int i=1;
				for(final AcceptanceFormDTO form : forms) {
					formsGrid.setText(i, 0, form.getName());
					final CheckBox activeCheckBox = new CheckBox();
					activeCheckBox.setValue(form.isActive());
//					activeCheckBox.addClickHandler(new ClickHandler() {
//						@Override
//						public void onClick(ClickEvent event) {
//							form.setActive(activeCheckBox.getValue());
//						}
//					});
					formsGrid.setWidget(i, 1, activeCheckBox);
					formsGrid.setText(i, 2, String.valueOf(form.getOrder()));
					VerticalPanel contentPanel = new VerticalPanel();
					Button toggleContentButton = new Button("Toggle");
					final TextArea contentArea = new TextArea();
					contentArea.setText(form.getContent());
					contentArea.setVisible(false);
					contentPanel.add(toggleContentButton);
					contentPanel.add(contentArea);
					toggleContentButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							contentArea.setVisible(!contentArea.isVisible());
						}
					});
					formsGrid.setWidget(i, 3, contentPanel);
					Button resetButton = new Button("Reset");
					resetButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							if(Window.confirm("Delete all confirmations for form '" + form.getName() + "'?")) {
								GameServices.loginService.deleteFormConfirmations(form.getId(), new AsyncCallback<Void> () {
									@Override
									public void onFailure(Throwable caught) {
										Window.alert("Request failed! - " + caught.getMessage());
									}
									@Override
									public void onSuccess(Void result) {
										FormsAdminWindow.this.refresh();
									}
								});
							}
						}
					});
					formsGrid.setWidget(i, 4, resetButton);
					formContentMap.put(form, contentArea);
					i++;
				}
			}
		};
		GameServices.loginService.getAllAcceptanceForms(callback);
	}
}