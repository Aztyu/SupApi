package com.supinfo.supapi.entity;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.supinfo.supapi.enumeration.Sens;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Travel {
	private List<SearchStep> aller;
	
	private List<SearchStep> retour;
	
	private int minutes_aller;
	
	private int minutes_retour;
	
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

	public void calculatePrice(){
		double price = 0.0;
		for(SearchStep ss : aller){
			price += ss.getPrice();
		}
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
