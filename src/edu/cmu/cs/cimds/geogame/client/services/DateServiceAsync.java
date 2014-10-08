/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;

public interface DateServiceAsync {
	public void getDate(AsyncCallback<Date> callback) throws GeoGameException;
}