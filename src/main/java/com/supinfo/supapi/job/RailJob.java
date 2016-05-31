package com.supinfo.supapi.job;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.supinfo.supapi.entity.Line;
import com.supinfo.supapi.entity.SearchStation;
import com.supinfo.supapi.entity.SearchStep;
import com.supinfo.supapi.entity.Station;
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
		
		Station departure_st = dao.findStation(search.getArrival_station_id());
		Station arrival_st = dao.findStation(search.getDeparture_station_id());
		
		Line common_line = getCommonLine(departure_st, arrival_st);
		
		List<SearchStep> steps = new ArrayList<SearchStep>();
		
		if(common_line == null){
			//steps = searchStepsViaLines(steps);
			//TODO : find a path;
		}else{
			steps.add(getStep(departure_st, arrival_st, common_line));
		}
		
		String toto = "";
		
		
		// TODO Auto-generated method stub
		
	}

	private List<Station> searchStepsViaLines(List<Station> steps) {
		// TODO Auto-generated method stub
		return null;
	}

	private SearchStep getStep(Station departure_st, Station arrival_st, Line line) {
		SearchStep step = new SearchStep();
		
		Sens sens;
		if(departure_st.getId() < arrival_st.getId()){
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
		
		double time_start = distance_start/line.getAvg_speed();
		double time_end = distance_end/line.getAvg_speed();
		
		return step;
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
		Line l6 = new Line(6, "6", "Saint-Ã‰tienne-GenÃ¨ve", 150.0);
		Line l7 = new Line(7, "7", "Bordeaux-Perpignan", 200.0);
		Line l8 = new Line(8, "8", "Avignon-Perpignan", 220.0);
		
		//Station carrefour
		Station paris = new Station(1, "Paris, Gare de Lyon", "Place Louis Armand", "75571", "Paris", "FRANCE","48.8453765","2.369975");
		Station lyon = new Station(2, "Lyon, Gare Part-Dieu", "Place Charles Béraudier", "69003", "Lyon", "FRANCE","45.7605748","4.85822");
		Station avignon = new Station(3, "Avignon, Gare d'Avignon TGV", "Pont de l'Europe", "84000", "Avignon", "FRANCE","43.92157","4.7837573");
		Station perpignan = new Station(4, "Perpignan, Gare de Perpignan", "Place Salvador Dali", "66027", "Perpignan", "FRANCE","42.696303","2.8774992");
		Station bordeaux = new Station(5, "Bordeaux, Gare Saint-Jean", " Rue Charles Domercq", "33800", "Bordeaux", "FRANCE","44.831247","-0.7155392");
		Station angouleme = new Station(6, "AngoulÃªme, Gare d'Angoulême", "Rue de l'État À la Grand Font", "16000", "Angoulême", "FRANCE","45.653109","0.1661533");
		
		
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
		l1.addStation(new Station(7, "Lille, Gare de Lille-Europe", "Place François Mitterrand", "59777", "Euralille", "FRANCE","50.6271507","3.0477618"), 1, 100);
		l1.addStation(new Station(8, "Amiens, Gare d'Amiens", "Passage Alphonse Fique", "80000", "Amiens", "FRANCE","49.8899874","2.305108"), 2, 100);
		l1.addStation(new Station(9, "Le Mans, Gare du Mans", "Place du 8 Mai 1945", "72000", "Le Mans", "FRANCE","47.9956173","0.1902572"), 4, 100);
		l1.addStation(new Station(10, "Rennes, Gare de Rennes", "Place de la Gare", "35000", "Rennes", "FRANCE","48.1036292","-1.6746196,17"), 5, 100);
		l1.addStation(new Station(11, "Brest, Gare de Brest", "Place du 19ème RI", "29200", "Brest", "FRANCE","48.387721","-4.4823407"), 6, 100);
		
		l2.addStation(new Station(12, "Strasbourg, Gare de Strasbourg-Ville", "Place de la gare", "67000", "Strasbourg", "FRANCE","48.5816297","7.7265005"), 1, 100);
		l2.addStation(new Station(13, "Nancy, Gare de Nancy-Ville", "Place Thiers", "54000", "Nancy", "FRANCE","48.689836","6.1722613"), 2, 100);
		l2.addStation(new Station(14, "Metz, Gare de Metz-Ville", "Place du Général de Gaulle", "57000", "Metz", "FRANCE","49.1098419","6.1749279"), 3, 100);
		
		l3.addStation(new Station(15, "OrlÃ©ans, Gare d'Orléans", "Avenue de Paris", "45000", "Orléans", "FRANCE","47.9079059","1.9024873"), 2, 100);
		l3.addStation(new Station(16, "Tours, Gare SNCF de Tours", "Place du Général Leclerc", "37000", "Tours", "FRANCE","47.3892142","0.6920998"), 3, 100);
		l3.addStation(new Station(17, "Poitiers, Gare de Poitiers", "Boulevard Pont Achard", "86000", "Poitiers", "FRANCE","46.582491","0.3317063"), 4, 100);
		l3.addStation(new Station(18, "Bayonne, Gare de Bayonne", "Place Pereire", "64100", "Bayonne", "FRANCE","43.496848","-1.4724837"), 7, 100);
		
		l4.addStation(new Station(19, "Troyes, Gare de Troyes", "Rue du Ravelin", "10014", "Troyes", "FRANCE","48.2961167","4.0630401"), 2, 100);
		l4.addStation(new Station(20, "Dijon, Gare de Dijon-Ville", "Cour de la gare", "21000", "Dijon", "FRANCE","47.3235004","5.0249542"), 3, 100);
		l4.addStation(new Station(21, "Valence, Gare de Valence-Rhône-Alpes-Sud TGV", "Rue Denis Papin", "26000", "Valence", "FRANCE","44.9279752","4.8913464"), 5, 100);
		l4.addStation(new Station(22, "Marseille, Gare de Marseille-Saint-Charles", "Square Narvik", "13232", "Marseille", "FRANCE","43.3032794","5.3779533"), 7, 100);
		l4.addStation(new Station(23, "Toulon, Gare de Toulon", "Place de l'Europe", "83000", "Toulon", "FRANCE","43.1280607","5.9278816"), 8, 100);
		l4.addStation(new Station(24, "Nice, Gare de Nice-Ville", "Avenue Thiers", "06000", "Nice", "FRANCE","43.704612","7.2597443"), 9, 100);
		
		l5.addStation(new Station(25, "La Rochelle, Gare de La Rochelle-Ville", "Place Pierre Semard", "17000", "La Rochelle", "FRANCE","46.152642","-1.1475027"), 1, 100);
		l5.addStation(new Station(26, "Limoges, Gare de Limoges-Bénédictins", "Place Maison Dieu", "87036", "Limoges", "FRANCE","45.8362619","1.2653575"), 3, 100);
		l5.addStation(new Station(27, "Clermont-Ferrand, Gare de Clermont-Ferrand", "Avenue de l'Union Soviétique", "63000", "Clermont-Ferrand", "FRANCE","45.7788661","3.0984426"), 4, 100);
		l5.addStation(new Station(28, "Grenoble, Gare de Grenoble", "Place de la Gare", "38000", "Grenoble", "FRANCE","45.191506","5.7122818"), 6, 100);
		
		l6.addStation(new Station(29, "Saint-Ã‰tienne, Gare de Saint-Étienne-Châteaucreux", "Esplanade de France", "42000", "Saint-Étienne", "FRANCE","45.4434262","4.3972351"), 1, 100);
		l6.addStation(new Station(30, "Annecy, Gare d'Annecy", "Place de la gare", "74000", "Annecy", "FRANCE","45.901763","6.1207468"), 3, 100);
		l6.addStation(new Station(31, "GenÃ¨ve, Gare de Genève-Cornavin", "Place de Cornavin", "1203", "Genève", "SUISSE","46.2105273"," 6.140853"), 4, 100);
		
		l7.addStation(new Station(32, "Toulouse, Gare de Toulouse-Matabiau", "Boulevard Pierre Semard", "31079", "Toulouse", "FRANCE","43.6112684","1.4515309"), 2, 100);
		
		l8.addStation(new Station(33, "Nîmes, Gare de Nîmes", "Boulevard Sergent Triaire", "30000", "Nîmes", "FRANCE","43.83251","4.3640123"), 2, 100);
		l8.addStation(new Station(34, "Montpellier, Gare de Montpellier-Saint-Roch", "Place Auguste Gibert", "34000", "Montpellier", "FRANCE","43.6044652","3.8786331"), 3, 100);
		
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
