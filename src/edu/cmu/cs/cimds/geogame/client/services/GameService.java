/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.services;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.cmu.cs.cimds.geogame.client.ActionResult;
import edu.cmu.cs.cimds.geogame.client.GameTime;
import edu.cmu.cs.cimds.geogame.client.MoveResult;
import edu.cmu.cs.cimds.geogame.client.ServerSettingsResult;
import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;
import edu.cmu.cs.cimds.geogame.client.model.dto.GameStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemTypeDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ServerSettingsStructDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;


@RemoteServiceRelativePath("GameService")
public interface GameService extends RemoteService {
	public GameStruct getGameInformation(UserDTO player) throws GeoGameException;
	public GameStruct getGameInformationNew(UserDTO player) throws GeoGameException;
	public LocationDTO getLocationInformation(UserDTO player, Long locationId) throws GeoGameException;
	public MoveResult moveToLocation(UserDTO player, Long locationId) throws GeoGameException;
	public MoveResult moveToLocationNew(UserDTO player, Long locationId) throws GeoGameException;
//	public ActionResult moveOnRoad(UserDTO player, Long roadId) throws GeoGameException;
	public ActionResult takeItem(UserDTO player, long itemId) throws GeoGameException;
	public ActionResult takeItemNew(UserDTO player, long itemId) throws GeoGameException;
//	public ActionResult sellItem(UserDTO player, long itemId, int price) throws GeoGameException;
//	public ActionResult sellCombo(UserDTO player, long comboId) throws GeoGameException;
//	public Integer getSalePrice(UserDTO player, long itemId) throws GeoGameException;
//	public ActionResult sendTradeOffer(UserDTO player, long player2Id, TradeOfferDTO tradeOfferDTO) throws GeoGameException;
//	public ActionResult replyTradeOffer(UserDTO player, long tradeOfferId, boolean accept) throws GeoGameException;
//	public ActionResult sendCommand(UserDTO player, String commandString) throws GeoGameException;
	public void startGameTimer(long gameDuration) throws GeoGameException;
	public void stopGameTimer() throws GeoGameException;
	public void startPeriodicGame() throws GeoGameException;
	public List<UserDTO> getAllPlayers() throws GeoGameException;
	public List<ItemTypeDTO> getAllItemTypes() throws GeoGameException;
	public GameTime getGameTime() throws GeoGameException;
	public void createUser(String username, String firstName, String lastName, String email, String password, boolean AMTVisitor) throws GeoGameException;
	public void createUserNetwork(List<UserDTO> playersInNetwork, Double graphDensity) throws GeoGameException;
	public void setSynonyms(List<ItemTypeDTO> itemTypes) throws GeoGameException;
	public List<List<Object>> getDistanceMap() throws GeoGameException;
	public Boolean sendFormAcceptance(UserDTO player, long id, Boolean result) throws GeoGameException;
	public ServerSettingsResult sendServerSettings(ServerSettingsStructDTO serverSettings) throws GeoGameException;
	public ServerSettingsStructDTO getServerSettings() throws GeoGameException;
	public String userGraphToJSON ();  
	public HashMap<String, String> getItemNameSynPairsForUser(long user) throws GeoGameException;
}