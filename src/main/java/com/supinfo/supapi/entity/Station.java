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
	
	@OneToMany(mappedBy="station")
	private List<StationLineAssociation> lines;
	
	public Station(){
		this.id = -1;
		this.name = new String();
		this.address = new String();
		this.lines = new ArrayList<StationLineAssociation>();
		
	}
	
	public Station(long id, String name, String address){
		this.id = id;
		this.name = name;
		this.address = address;
		this.lines = new ArrayList<StationLineAssociation>();
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
	
	public List<StationLineAssociation> getLines() {
		return lines;
	}

	public void setLines(List<StationLineAssociation> lines) {
		this.lines = lines;
	}
}
