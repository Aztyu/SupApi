package com.supinfo.supapi.interfaces.job;

import java.util.List;

import com.supinfo.supapi.entity.SearchStation;
import com.supinfo.supapi.entity.Travel;

public interface ITrainJob {
	public void initRail();
	public List<Travel> findTravel(SearchStation search_station);
}
