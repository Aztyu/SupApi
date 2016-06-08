package com.supinfo.supapi.entity;

import java.sql.Timestamp;
import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class SearchStep {
	Station start;
	Timestamp start_time;
	Station end;
	Timestamp end_time;
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
