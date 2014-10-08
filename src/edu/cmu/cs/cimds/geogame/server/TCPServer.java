package edu.cmu.cs.cimds.geogame.server;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import edu.cmu.cs.cimds.geogame.server.GameServiceImpl;

public class TCPServer implements Runnable {
	
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private Queue<String> incomingMessages;
	private Queue<String> outgoingMessages;
	private int threadID;
	private long count = 0;

	private final int DEFAULT_PORT = 9500;
	
	@Override
	public void run () {
		// this is the method that sets up the server
		// only needs to be run once
		
		
		try {
			serverSocket = new ServerSocket(DEFAULT_PORT);
		
			while (true) {
			
				Socket connection = serverSocket.accept();
					
				TCPThread clientThread = new TCPThread(connection, count);
				//TCPThread clientThread = new TCPThread(clientConnection);
				clientThread.start();
				
				GameServiceImpl.threadMap.put(count, clientThread);
				count++;
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public TCPServer(Socket clientSocket, int count) {
		this.incomingMessages = new ConcurrentLinkedQueue<String>();
		this.outgoingMessages = new ConcurrentLinkedQueue<String>();
		this.threadID = count;
		this.clientSocket = clientSocket;
	}
	
	public TCPServer() {
		// TODO Auto-generated constructor stub
		//this.init(9500);
		incomingMessages = new ConcurrentLinkedQueue<String>();
		outgoingMessages = new ConcurrentLinkedQueue<String>();
	}
	
	public String getNextCommand() {
		return GameServiceImpl.getIncomingMessages().poll();
	}
	
	public void setNextResponse (String response) {
		outgoingMessages.add(response);
	}
}
