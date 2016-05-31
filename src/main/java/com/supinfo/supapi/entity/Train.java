package com.supinfo.supapi.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Train {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	private long line_id;
	
	private long seats;
	
	@OneToMany(mappedBy="train")
	private List<TrainTrip> trips;

	public long getLine_id() {
		return line_id;
	}

	public void setLine_id(long line_id) {
		this.line_id = line_id;
	}

	public long getSeats() {
		return seats;
	}

	public void setSeats(long seats) {
		this.seats = seats;
	}

	public List<TrainTrip> getTrips() {
		return trips;
	}

	public void setTrips(List<TrainTrip> trips) {
		this.trips = trips;
	}
}
