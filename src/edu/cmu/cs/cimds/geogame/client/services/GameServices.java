/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.services;

import com.google.gwt.core.client.GWT;

public class GameServices {
	// Define necessary services
	final public static LoginServiceAsync loginService = (LoginServiceAsync)GWT.create(LoginService.class);
	final public static MessageServiceAsync messageService = (MessageServiceAsync)GWT.create(MessageService.class);
	final public static GameServiceAsync gameService = (GameServiceAsync)GWT.create(GameService.class);
	final public static DevServiceAsync devService = (DevServiceAsync)GWT.create(DevService.class);
	final public static TestServiceAsync testService = (TestServiceAsync)GWT.create(TestService.class);
	final public static AgentServiceAsync agentService = (AgentServiceAsync)GWT.create(AgentService.class);
}