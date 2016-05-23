package com.supinfo.supapi.interfaces.job;

import java.util.List;

import com.supinfo.supapi.entity.Station;

public interface IStationJob {
	public List<Station> searchStationByName(String search);
}
