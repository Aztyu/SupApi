package com.supinfo.supapi.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.codehaus.jackson.annotate.JsonBackReference;

@Entity
public class Reservation {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@JsonBackReference
	@ManyToOne
	private User user;
	
	@OneToOne(cascade = {CascadeType.ALL})
	private Travel travel;
	
	private boolean paid;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Travel getTravel() {
		return travel;
	}

	public void setTravel(Travel travel) {
		this.travel = travel;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}
}
