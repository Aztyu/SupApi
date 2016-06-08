package com.supinfo.supapi.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.supinfo.supapi.entity.association.StationLineAssociation;


@Entity
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class Line {
	@Id
	private long id;	
	private String reference;
	private String name;
	private Double avg_speed;
	private double price;
	
	@OneToMany(mappedBy="line", cascade = CascadeType.ALL)
	private List<StationLineAssociation> stations;
	
	
	public Line(){
		this.id = -1;
		this.name = new String();
		this.reference = new String();
		this.avg_speed = 0.0;
		this.stations = new ArrayList<StationLineAssociation>(); 
	}
	
	public Line(int id, String reference, String name, double avg_speed, double price){
		this.id = id;
		this.name = name;
		this.reference = reference;
		this.avg_speed = avg_speed;
		this.price = price;
		this.stations = new ArrayList<StationLineAssociation>(); 
	}
	
	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getAvg_speed() {
		return avg_speed;
	}

	public void setAvg_speed(Double avg_speed) {
		this.avg_speed = avg_speed;
	}

	public List<StationLineAssociation> getStations() {
		return stations;
	}

	public void setStations(List<StationLineAssociation> stations) {
		this.stations = stations;
	}

	public void addStation(Station station, int station_order, long distance){
		StationLineAssociation sla = new StationLineAssociation();
		sla.setLine(this);
		sla.setStation(station);
		sla.setStation_order(station_order);
		sla.setDistance(distance);
		
		this.stations.add(sla);
		station.getLines().add(sla);
	}
	
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		if(arg0 instanceof Line){
			Line other = (Line)arg0;
			return other.getId() == this.id;
		}
		return false;
	}
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
