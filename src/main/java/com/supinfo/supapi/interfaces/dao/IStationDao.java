package com.supinfo.supapi.interfaces.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.supinfo.supapi.entity.Station;
import com.supinfo.supapi.enumeration.Sens;
import com.supinfo.supapi.utils.Pair;

public interface IStationDao {
	public List<Station> searchStationByName(String search);
	public Station findStation(long arrival_id);
	public List<Station> getStations();
	public void getStationsFromStartToEndonLine(long id, long departure_id, long arrival_id);

	public long getDistanceforLine(long departure_id, long arrival_id, long line_id, Sens sens);
}
