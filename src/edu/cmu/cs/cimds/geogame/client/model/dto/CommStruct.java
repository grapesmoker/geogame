package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Antonio
 */
public class CommStruct implements Serializable {

	private static final long serialVersionUID = -4394590720201822815L;

	public CommStruct() {}
	
	public List<GeoGameCommandDTO> commands = new ArrayList<GeoGameCommandDTO>();
	public List<MessageDTO> messages = new ArrayList<MessageDTO>();
	public UserDTO player;
	public boolean gameStarted;
	public boolean gameFinished;
	public long timeRemaining;
	public long gameDuration;
	public boolean logOffPlayer;
	public List<UserDTO> scorePlayers;
	public List<ScoreMessage> scoreMessages;
	public Date timestamp;
//	public List<TradeOfferDTO> tradeOffers = new ArrayList<TradeOfferDTO>();
}