package edu.cmu.cs.cimds.geogame.client.model.enums;

public enum ActionType {
	LOGIN("Login"),							//0
	LOGOUT ("Logout"),						//1
	MOVE ("Move"),							//2
	PURCHASE ("Purchase"),					//3
	SALE ("Sale"),							//4
	NEW_GOAL_ITEM ("New Goal Item"),		//5
	NEW_ITEM_CREATED ("New Item Created"),	//6
	DB_RESET ("DB Reset"),					//7
	GAME_START ("Game Start"),				//8
	GAME_END ("Game End"),					//9
	SCORE_INCREASE ("Score Increase"),		//10
	LOGIN_REFRESH ("Login Refresh");		//11
	
	private String descriptor;
	private ActionType(String descriptor) {
		this.descriptor = descriptor;
	}
	
	@Override
	public String toString() {
		return this.descriptor;
	}
}