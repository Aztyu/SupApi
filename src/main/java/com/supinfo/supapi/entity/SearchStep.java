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

	public Station getStart() {
		return start;
	}

	public void setStart(Station start) {
		start.setLines(null);
		this.start = start;
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
		end.setLines(null);
		this.end = end;
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
}
