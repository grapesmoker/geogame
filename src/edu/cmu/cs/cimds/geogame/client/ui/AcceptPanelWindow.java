package edu.cmu.cs.cimds.geogame.client.ui;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import edu.cmu.cs.cimds.geogame.client.DisplayResult;
import edu.cmu.cs.cimds.geogame.client.GameInfo;
import edu.cmu.cs.cimds.geogame.client.model.dto.AcceptanceFormDTO;
import edu.cmu.cs.cimds.geogame.client.services.GameServices;


/**
 * @author Antonio Juarez <ajuarez@andrew.cmu.edu>
 */
public class AcceptPanelWindow extends DialogBox {

	private long formId;
	private AcceptPanel acceptPanel = new AcceptPanel();
	private Button hideButton = new Button();

	public AcceptPanel getAcceptPanel() {
		return acceptPanel;
	}
	
	
	
	public AcceptPanelWindow() {
		super(true,false);
		this.setWidget(acceptPanel);
	}

	public void init() {
		AsyncCallback<DisplayResult> callback = new AsyncCallback<DisplayResult>() {

			@Override
			public void onFailure(Throwable caught) {
				acceptPanel.setContent("Could not load form - " + caught.getMessage());
			}

			@Override
			public void onSuccess(DisplayResult result) {
				AcceptanceFormDTO form = result.getAcceptanceForm();
				acceptPanel.setContent(form.getContent());
				acceptPanel.setButton1Text(form.getButton1Text());
				acceptPanel.setButton2Text(form.getButton2Text());
				acceptPanel.setCallback(new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						AcceptPanelWindow.this.hide();
					}
					@Override
					public void onSuccess(Boolean result) {
						AcceptPanelWindow.this.hide();
					}
				});
			}
		};
		
		GameServices.loginService.requestPeekDisplay(GameInfo.getInstance().getPlayer(), formId, callback);
		
		hideButton.addClickHandler (new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AcceptPanelWindow.this.hide();
			}
		});
		
		hideButton.setVisible(false);
		hideButton.setText("Close this window");
		acceptPanel.add(hideButton);
	}

	public long getFormId() { return formId; }
	public void setFormId(long formId) { this.formId = formId; }
	
	public void showHideButton() {
		acceptPanel.yesButton.setVisible(false);
		acceptPanel.noButton.setVisible(false);
		hideButton.setVisible(true);
	}
}