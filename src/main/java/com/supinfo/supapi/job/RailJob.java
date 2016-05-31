package com.supinfo.supapi.job;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.supinfo.supapi.entity.Line;
import com.supinfo.supapi.entity.SearchStation;
import com.supinfo.supapi.entity.SearchStep;
import com.supinfo.supapi.entity.Station;
import com.supinfo.supapi.entity.Train;
import com.supinfo.supapi.entity.TrainTrip;
import com.supinfo.supapi.entity.association.StationLineAssociation;
import com.supinfo.supapi.enumeration.Sens;
import com.supinfo.supapi.interfaces.dao.IRailDao;
import com.supinfo.supapi.interfaces.job.IRailJob;


public class RailJob implements IRailJob{
	
	@Autowired
	private IRailDao dao;
	
	//Rail
	
	@Override
	public void findTravel(SearchStation search) {
		Timestamp departure_date = search.getDeparture_date();
		Timestamp arrival_date = search.getArrival_date();
		
		Station departure_st = dao.findStation(search.getDeparture_station_id());
		Station arrival_st = dao.findStation(search.getArrival_station_id());
		
		Line common_line = getCommonLine(departure_st, arrival_st);
		
		List<SearchStep> steps = new ArrayList<SearchStep>();
		
		if(common_line == null){
			//steps = searchStepsViaLines(steps);
			//TODO : find a path;
		}else{
			steps.add(getStep(departure_st, arrival_st, common_line, departure_date));
		}
		
		String toto = "";
		
		
		// TODO Auto-generated method stub
		
	}

	private List<Station> searchStepsViaLines(List<Station> steps) {
		// TODO Auto-generated method stub
		return null;
	}

	private SearchStep getStep(Station departure_st, Station arrival_st, Line line, Timestamp departure) {
		SearchStep step = new SearchStep();
		
		Sens sens;
		if(departure_st.getId() < arrival_st.getId()){ //Erreur changé
			sens = Sens.ALLER;
		}else{
			sens = Sens.RETOUR;
		}
		double distance_start = 0.0;
		if(sens == Sens.ALLER){
			distance_start = dao.getDistanceforLine(1, departure_st.getId(), line.getId(), sens);
		}else{
			long station_id = line.getStations().get(line.getStations().size()-1).getStation().getId();
			distance_start = dao.getDistanceforLine(station_id, departure_st.getId(), line.getId(), sens);
		}
		
		double distance_end = dao.getDistanceforLine(departure_st.getId(), arrival_st.getId(), line.getId(), sens);
		
		double time_start = (distance_start/line.getAvg_speed())*60;	//Temps en minutes
		double time_end = (distance_end/line.getAvg_speed())*60;		
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(departure);							//init at desired departure time
		cal.set(Calendar.MINUTE, (int) -time_start);	//Set time at first station
		
		TrainTrip trip = findTrain(line, cal.getTime(), sens);
		
		return step;
	}
	
	private TrainTrip findTrain(Line line, Date date, Sens sens){
		Calendar cal_down = Calendar.getInstance();
		cal_down.setTime(date);	
		cal_down.add(Calendar.HOUR, -1);
		
		Calendar cal_up = Calendar.getInstance();
		cal_up.setTime(date);	
		cal_up.add(Calendar.HOUR, 1);
		
		TrainTrip tt = dao.findTrainTrip(line, cal_down, cal_up, sens);
		if(tt == null){
			cal_down.set(Calendar.HOUR_OF_DAY, 0);
			cal_up.set(Calendar.HOUR_OF_DAY, 0);
			cal_up.add(Calendar.DAY_OF_YEAR, 1);
			Train t = dao.findAvailableTrain(line, sens, cal_down, cal_up);
			if(t == null){
				t = new Train();
				t.setLine_id(line.getId());
				t.setSeats(140);
				dao.createTrain(t);
			}
			
			tt = new TrainTrip();
			tt.setAller(sens == Sens.ALLER);
			
			Calendar new_trip = Calendar.getInstance();
			new_trip.setTime(date);
			new_trip.set(Calendar.MINUTE, 0);
			
			tt.setDeparture_date(new_trip.getTime().getTime());
			tt.setTrain(t);
			dao.createTrainTrip(tt);
		}
		
		return tt;
	}

	private Line getCommonLine(Station departure_st, Station arrival_st) {
		long line = -1;
		for(StationLineAssociation l : departure_st.getLines()){
			long line_id = l.getLine().getId();
			for(StationLineAssociation l1 : arrival_st.getLines()){
				if(line_id == l1.getLine().getId()){
					return l1.getLine();
				}
			}
		}
		return null;
	}

