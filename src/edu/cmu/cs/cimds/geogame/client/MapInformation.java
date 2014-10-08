package edu.cmu.cs.cimds.geogame.client;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.cimds.geogame.client.model.dto.LocationDTO;
import edu.cmu.cs.cimds.geogame.client.model.dto.RoadDTO;

/**
 *
 * @author Antonio
 */
public class MapInformation implements Serializable {

	private static final long serialVersionUID = -6794355745529894079L;
	
	public Set<LocationDTO> locations = new HashSet<LocationDTO>();
	public Set<RoadDTO> roads = new HashSet<RoadDTO>();
}