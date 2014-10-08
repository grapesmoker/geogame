package edu.cmu.cs.cimds.geogame.client;

import java.io.Serializable;

import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;

/**
 *
 * @author ajuarez
 */
public class MoveResult extends ActionResult implements Serializable {

	private static final long serialVersionUID = -8272028977463843100L;
	
	private int duration;
	private LocationDTO source;
	private LocationDTO destination;
	
	public int getDuration() { return duration; }
	public void setDuration(int duration) { this.duration = duration; }
	
	public LocationDTO getSource() { return source; }
	public void setSource(LocationDTO source) { this.source = source; }

	public LocationDTO getDestination() { return destination; }
	public void setDestination(LocationDTO destination) { this.destination = destination; }
}