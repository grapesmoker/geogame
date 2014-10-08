/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.cmu.cs.cimds.geogame.client.exception.GeoGameException;

@RemoteServiceRelativePath("DateService")
public interface DateService extends RemoteService {
	public Date getDate() throws GeoGameException;
}