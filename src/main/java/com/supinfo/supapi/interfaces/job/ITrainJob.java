package com.supinfo.supapi.interfaces.job;

import com.supinfo.supapi.entity.SearchStation;

public interface ITrainJob {
	public void initRail();
	public void findTravel(SearchStation search_station);
}
