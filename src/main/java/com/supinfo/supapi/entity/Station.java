package com.supinfo.supapi.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.supinfo.supapi.entity.association.StationLineAssociation;


@Entity
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "id")
public class Station {
	@Id
	private long id;
	
	private String name;
	private String address;
	private String zipcode;
	private String city;
	private String country;
	private double latitude;
	private double longitude;
	

	@OneToMany(mappedBy="station")
	private List<StationLineAssociation> lines;
	
	public Station(){
		this.id = -1;
		this.name = new String();
		this.address = new String();
		this.zipcode = new String();
		this.city = new String();
		this.country = new String();
		this.lines = new ArrayList<StationLineAssociation>();
	}
	
	public Station(long id, String name, String address, String zipcode, String city, String country, double latitude, double longitude){
		this.id = id;
		this.name = name;
		this.address = address;
		this.zipcode = zipcode;
		this.city = city;
		this.country = country;
		this.lines = new ArrayList<StationLineAssociation>();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public List<StationLineAssociation> getLines() {
		return lines;
	}

	public void setLines(List<StationLineAssociation> lines) {
		this.lines = lines;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getStationOrder(long line_id) {
		long station_order = -1;
		for(int i = lines.size()-1; i>=0; i--){
			if(lines.get(i).getLine().getId() == line_id){
				return lines.get(i).getStation_order();
			}
		}
		return station_order;
	}
}