	@Override
	public void initRail() {
		List<Line> lines = new ArrayList<Line>();
		
		//Toutes les lignes
		Line l1 = new Line(1, "1", "Lille-Brest", 200.0);
		Line l2 = new Line(2, "2", "Stasbourg-Paris", 140.0);
		Line l3 = new Line(3, "3", "Paris-Bayonne", 170.0);
		Line l4 = new Line(4, "4", "Paris-Nice", 250.0);
		Line l5 = new Line(5, "5", "La Rochelle-Grenoble", 120.0);
		Line l6 = new Line(6, "6", "Saint-Étienne-Genève", 150.0);
		Line l7 = new Line(7, "7", "Bordeaux-Perpignan", 200.0);
		Line l8 = new Line(8, "8", "Avignon-Perpignan", 220.0);
		
		//Station carrefour
		Station paris = new Station(1, "Paris, Gare de Lyon", "Place Louis Armand", "75571", "Paris", "FRANCE");
		Station lyon = new Station(2, "Lyon", "", "", "", "");
		Station avignon = new Station(3, "Avignon", "", "", "", "");
		Station perpignan = new Station(4, "Perpignan", "", "", "", "");
		Station bordeaux = new Station(5, "Bordeaux", "", "", "", "");
		Station angouleme = new Station(6, "Angoulême", "", "", "", "");
		
		
		//Ajout des stations qui servent de noeuds
		l1.addStation(paris, 3, 100);
		l2.addStation(paris, 4, 100);
		l3.addStation(paris, 1, 100);
		l4.addStation(paris, 1, 100);
		
		l3.addStation(angouleme, 5, 100);
		l5.addStation(angouleme, 2, 100);
		
		l3.addStation(bordeaux, 6, 100);
		l7.addStation(bordeaux, 1, 100);
		
		l4.addStation(lyon, 4, 100);
		l5.addStation(lyon, 5, 100);
		l6.addStation(lyon, 2, 100);
		
		l4.addStation(avignon, 6, 100);
		l8.addStation(avignon, 1, 100);
		
		l8.addStation(perpignan, 4, 100);
		
		//Ajout des station uniques par ligne
		l1.addStation(new Station(7, "Lille", "", "", "", ""), 1, 100);
		l1.addStation(new Station(8, "Amiens", "", "", "", ""), 2, 100);
		l1.addStation(new Station(9, "Le Mans", "", "", "", ""), 4, 100);
		l1.addStation(new Station(10, "Rennes", "", "", "", ""), 5, 100);
		l1.addStation(new Station(11, "Brest", "", "", "", ""), 6, 100);
		
		l2.addStation(new Station(12, "Strasbourg", "", "", "", ""), 1, 100);
		l2.addStation(new Station(13, "Nancy", "", "", "", ""), 2, 100);
		l2.addStation(new Station(14, "Metz", "", "", "", ""), 3, 100);
		
		l3.addStation(new Station(15, "Orléans", "", "", "", ""), 2, 100);
		l3.addStation(new Station(16, "Tours", "", "", "", ""), 3, 100);
		l3.addStation(new Station(17, "Poitiers", "", "", "", ""), 4, 100);
		l3.addStation(new Station(18, "Bayonne", "", "", "", ""), 7, 100);
		
		l4.addStation(new Station(19, "Troyes", "", "", "", ""), 2, 100);
		l4.addStation(new Station(20, "Dijon", "", "", "", ""), 3, 100);
		l4.addStation(new Station(21, "Valence", "", "", "", ""), 5, 100);
		l4.addStation(new Station(22, "Marseille", "", "", "", ""), 7, 100);
		l4.addStation(new Station(23, "Toulon", "", "", "", ""), 8, 100);
		l4.addStation(new Station(24, "Nice", "", "", "", ""), 9, 100);
		
		l5.addStation(new Station(25, "la Rochelle", "", "", "", ""), 1, 100);
		l5.addStation(new Station(26, "Limoges", "", "", "", ""), 3, 100);
		l5.addStation(new Station(27, "Clermont-Ferrand", "", "", "", ""), 4, 100);
		l5.addStation(new Station(28, "Grenoble", "", "", "", ""), 6, 100);
		
		l6.addStation(new Station(29, "Saint-Étienne", "", "", "", ""), 1, 100);
		l6.addStation(new Station(30, "Annecy", "", "", "", ""), 3, 100);
		l6.addStation(new Station(31, "Genève", "", "", "", ""), 4, 100);
		
		l7.addStation(new Station(32, "Toulouse", "", "", "", ""), 2, 100);
		
		l8.addStation(new Station(33, "Nimes", "", "", "", ""), 2, 100);
		l8.addStation(new Station(34, "Montpellier", "", "", "", ""), 3, 100);
		
		lines.add(l1);
		lines.add(l2);
		lines.add(l3);
		lines.add(l4);
		lines.add(l5);
		lines.add(l6);
		lines.add(l7);
		lines.add(l8);
		
		for(Line line : lines){
			dao.createLine(line);
		}
	}

	//Station
	
	@Override
	public List<Station> searchStationByName(String search) {
		List<Station> stations = dao.searchStationByName(search);
		for(Station s : stations){
			s.setLines(null);
		}
		return stations;
	}
	
	@Override
	public List<Station> getStations() {
		List<Station> stations = dao.getStations();
		for(Station s : stations){
			s.setLines(null);
		}
		return stations;
	}
	
}
