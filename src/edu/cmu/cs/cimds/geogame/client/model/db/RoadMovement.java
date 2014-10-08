/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.cmu.cs.cimds.geogame.client.model.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name="road_movement")
public class RoadMovement extends PersistentEntity {
	
	private static final long serialVersionUID = 952823332683523754L;
	
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
	
	private Road road;
	private boolean forward;
	private Date moveStart;
	private int duration;
	private Status status = Status.ACTIVE;
	private User user;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="road_id")
	public Road getRoad() { return road; }
	public void setRoad(Road road) { this.road = road; }
	
	@Column(name="forward")
	public boolean isForward() { return forward; }
	public void setForward(boolean forward) { this.forward = forward; }
	
	@Column(name="move_start")
	public Date getMoveStart() { return moveStart; }
	public void setMoveStart(Date moveStart) { this.moveStart = moveStart; }
	
	@Column(name="duration")
	public int getDuration() { return duration; }
	public void setDuration(int duration) { this.duration = duration; }
	
	@Column(name="status")
	public Status getStatus() { return status; }
	public void setStatus(Status status) { this.status = status; }

	@ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="user_id")
	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }

	@Transient
	public Location getSource() {
		if(this.isForward()) {
			return this.road.getLocation1();
		} else {
			return this.road.getLocation2();
		}
	}

	@Transient
	public Location getDestination() {
		if(this.isForward()) {
			return this.road.getLocation2();
		} else {
			return this.road.getLocation1();
		}
	}
}