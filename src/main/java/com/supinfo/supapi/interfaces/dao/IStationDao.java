package com.supinfo.supapi.interfaces.dao;

import java.util.List;

import com.supinfo.supapi.entity.Station;

public interface IStationDao {
	public List<Station> searchStationByName(String search);
	public Station findStation(long arrival_id);
}
