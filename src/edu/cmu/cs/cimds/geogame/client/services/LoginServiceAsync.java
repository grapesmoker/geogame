package edu.cmu.cs.cimds.geogame.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.cmu.cs.cimds.geogame.client.DisplayResult;
import edu.cmu.cs.cimds.geogame.client.LoginResult;
import edu.cmu.cs.cimds.geogame.client.model.dto.AcceptanceFormDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

public interface LoginServiceAsync {
    public void sendLogin(String username, String password, AsyncCallback<LoginResult> asyncCallback);
    public void sendAdminLogin(String username, String password, AsyncCallback<LoginResult> asyncCallback);
    public void sendRefreshLogin(UserDTO player, AsyncCallback<LoginResult> asyncCallback);
	public void sendLogout(UserDTO player, AsyncCallback<Boolean> asyncCallback);
//	public void getNextAcceptanceForm(UserDTO player, AsyncCallback<AcceptanceFormDTO> asyncCallback);
	public void requestNextDisplay(UserDTO player, AsyncCallback<DisplayResult> asyncCallback);
	public void requestPeekDisplay(UserDTO player, long peekFormId, AsyncCallback<DisplayResult> asyncCallback);
	public void getAllAcceptanceForms(AsyncCallback<List<AcceptanceFormDTO>> callback);
	public void updateAcceptanceForms(List<AcceptanceFormDTO> updateForms, AsyncCallback<Void> callback);
	public void deleteFormConfirmations(Long formId, AsyncCallback<Void> callback);
	public void requestNextAdminDisplay(UserDTO admin, AsyncCallback<DisplayResult> callback);
}