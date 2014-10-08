package edu.cmu.cs.cimds.geogame.client;

import java.io.Serializable;

public class GameTime implements Serializable {
	
	private static final long serialVersionUID = -8310663947041459420L;
	
	public boolean gameStarted;
	public boolean gameFinished;
	public long timeRemaining;
	public long gameDuration;
	
	public GameTime() {};
}