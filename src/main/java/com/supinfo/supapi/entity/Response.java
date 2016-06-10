package com.supinfo.supapi.entity;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

//Entité qui pemet le renvoie de tous JSON, NON_EMPTY prévient de n' envoyer que les cj-hamps non nuls ou vides

@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Response {
	private int html_status;
	private String html_message;
	
	private User user;
	private List<Station> stations;
	private List<Travel> travels;
	private Station station;
	private Reservation reservation;
	private List<Reservation> reservations;

	public int getHtml_status() {
		return html_status;
	}

	public void setHtml_status(int html_status) {
		this.html_status = html_status;
	}

	public String getHtml_message() {
		return html_message;
	}

	public void setHtml_message(String html_message) {
		this.html_message = html_message;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Station> getStations() {
		return stations;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}

	public List<Travel> getTravels() {
		return travels;
	}

	public void setTravels(List<Travel> travels) {
		this.travels = travels;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public List<Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
}
