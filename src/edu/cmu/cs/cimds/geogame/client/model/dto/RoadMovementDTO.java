package edu.cmu.cs.cimds.geogame.client.model.dto;

import java.io.Serializable;
import java.util.Date;

import edu.cmu.cs.cimds.geogame.client.model.db.RoadMovement;


public class RoadMovementDTO implements Serializable {
	
	private static final long serialVersionUID = 2651763218888369106L;

	public enum Status implements Serializable {
		ACTIVE ("Active"),
		COMPLETE ("Complete");
		
		private String descriptor;
		private Status(String descriptor) {
			this.descriptor = descriptor;
		}
		
		@Override
		public String toString() {
			return this.descriptor;
		}
	}
	
	private long id;
	private RoadDTO road;
	private boolean forward;
	private Date moveStart;
	private int duration;
	private Status status = Status.ACTIVE;
	private double speed;
	private double slope;
	private int segment;
	
//	private long timeLeft;
	
	public RoadMovementDTO() {}

	public RoadMovementDTO(RoadMovement roadMovement) {
		this.id = roadMovement.getId();
		this.duration = roadMovement.getDuration();
		this.road = new RoadDTO(roadMovement.getRoad());
		this.moveStart = roadMovement.getMoveStart();
//		this.timeLeft = roadMovement.getMoveStart().getTime() + roadMovement.getDuration() - new Date().getTime();
		this.forward = roadMovement.isForward();
		switch(roadMovement.getStatus()) {
			case ACTIVE:	this.status = Status.ACTIVE;
							break;
			case COMPLETE:	this.status = Status.COMPLETE;
							break;
		}
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public RoadDTO getRoad() { return road; }
	public void setRoad(RoadDTO road) { this.road = road; }
	
	public boolean isForward() { return forward; }
	public void setForward(boolean forward) { this.forward = forward; }
	
	public Date getMoveStart() { return moveStart; }
	public void setMoveStart(Date moveStart) { this.moveStart = moveStart; }
	
	public int getDuration() { return duration; }
	public void setDuration(int duration) { this.duration = duration; }
	
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }

	public double getSpeed() { return speed; }
	public void setSpeed(double speed) { this.speed = speed; }

	public double getSlope() { return slope; }
	public void setSlope(double slope) { this.slope = slope; }

	public int getSegment() { return segment; }
	public void setSegment(int segment) { this.segment = segment; }

	public long getTimeLeft() {
		return this.getMoveStart().getTime() + this.getDuration() - new Date().getTime();
	}
//	@SuppressWarnings("unused")
//	private void setTimeLeft(long timeLeft) { this.timeLeft = timeLeft; }
	
	public LocationDTO getSource() {
		if(this.isForward()) {
			return this.road.getLocation1();
		} else {
			return this.road.getLocation2();
		}
	}

	public LocationDTO getDestination() {
		if(this.isForward()) {
			return this.road.getLocation2();
		} else {
			return this.road.getLocation1();
		}
	}
	
//	public void update() {
//		this.timeLeft = this.getMoveStart().getTime() + this.getDuration() - new Date().getTime();
//	}
}