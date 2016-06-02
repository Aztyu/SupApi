package com.supinfo.supapi.interfaces.job;

import com.supinfo.supapi.entity.SearchStation;
import com.supinfo.supapi.entity.Travel;

public interface ITrainJob {
	public void initRail();
	public Travel findTravel(SearchStation search_station);
}
