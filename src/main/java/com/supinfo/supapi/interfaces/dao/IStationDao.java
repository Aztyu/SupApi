package com.supinfo.supapi.interfaces.dao;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.supinfo.supapi.entity.Line;
import com.supinfo.supapi.entity.Station;
import com.supinfo.supapi.entity.Train;
import com.supinfo.supapi.entity.TrainTrip;
import com.supinfo.supapi.enumeration.Sens;
import com.supinfo.supapi.utils.Pair;

public interface IStationDao {
	public List<Station> searchStationByName(String search);
	public Station findStation(long arrival_id);
	public List<Station> getStations();
	public long getDistanceforLine(long departure_id, long arrival_id, long line_id, Sens sens);
	public TrainTrip findTrainTrip(Line line, Calendar cal_down, Calendar cal_up, Sens sens);

}
