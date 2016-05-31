package com.supinfo.supapi.interfaces.dao;

import java.util.Calendar;

import com.supinfo.supapi.entity.Line;
import com.supinfo.supapi.entity.Train;
import com.supinfo.supapi.entity.TrainTrip;
import com.supinfo.supapi.enumeration.Sens;

public interface ITrainDao {
	public void createLine(Line line);
	public Train findAvailableTrain(Line line, Sens sens, Calendar cal_down, Calendar cal_up);
	public void createTrain(Train train);
	public void createTrainTrip(TrainTrip tt);
}
