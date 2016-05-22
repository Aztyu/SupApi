package com.supinfo.supapi.entity.association;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.supinfo.supapi.entity.Line;
import com.supinfo.supapi.entity.Station;

@Entity
public class StationLineAssociation {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	private Station station;
  
	@ManyToOne
	private Line line;

	private long station_order;

	public long getStation_order() {
		return station_order;
	}

	public void setStation_order(long station_order) {
		this.station_order = station_order;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	
}
