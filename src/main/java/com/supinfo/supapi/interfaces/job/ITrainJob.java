package com.supinfo.supapi.interfaces.job;

public interface ITrainJob {
	public void initRail();
	public void findTravel(String departure_id, String departure_time, String arrival_id, String arrival_time);
}
