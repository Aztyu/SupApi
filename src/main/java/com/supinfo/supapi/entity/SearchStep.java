package com.supinfo.supapi.entity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class SearchStep {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@OneToOne
	private Travel allerTravel;
	
	@OneToOne
	private Travel retourTravel;
	
	@JsonBackReference
	@ManyToOne
    @JoinColumn(name = "start_id", insertable =  false, updatable = false)
	private Station start;
	
	private Timestamp start_time;
	
	@JsonBackReference
	@ManyToOne
    @JoinColumn(name = "end_id", insertable =  false, updatable = false)
	private Station end;
	
	private Timestamp end_time;
	
	@ManyToOne
	private TrainTrip train_trip;
	
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
