/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.cmu.cs.cimds.geogame.client.DisplayResult;
import edu.cmu.cs.cimds.geogame.client.LoginResult;
import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;
import edu.cmu.cs.cimds.geogame.client.model.dto.AcceptanceFormDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

@RemoteServiceRelativePath("LoginService")
public interface LoginService extends RemoteService {
	public LoginResult sendLogin(String username, String password) throws GeoGameException;
	public LoginResult sendAdminLogin(String username, String password) throws GeoGameException;
	public LoginResult sendRefreshLogin(UserDTO player) throws GeoGameException;
	public Boolean sendLogout(UserDTO player) throws GeoGameException;
//	public AcceptanceFormDTO getNextAcceptanceForm(UserDTO player) throws GeoGameException;
	public DisplayResult requestNextDisplay(UserDTO player) throws GeoGameException;
	public DisplayResult requestPeekDisplay(UserDTO player, long peekFormId) throws GeoGameException;
	public DisplayResult requestNextAdminDisplay(UserDTO admin) throws GeoGameException;
	public List<AcceptanceFormDTO> getAllAcceptanceForms() throws GeoGameException;
	public void updateAcceptanceForms(List<AcceptanceFormDTO> updateForms) throws GeoGameException;
	public void deleteFormConfirmations(Long formId) throws GeoGameException;
}