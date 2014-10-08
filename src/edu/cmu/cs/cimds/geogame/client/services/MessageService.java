/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;
import edu.cmu.cs.cimds.geogame.client.model.dto.CommStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.GeoGameCommandDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.MessageDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

@RemoteServiceRelativePath("MessageService")
public interface MessageService extends RemoteService {
	public void sendMessage(UserDTO player, MessageDTO message) throws GeoGameException;
	public void sendMessageNew(UserDTO player, MessageDTO message) throws GeoGameException;
	public void sendAdminMessage(UserDTO admin, MessageDTO message) throws GeoGameException;
	public CommStruct getMessages(UserDTO player, Date minTimestamp, boolean needsDBRefresh) throws GeoGameException;
	public CommStruct getMessagesNew(UserDTO player, Date minTimestamp, boolean needsDBRefresh) throws GeoGameException;
	public void sendAdminCommand (UserDTO admin, GeoGameCommandDTO command) throws GeoGameException;
	public GeoGameCommandDTO getAdminCommand(UserDTO player) throws GeoGameException;
}