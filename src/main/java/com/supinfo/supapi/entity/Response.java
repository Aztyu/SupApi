package com.supinfo.supapi.entity;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Response {
	private int html_status;
	private String html_message;
	
	private User user;
	private List<Station> stations;

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
}
