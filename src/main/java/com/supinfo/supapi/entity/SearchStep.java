package com.supinfo.supapi.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@Entity
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class SearchStep {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne
    @JoinColumn(name = "start_id", insertable =  false, updatable = false)
	Station start;
	
	Timestamp start_time;
	
	@ManyToOne
    @JoinColumn(name = "end_id", insertable =  false, updatable = false)
	Station end;
	
	Timestamp end_time;
	
	@ManyToOne
    @JoinColumn(name = "trip_id", insertable =  false, updatable = false)
	TrainTrip train_trip;
	
	double price;
	
	int time;

	public Station getStart() {
		return start;
	}

	public void setStart(Station start) {
		this.start = new Station(start);
		this.start.setLines(null);
	}

	public Timestamp getStart_time() {
		return start_time;
	}

	public void setStart_time(Date date) {
		this.start_time = new Timestamp(date.getTime());
	}

	public Station getEnd() {
		return end;
	}

	public void setEnd(Station end) {
		this.end = new Station(end);
		this.end.setLines(null);
	}

	public Timestamp getEnd_time() {
		return end_time;
	}

	public void setEnd_time(Date date) {
		this.end_time = new Timestamp(date.getTime());
	}

	public TrainTrip getTrain_trip() {
		return train_trip;
	}

	public void setTrain_trip(TrainTrip train_trip) {
		this.train_trip = train_trip;
	}

	public void setStart_time(Timestamp start_time) {
		this.start_time = start_time;
	}

	public void setEnd_time(Timestamp end_time) {
		this.end_time = end_time;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPrice() {
		return this.price;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
