/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.services;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.cmu.cs.cimds.geogame.client.ActionResult;
import edu.cmu.cs.cimds.geogame.client.GameTime;
import edu.cmu.cs.cimds.geogame.client.MoveResult;
import edu.cmu.cs.cimds.geogame.client.ServerSettingsResult;
import edu.cmu.cs.cimds.geogame.client.model.dto.GameStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ServerSettingsStructDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

public interface GameServiceAsync {
	public void getGameInformation(UserDTO player, AsyncCallback<GameStruct> asyncCallback);
	public void getGameInformationNew(UserDTO player, AsyncCallback<GameStruct> callback);
	public void getLocationInformation(UserDTO player, Long locationId, AsyncCallback<LocationDTO> asyncCallback);
	public void moveToLocation(UserDTO player, Long locationId, AsyncCallback<MoveResult> asyncCallback);
	public void moveToLocationNew(UserDTO player, Long locationId, AsyncCallback<MoveResult> asyncCallback);
//	public void moveOnRoad(UserDTO player, Long roadId, AsyncCallback<ActionResult> asyncCallback);
	public void takeItem(UserDTO player, long itemId, AsyncCallback<ActionResult> asyncCallback);
	public void takeItemNew(UserDTO player, long itemId, AsyncCallback<ActionResult> asyncCallback);
//	public void sellItem(UserDTO player, long itemId, int price, AsyncCallback<ActionResult> asyncCallback);
//	public void sellCombo(UserDTO player, long comboId, AsyncCallback<ActionResult> asyncCallback);
//	public void getSalePrice(UserDTO player, long itemId, AsyncCallback<Integer> asyncCallback);
//	public void sendTradeOffer(UserDTO player, long player2Id, TradeOfferDTO tradeOfferDTO, AsyncCallback<ActionResult> asyncCallback);
//	public void replyTradeOffer(UserDTO player, long tradeOfferId, boolean accept, AsyncCallback<ActionResult> asyncCallback);
//	public void sendCommand(UserDTO player, String commandString, AsyncCallback<ActionResult> asyncCallback);
	public void startGameTimer(long gameDuration, AsyncCallback<Void> callback);
	public void stopGameTimer(AsyncCallback<Void> callback);
	public void startPeriodicGame(AsyncCallback<Void> callback);
	public void getAllPlayers(AsyncCallback<List<UserDTO>> callback);
	public void getAllItemTypes(AsyncCallback<List<ItemTypeDTO>> callback);
	public void getGameTime(AsyncCallback<GameTime> callback);
	public void createUser(String username, String firstName, String lastName, String email, String password, boolean AMTVisitor, AsyncCallback<Void> callback);
	public void createUserNetwork(List<UserDTO> playersInNetwork, Double graphDensity, AsyncCallback<Void> asyncCallback);
	public void setSynonyms(List<ItemTypeDTO> itemTypes, AsyncCallback<Void> callback);
	public void getDistanceMap(AsyncCallback<List<List<Object>>> asyncCallback);
	public void sendFormAcceptance(UserDTO player, long id, Boolean result, AsyncCallback<Boolean> callback);
	public void sendServerSettings(ServerSettingsStructDTO serverSettings, AsyncCallback<ServerSettingsResult> callback);
	public void getServerSettings(AsyncCallback<ServerSettingsStructDTO> callback);
	public void userGraphToJSON(AsyncCallback<String> callback);
	public void getItemNameSynPairsForUser(long user, AsyncCallback<HashMap<String, String>> callback);

}