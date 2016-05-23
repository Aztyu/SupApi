package com.supinfo.supapi.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.supinfo.supapi.entity.Line;
import com.supinfo.supapi.entity.Station;
import com.supinfo.supapi.entity.association.StationLineAssociation;
import com.supinfo.supapi.interfaces.dao.IRailDao;
import com.supinfo.supapi.interfaces.dao.ITrainDao;
import com.supinfo.supapi.interfaces.job.IRailJob;
import com.supinfo.supapi.interfaces.job.ITrainJob;


public class RailJob implements IRailJob{
	
	@Autowired
	private IRailDao dao;
	
	//Rail
	
	@Override
	public void findTravel(String departure_id, String departure_time, String arrival_id, String arrival_time) {
		Date departure_date = new Date(Long.parseLong(departure_time));
		Date arrival_date = new Date(Long.parseLong(departure_time));
		
		
		Station departure_st = dao.findStation(Long.parseLong(departure_id));
		Station arrival_st = dao.findStation(Long.parseLong(arrival_id));
		
		long id = getCommonLine(departure_st, arrival_st);
		
		List<Station> steps = new ArrayList<Station>();
		steps.add(departure_st);
		
		if(id == -1){
			steps.addAll(getSteps(departure_st, arrival_st));
		}
		
		steps.add(arrival_st);
		
		
		// TODO Auto-generated method stub
		
	}
	
	private List<Station> getSteps(Station departure_st, Station arrival_st) {
		List<Station> stations = new ArrayList<Station>();
		
		
		
		return stations;
	}

	private long getCommonLine(Station departure_st, Station arrival_st) {
		long line = -1;
		for(StationLineAssociation l : departure_st.getLines()){
			long line_id = l.getLine().getId();
			for(StationLineAssociation l1 : arrival_st.getLines()){
				if(line_id == l1.getLine().getId()){
					return line_id;
				}
			}
		}
		return line;
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
		Station paris = new Station(1, "Paris", "");
		Station lyon = new Station(2, "Lyon", "");
		Station avignon = new Station(3, "Avignon", "");
		Station perpignan = new Station(4, "Perpignan", "");
		Station bordeaux = new Station(5, "Bordeaux", "");
		Station angouleme = new Station(6, "Angoulême", "");
		
		
		//Ajout des stations qui servent de noeuds
		l1.addStation(paris, 3);
		l2.addStation(paris, 4);
		l3.addStation(paris, 1);
		l4.addStation(paris, 1);
		
		l3.addStation(angouleme, 5);
		l5.addStation(angouleme, 2);
		
		l3.addStation(bordeaux, 6);
		l7.addStation(bordeaux, 1);
		
		l4.addStation(lyon, 4);
		l5.addStation(lyon, 5);
		l6.addStation(lyon, 2);
		
		l4.addStation(avignon, 6);
		l8.addStation(avignon, 1);
		
		l8.addStation(perpignan, 4);
		
		//Ajout des station uniques par ligne
		l1.addStation(new Station(7, "Lille", ""), 1);
		l1.addStation(new Station(8, "Amiens", ""), 2);
		l1.addStation(new Station(9, "Le Mans", ""), 4);
		l1.addStation(new Station(10, "Rennes", ""), 5);
		l1.addStation(new Station(11, "Brest", ""), 6);
		
		l2.addStation(new Station(12, "Strasbourg", ""), 1);
		l2.addStation(new Station(13, "Nancy", ""), 2);
		l2.addStation(new Station(14, "Metz", ""), 3);
		
		l3.addStation(new Station(15, "Orléans", ""), 2);
		l3.addStation(new Station(16, "Tours", ""), 3);
		l3.addStation(new Station(17, "Poitiers", ""), 4);
		l3.addStation(new Station(18, "Bayonne", ""), 7);
		
		l4.addStation(new Station(19, "Troyes", ""), 2);
		l4.addStation(new Station(20, "Dijon", ""), 3);
		l4.addStation(new Station(21, "Valence", ""), 5);
		l4.addStation(new Station(22, "Marseille", ""), 7);
		l4.addStation(new Station(23, "Toulon", ""), 8);
		l4.addStation(new Station(24, "Nice", ""), 9);
		
		l5.addStation(new Station(25, "la Rochelle", ""), 1);
		l5.addStation(new Station(26, "Limoges", ""), 3);
		l5.addStation(new Station(27, "Clermont-Ferrand", ""), 4);
		l5.addStation(new Station(28, "Grenoble", ""), 6);
		
		l6.addStation(new Station(29, "Saint-Étienne", ""), 1);
		l6.addStation(new Station(30, "Annecy", ""), 3);
		l6.addStation(new Station(31, "Genève", ""), 4);
		
		l7.addStation(new Station(32, "Toulouse", ""), 2);
		
		l8.addStation(new Station(33, "Nimes", ""), 2);
		l8.addStation(new Station(34, "Montpellier", ""), 3);
		
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
	
}
