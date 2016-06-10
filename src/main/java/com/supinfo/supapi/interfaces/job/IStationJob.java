package com.supinfo.supapi.interfaces.job;

import java.util.List;

import com.supinfo.supapi.entity.Reservation;
import com.supinfo.supapi.entity.Station;
import com.supinfo.supapi.entity.Travel;

public interface IStationJob {
	public List<Station> searchStationByName(String search);
	public List<Station> getStations();
	Station findStation(int station_id);
	public Reservation createReservation(Travel travel, long user_id);
	public List<Reservation> getReservations(long user_id);
}
