/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.cmu.cs.cimds.geogame.client.model.dto.CommStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.GeoGameCommandDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.MessageDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

public interface MessageServiceAsync {
	public void sendMessage(UserDTO player, MessageDTO message, AsyncCallback<Void> asyncCallback);
	public void sendMessageNew(UserDTO player, MessageDTO message, AsyncCallback<Void> asyncCallback);
	public void sendAdminMessage(UserDTO admin, MessageDTO message, AsyncCallback<Void> asyncCallback);
	public void getMessages(UserDTO player, Date minTimestamp, boolean needsDBRefresh, AsyncCallback<CommStruct> asyncCallback);
	public void getMessagesNew(UserDTO player, Date minTimestamp, boolean needsDBRefresh, AsyncCallback<CommStruct> asyncCallback);
	public void sendAdminCommand(UserDTO admin, GeoGameCommandDTO command, AsyncCallback<Void> asyncCallback);
	public void getAdminCommand(UserDTO player, AsyncCallback<GeoGameCommandDTO> asyncCallback);
}