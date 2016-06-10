package com.supinfo.supapi.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.supinfo.supapi.enumeration.Sens;

@Entity
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Travel {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;	
	
	@OneToMany(mappedBy = "allerTravel", cascade = {CascadeType.ALL})
	private List<SearchStep> aller;

	@OneToMany(mappedBy = "retourTravel",cascade = {CascadeType.ALL})
	private List<SearchStep> retour;
	
	@OneToOne
	private Reservation reservation;
	
	@Transient
	private int minutes_aller;
	
	@Transient
	private int minutes_retour;
	
	@Transient
	private double price;

	public List<SearchStep> getAller() {
		return aller;
	}

	public void setAller(List<SearchStep> aller) {
		this.aller = aller;
	}

	public List<SearchStep> getRetour() {
		return retour;
	}

	public void setRetour(List<SearchStep> retour) {
		this.retour = retour;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public int getMinutes_aller() {
		return minutes_aller;
	}

	public void setMinutes_aller(int minutes_aller) {
		this.minutes_aller = minutes_aller;
	}

	public int getMinutes_retour() {
		return minutes_retour;
	}

	public void setMinutes_retour(int minutes_retour) {
		this.minutes_retour = minutes_retour;
	}
	
	

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public void calculatePrice(){
		double price = 0.0;
		for(SearchStep ss : aller){
			price += ss.getPrice();
		}
		if(!retour.isEmpty()){
			price *= 2;
		}
		this.price = price;
	}
	
	public void calculateTime(){
		int time = 0;
		for(SearchStep ss : aller){
			time += ss.getTime();
		}
		minutes_aller = time;
		
		time = 0;
		if(retour != null){
			for(SearchStep ss : retour){
				time += ss.getTime();
			}
		}
		minutes_retour = time;
	}
}
