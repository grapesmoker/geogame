package edu.cmu.cs.cimds.geogame.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.cmu.cs.cimds.geogame.client.LoginResult;
import edu.cmu.cs.cimds.geogame.client.MoveResult;
import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;
import edu.cmu.cs.cimds.geogame.client.model.dto.CommStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.GameStruct;
import edu.cmu.cs.cimds.geogame.client.model.dto.ItemDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.MessageDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.ScoreMessage;
import edu.cmu.cs.cimds.geogame.client.model.dto.UserDTO;

public class TCPThread extends Thread {

	private Socket socket;
	private long threadIndex;
	private boolean listen = true;
	private UserDTO player;
	
	private ConcurrentLinkedQueue<String> incomingMessages = new ConcurrentLinkedQueue<String>();
	private ConcurrentLinkedQueue<String> outgoingMessages = new ConcurrentLinkedQueue<String>();
	
	public TCPThread(Socket connection) {
		this.socket = connection;
	}
	
	public TCPThread(Socket connection, long count) {
		this.socket = connection;
		this.threadIndex = count;
	}

	public TCPThread(Runnable clientConnection) {
		// TODO Auto-generated constructor stub
		super(clientConnection);
	}

	@Override
	public void run() {
		
		try {
			PrintWriter outputWriter = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			while (socket.isConnected()) {
				// execute any commands we might receive from agents
				if (inputReader.ready()) {
					String command = inputReader.readLine();
					incomingMessages.add(command);
					processCommand(command);
					System.out.println(command);
				}

				// if we have arrived somewhere, inform the agent of this and give them some information about what's there
				if (player != null) {
					player.setLastRequest(new Date());
					player.updateSeemsActive();
					
					if (GameServiceImpl.hasUserArrived(player)) {
						sendLocationInfo();
					}
					// update our messages
					CommStruct commStruct = MessageServiceImpl.getAgentMessages(player, new Date(), true);
					this.player = commStruct.player;
					for (ScoreMessage scoreMessage : commStruct.scoreMessages) {
						addOutgoingMessage("SCORE_INCREASE: " + scoreMessage.getScoreObtained() + ";");
					}
					for (MessageDTO message : commStruct.messages) {
						addOutgoingMessage("MESSAGE: " + message.getContent() + ";");
					}
				}
				
				// send agents any messages that we have accumulated for them
				if (!outgoingMessages.isEmpty()) {
					outputWriter.println(outgoingMessages.poll());
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getIncomingMessage() {
		return incomingMessages.poll();
	}
	
	public ConcurrentLinkedQueue<String> getIncomingMessages() {
		return incomingMessages;
	}
	
	public void addOutgoingMessage(String message) {
		outgoingMessages.add(message);
	}
	
	public String getNextLoginCommand() {
		Iterator<String> commandIterator = incomingMessages.iterator();
		while (commandIterator.hasNext()) {
			String command = commandIterator.next();
			if (command.startsWith("LOGIN")) {
				commandIterator.remove();
				return command;
			}
		}
		return "";
	}
	
	public boolean validateCommand(String command) {
		// validator for the command syntax
		// possible commands:
		// 1. LOGIN <USER> <PASSWORD>
		// 2. MOVE <LOCATION>
		// 3. PICKUP <ITEM>
		// 4. SEND <MESSAGE>
		// 5. INFO
		// 6. LOGOUT
		
		String [] cmdArgs = command.split("\\s");
		System.out.println(cmdArgs);
		
		if (cmdArgs[0].contentEquals("LOGIN") && cmdArgs[1] != "" && cmdArgs[2] != "") {
			return true;
		}
		
		if (cmdArgs[0].contentEquals("MOVE") || cmdArgs[0].contentEquals("PICKUP")) {
			if (cmdArgs[1] != "") {
				try {
					Integer.parseInt(cmdArgs[1]);
				} catch (NumberFormatException ex) {
					return false;
				}
				return true;
			}
			else {
				return false;
			}
		}
		
		if (cmdArgs[0].contentEquals("SEND") && cmdArgs[1] != "") {
			return true;
		}
		
		if (cmdArgs[0].equals("INFO")) {
			return true;
		}
		
		return false;
	}
	
	public void processCommand (String command) {
		if (validateCommand(command)) {
			System.out.println(command);
			String [] cmdArgs = command.split("\\s");
			
			// handle the LOGIN case
			
			if (cmdArgs[0].contentEquals("LOGIN")) {
				try {
					LoginResult result = LoginServiceImpl.sendAgentLogin(cmdArgs[1], cmdArgs[2]);
					if (result.isSuccess()) {
						GameServiceImpl.threadMap.remove(this.threadIndex);
						GameServiceImpl.threadMap.put(result.getPlayer().getId(), this);
						addOutgoingMessage("LOGIN SUCCESSFUL;USER_ID: " + result.getPlayer().getId() + ";");
						this.player = result.getPlayer();
					}
					else {
						addOutgoingMessage("INVALID LOGIN");
					}
				} catch (GeoGameException ex) {
					ex.printStackTrace();
					addOutgoingMessage("LOGIN FAILED: " + ex.getMessage());
				}
			}
			
			// handle the MOVE case
			
			if (cmdArgs[0].contentEquals("MOVE")) {
				moveAgent(Long.parseLong(cmdArgs[1]));
			}
			
			// get information on our current location
			
			if (cmdArgs[0].equals("INFO")) {
				sendLocationInfo();
			}
		}
	}
	
	public void sendLocationInfo () {
		try {
			LocationDTO locationInfo = GameServiceImpl.getAgentLocationInformation(player, player.getCurrentLocation().getId());
			GameStruct gameInfo = GameServiceImpl.getAgentGameInformation(player);
			
			String locationInfoString = "ARRIVED: " + locationInfo.getName() + ";" +
				"ITEMS: ";
			
			for (ItemDTO item : locationInfo.getItems()) {
				locationInfoString += item.getId();
			}
			locationInfoString += ";AVAIL_LOCATIONS: ";
			
			for (LocationDTO availableLocation: gameInfo.mapInformation.locations) {
				locationInfoString += availableLocation.getId() + ",";
			}
			locationInfoString = locationInfoString.substring(0, locationInfoString.length() - 1) + ";";
			addOutgoingMessage(locationInfoString);
		} catch (GeoGameException ex) {
			ex.printStackTrace();
			addOutgoingMessage("GETTING INFORMATION FAILED: " + ex.getMessage());
		}
	}
	
	public void moveAgent (Long location) {
		try {
			MoveResult result = GameServiceImpl.moveAgentToLocation(player, location);
			if (result.isSuccess()) {
				addOutgoingMessage("MOVE SUCCESSFUL;" +
						"DESTINATION: " + result.getDestination().getId() + ";" +
						"DURATION: " + result.getDuration() + ";");
				
			}
			else {
				addOutgoingMessage("MOVE FAILED");
			}
		} catch (GeoGameException ex) {
			ex.printStackTrace();
			addOutgoingMessage("MOVE FAILED: " + ex.getMessage());
		}
	}
}
