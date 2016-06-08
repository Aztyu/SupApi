package com.supinfo.supapi.job;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.impl.SLF4JLocationAwareLog;
import org.springframework.beans.factory.annotation.Autowired;

import com.supinfo.supapi.entity.Line;
import com.supinfo.supapi.entity.Node;
import com.supinfo.supapi.entity.SearchStation;
import com.supinfo.supapi.entity.SearchStep;
import com.supinfo.supapi.entity.Station;
import com.supinfo.supapi.entity.StationList;
import com.supinfo.supapi.entity.Train;
import com.supinfo.supapi.entity.TrainTrip;
import com.supinfo.supapi.entity.Travel;
import com.supinfo.supapi.entity.association.StationLineAssociation;
import com.supinfo.supapi.enumeration.Sens;
import com.supinfo.supapi.interfaces.dao.IRailDao;
import com.supinfo.supapi.interfaces.job.IRailJob;


public class RailJob implements IRailJob{
	
	@Autowired
	private IRailDao dao;
	
	//Rail
	
	@Override
	public List<Travel> findTravel(SearchStation search) {
		List<Travel> travel_list = new ArrayList<Travel>();

		Timestamp departure_date = search.getDeparture_date();
		Timestamp arrival_date = search.getArrival_date();
		
		Station departure_st = dao.findStation(search.getDeparture_station_id());
		Station arrival_st = dao.findStation(search.getArrival_station_id());
		
		Line common_line = getCommonLine(departure_st, arrival_st);

		if(common_line != null){
			List<SearchStep> steps = new ArrayList<SearchStep>();
			
			Travel travel = new Travel();
			steps.add(getStep(departure_st, arrival_st, common_line, departure_date));
			
			travel.setAller(steps);
			
			if(search.isAller_only() == false){
				List<SearchStep> steps_retour = new ArrayList<SearchStep>();
				steps_retour.add(getStep(arrival_st, departure_st, common_line, arrival_date));
				travel.setRetour(steps_retour);
			}
			
			travel.calculatePrice();
			travel.calculateTime();
			travel_list.add(travel);
		}else{
			List<List<StationList>> stations = getStationList(departure_st, arrival_st);
			
			for(List<StationList> lsl : stations){
				Travel travel = new Travel();
				List<SearchStep> steps = new ArrayList<SearchStep>();
				
				Timestamp last_departure_date = departure_date;
				for(StationList sl : lsl){				
					steps.add(getStep(departure_st, arrival_st, common_line, last_departure_date));
					last_departure_date = steps.get(steps.size()-1).getEnd_time();
				}
				
				travel.setAller(steps);
				
				if(search.isAller_only() == false){
					List<SearchStep> steps_retour = new ArrayList<SearchStep>();
					steps_retour.add(getStep(arrival_st, departure_st, common_line, arrival_date));
					travel.setRetour(steps_retour);
				}
				
				travel.calculatePrice();
				travel.calculateTime();
				travel_list.add(travel);
			}

		}
		
		return travel_list;
	}

	private List<List<StationList>> getStationList(Station start, Station stop) {
		List<Line> target = new ArrayList<Line>();
		List<Station> checked_station = new ArrayList<Station>();
		List<Station> nodes = dao.getNodeStations();
		
		Node root = new Node(start, null);

		List<Line> check_line = new ArrayList<Line>();
		for(StationLineAssociation sla : start.getLines()){
			check_line.add(sla.getLine());
		}
		
		for(StationLineAssociation sla : stop.getLines()){
			target.add(sla.getLine());
		}
		
		while(checked_station.size() < nodes.size()){
			parseChildren(root, checked_station, nodes);
		}
		
		cleanTree(root, target);
		List<List<StationList>> liste = root.getList();
		return liste;
	}

	private boolean cleanTree(Node root, List<Line> target) {	
		if(root.getChild().isEmpty()){
			for(Line line : target){
				for(StationLineAssociation sla : root.getValue().getLines()){
					if(sla.getLine().equals(line)){
						return false;
					}
				}
			}
			return true;
		}else{
			boolean hasTarget = false;
			for(Line line : target){
				for(StationLineAssociation sla : root.getValue().getLines()){
					if(sla.getLine().equals(line)){
						hasTarget = true;
					}
				}
			}
			
			if(hasTarget){
				root.getChild().clear();
			}else{
				List<Node> nodes = new ArrayList<Node>();
				
				for(Node node : root.getChild()){
					if(cleanTree(node, target)){
						nodes.add(node);
					}
				}
				
				root.getChild().removeAll(nodes);
				if(root.getChild().isEmpty()){
					return true;
				}
			}
			return false;
		}
	}

	private void parseChildren(Node root, List<Station> checked_station, List<Station> nodes) {
		if(root.getChild().isEmpty()){
			checked_station.add(root.getValue());
			for(StationLineAssociation sla : root.getValue().getLines()){
				for(Station s : nodes){
					if(checked_station.contains(s) == false){
						for(StationLineAssociation sla1 : s.getLines()){
							if(sla.getLine().equals(sla1.getLine())){
								root.addChild(new Node(s, root));
								break;
							}
						}
					}
				}
			}
		}else{
			for(Node child : root.getChild()){
				parseChildren(child, checked_station, nodes);
			}
		}
	}

	private SearchStep getStep(Station departure_st, Station arrival_st, Line line, Timestamp departure) {
		SearchStep step = new SearchStep();
		
		Sens sens;
		long arrival_order = arrival_st.getStationOrder(line.getId());
		long departure_order = departure_st.getStationOrder(line.getId());
		
		if(departure_order < arrival_order){
			sens = Sens.ALLER;
		}else{
			sens = Sens.RETOUR;
		}
		
		double distance_start = 0.0;
		
		if(sens == Sens.ALLER){
			distance_start = dao.getDistanceforLine(1, departure_order, line.getId(), sens);
		}else{
			long station_id = line.getStations().get(line.getStations().size()-1).getStation_order();
			distance_start = dao.getDistanceforLine(station_id, departure_order, line.getId(), sens);
		}
		
		double distance_end = dao.getDistanceforLine(departure_order, arrival_order, line.getId(), sens);
		
		double time_start = (distance_start/line.getAvg_speed())*60;	//Temps en minutes
		double time_end = (distance_end/line.getAvg_speed())*60;		
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(departure);							//init at desired departure time
		cal.set(Calendar.MINUTE, (int) -time_start);	//Set time at first station
		
		TrainTrip trip = findTrain(line, cal.getTime(), sens);
		Calendar trip_start = Calendar.getInstance();
		trip_start.setTime(trip.getDeparture_date());
		trip_start.add(Calendar.MINUTE, (int)time_start);
		
		Calendar trip_end = Calendar.getInstance();
		trip_end.setTime(trip_start.getTime());
		trip_end.add(Calendar.MINUTE, (int)time_end);
		
		step.setTrain_trip(trip);
		step.setStart(departure_st);
		step.setStart_time(trip_start.getTime());
		
		step.setEnd(arrival_st);
		step.setEnd_time(trip_end.getTime());
		
		step.setPrice(line.getPrice()*distance_end);
		step.setTime((int)time_end);
		
		return step;
	}
	
	private TrainTrip findTrain(Line line, Date date, Sens sens){
		Calendar cal_down = Calendar.getInstance();
		cal_down.setTime(date);	
		cal_down.add(Calendar.HOUR, -1);
		
		Calendar cal_up = Calendar.getInstance();
		cal_up.setTime(date);	
		cal_up.add(Calendar.HOUR, 1);
		
		TrainTrip tt = dao.findTrainTrip(line, cal_down.getTime(), cal_up.getTime(), sens);
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
			
			tt.setLong_Departure_date(new_trip.getTime().getTime());
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
		Station paris = new Station(1, "Paris, Gare de Lyon", "Place Louis Armand", "75571", "Paris", "FRANCE",48.8453765,2.369975, "Souvent appelée simplement gare de Lyon, elle est située dans le 12e arrondissement, principalement dans le quartier des Quinze-Vingts, le sud-est des voies et des quais étant situé dans le quartier de Bercy. C'est la troisième gare de Paris par son trafic (environ 83 millions de voyageurs en 20023), et la deuxième en termes de trains de grandes lignes (31,8 millions, TGV inclus, derrière la gare du Nord). <br> La gare de Lyon est la tête de ligne des TGV à destination du Sud-Est de la France, en particulier la région Rhône-Alpes et la façade méditerranéenne, ainsi que des régions intermédiaires et des pays voisins de ces régions. C'est aussi une gare du réseau Transilien réalisant la desserte du Sud-Est de la région parisienne avec la ligne R du Transilien (lignes Paris-Montereau via Héricy, Paris-Montereau via Fontainebleau et Moret et enfin Paris-Montargis).");
		Station lyon = new Station(2, "Lyon, Gare Part-Dieu", "Place Charles Béraudier", "69003", "Lyon", "FRANCE",45.7605748,4.85822, "La gare de Lyon-Part-Dieu, située dans le quartier de La Part-Dieu, est une gare ferroviaire française de la ville de Lyon, chef-lieu de la métropole de Lyon, en région Auvergne-Rhône-Alpes. <br> Elle a été mise en service le 13 juin 1983 dans le cadre d'une opération d'aménagement urbain qui a vu la création d'un second centre ville de Lyon et l'un des plus grands centres commerciaux de France, le centre commercial de la Part-Dieu situé juste en face de la gare sur le boulevard Vivier-Merle, un important centre administratif et un centre d'affaires dominés par « le crayon » (Tour Part-Dieu). <br> Ces centres d'intérêts ont conduit les TCL à en faire le premier pôle de correspondance de Lyon 4 : 17 lignes traversent ou prennent leur départ à Gare Part-Dieu Vivier Merle ou Gare Part-Dieu Villette.");
		Station avignon = new Station(3, "Avignon, Gare d'Avignon TGV", "Pont de l'Europe", "84000", "Avignon", "FRANCE",43.92157,4.7837573, "La gare d'Avignon TGV est une gare ferroviaire française TGV, de la LGV Méditerranée, située sur le territoire de la commune d'Avignon, dans le département de Vaucluse, en région Provence-Alpes-Côte d'Azur. Cette gare, inaugurée en 2001, a été conçue par le cabinet d'architecture de la SNCF sous la direction de Jean-Marie Duthilleul et Jean-François Blassel. <br> d'Aix-en-Provence TGV. Depuis le 15 décembre 2013, elle est reliée à la gare d'Avignon-Centre par le raccordement Courtine, plus connu sous le nom de Virgule d'Avignon.");
		Station perpignan = new Station(4, "Perpignan, Gare de Perpignan", "Place Salvador Dali", "66027", "Perpignan", "FRANCE",42.696303,2.8774992, "La gare de Perpignan est une gare ferroviaire française, située à proximité du centre ville de Perpignan, dans le département des Pyrénées-Orientales en région Languedoc-Roussillon. <br> Elle a été mise en service en 1858 par la Compagnie des chemins de fer du Midi et du Canal latéral à la Garonne. C'est une gare de la Société nationale des chemins de fer français (SNCF), desservie par des TGV, des Intercités de nuit et des trains express régionaux TER Languedoc-Roussillon.");
		Station bordeaux = new Station(5, "Bordeaux, Gare Saint-Jean", " Rue Charles Domercq", "33800", "Bordeaux", "FRANCE",44.831247,-0.7155392, "La gare de Bordeaux-Saint-Jean, dite gare Saint-Jean, à Bordeaux, est la plus grande gare ferroviaire de la région Aquitaine-Limousin-Poitou-Charentes, à 3 h de Paris, temps de parcours devant être ramené à 2 h 05 environ après la mise en service de la LGV Sud Europe Atlantique, prévue pour le 31 juillet 2017. <br> Les trains desservant la gare sont en provenance ou à destination de l'Espagne et de villes comme Arcachon, Toulouse, Lyon, Marseille, Strasbourg, Lille, Nantes et Nice. Les liaisons avec Bergerac, Périgueux, Angoulême, Agen, Pau, Libourne, La Rochelle, Limoges et Poitiers sont fréquentes.");
		Station angouleme = new Station(6, "Angoulême, Gare d'Angoulême", "Rue de l'état à la Grand Font", "16000", "Angoulême", "FRANCE",45.653109,0.1661533, "La gare d'Angoulême est une gare ferroviaire française, de la ville d'Angoulême, dans le département de la Charente, en région Aquitaine-Limousin-Poitou-Charentes, située à 2 h 10 de Paris et 55 min de Bordeaux en TGV (meilleurs temps de parcours) ; elle est également reliée aux villes de Royan et de Limoges ainsi qu'à Poitiers. <br> La gare possède un quai 1 latéral (voie 1) long de 465 m, un quai 2 central (voie 2 et Z) de 480/462 m et un quai 3 central (voies 4 et 6) de 380 m4. Son altitude est de 46 m5.");
		
		
		//Ajout des stations qui servent de noeuds
		l1.addStation(paris, 3, 115);
		l2.addStation(paris, 4, 280);
		l3.addStation(paris, 1, 100);
		l4.addStation(paris, 1, 100);
		
		l3.addStation(angouleme, 5, 106);
		l5.addStation(angouleme, 2, 117);
		
		l3.addStation(bordeaux, 6, 107);
		l7.addStation(bordeaux, 1, 100);
		
		l4.addStation(lyon, 4, 173);
		l5.addStation(lyon, 5, 134);
		l6.addStation(lyon, 2, 53);
		
		l4.addStation(avignon, 6, 111);
		l8.addStation(avignon, 1, 100);
		
		l8.addStation(perpignan, 4, 131);
		l7.addStation(perpignan, 3, 157);
		
		//Ajout des station uniques par ligne
		l1.addStation(new Station(7, "Lille, Gare de Lille-Europe", "Place François Mitterrand", "59777", "Lille", "FRANCE",50.6271507,3.0477618, "La gare de Lille-Europe, dite aussi « Lille Eurostar » ou « Lille Europe International », est une gare ferroviaire française de la ligne à grande vitesse de Fretin à Fréthun, située en bordure du centre-ville de Lille, ville centre de la Métropole européenne de Lille, préfecture du département du Nord et de la région Nord-Pas-de-Calais-Picardie.<br> Elle est mise en service en 1994, lors de l'ouverture de la ligne à grande vitesse (LGV). Elle est la deuxième gare de la ville, pour le trafic voyageurs, après sa voisine la gare de Lille-Flandres. <br> Gare de la Société nationale des chemins de fer français (SNCF), elle est desservie par l'Eurostar, le Thalys, des TGV et des TERGV. Elle permet des correspondances avec la station de métro Gare Lille-Europe."), 1, 100);
		l1.addStation(new Station(8, "Amiens, Gare d'Amiens", "Passage Alphonse Fique", "80000", "Amiens", "FRANCE",49.8899874,2.305108, "La gare d'Amiens (dite localement Gare du Nord, notamment dans les transports en commun urbains) est une gare ferroviaire française située à proximité immédiate du centre-ville de la commune d'Amiens, préfecture du département de la Somme, en région Nord-Pas-de-Calais-Picardie.<br> La tour Perret fait l’objet d’une inscription au titre des monuments historiques depuis le 29 octobre 19752. L'ensemble architectural d'Auguste Perret constitué par la place Alphonse-Fiquet (gare et immeubles d'habitation) fait l’objet d’une inscription au titre des monuments historiques depuis le 4 mars 20032. <br> C'est une gare de la Société nationale des chemins de fer français (SNCF) du réseau TER Picardie, desservie par des trains Intercités et régionaux."), 2, 96);
		l1.addStation(new Station(9, "Le Mans, Gare du Mans", "Place du 8 Mai 1945", "72000", "Le Mans", "FRANCE",47.9956173,0.1902572, "La gare du Mans est une gare ferroviaire française située sur le territoire de la commune du Mans, dans le département de la Sarthe, en région Pays de la Loire. <br> Elle se trouve sur la ligne de Paris-Montparnasse à Brest et fait partie d'un complexe ferroviaire où aboutissent ou partent les lignes du Mans à Mézidon, du Mans à Angers-Maître-École et de Tours au Mans. <br> C'est une gare de la Société nationale des chemins de fer français (SNCF) desservie par les trains des réseaux TGV, Intercités, TER Basse-Normandie, TER Centre-Val de Loire et TER Pays de la Loire."), 4, 183);
		l1.addStation(new Station(10, "Rennes, Gare de Rennes", "Place de la Gare", "35000", "Rennes", "FRANCE",48.1036292,-1.6746196, "La gare de Rennes est une gare ferroviaire française de la ligne de Paris-Montparnasse à Brest, située au sud du centre-ville de Rennes, préfecture du département d'Ille-et-Vilaine et de la région Bretagne. Elle est édifiée au cœur du quartier Sud-Gare, à environ un kilomètre de l’hôtel de Ville. <br> Quotidiennement, 54 TGV et 180 trains régionaux génèrent un trafic moyen de 26 300 voyageurs1. Sa création remonte à 1857, lors de l'arrivée dans la ville de la ligne de Paris-Montparnasse à Brest. Les locaux ont été entièrement réaménagés en 1992 par l'architecte Thierry Le Berre pour la mise en service du TGV Atlantique. <br> Depuis octobre 2015, la gare fait l'objet d'une profonde restructuration destinée à préparer, d'ici à 2020, l'arrivée de la LGV Bretagne, un projet de refonte de quartier appelé EuroRennes et le passage de la seconde ligne de métro."), 5, 138);
		l1.addStation(new Station(11, "Brest, Gare de Brest", "Place du 19ème RI", "29200", "Brest", "FRANCE",48.387721,-4.4823407, "La gare de Brest est la gare ferroviaire française terminus de la ligne de Paris-Montparnasse à Brest, située en surplomb de la rade à proximité du centre de la ville de Brest, dans le département du Finistère, en région Bretagne.<br> Elle est mise en service en 1865 par la compagnie des chemins de fer de l'Ouest. C'est une gare de la Société nationale des chemins de fer français (SNCF), desservie par le TGV Atlantique et des trains express régionaux TER Bretagne. <br> Établie à 43 mètres d'altitude, la gare de Brest est située au point kilométrique (PK) 622,422 de la ligne de Paris-Montparnasse à Brest, après la gare de Kerhuon (auparavant, la gare du Rody s'intercalait). Quatre kilomètres avant la gare, un embranchement permet de rejoindre le port de Brest."), 6, 208);
		
		l2.addStation(new Station(12, "Strasbourg, Gare de Strasbourg-Ville", "Place de la gare", "67000", "Strasbourg", "FRANCE",48.5816297,7.7265005, "La gare de Strasbourg-Ville (dénomination officielle donnée par la Société nationale des chemins de fer français pour la différencier des autres gares strasbourgeoises1), aussi appelée Gare Centrale [de Strasbourg] (essentiellement dans les transports en commun urbains) ou usuellement gare de Strasbourg (également dénomination commerciale de la SNCF), est une gare ferroviaire française, située dans le quartier Gare - Tribunal2, sur le territoire de la commune de Strasbourg, chef-lieu du département du Bas-Rhin et de la région Alsace-Champagne-Ardenne-Lorraine. <br> Inaugurée en 1883 par l'administration allemande de l'époque, elle remplace l'ancienne gare de Strasbourg, et constitue le centre d'une importante « étoile ferroviaire » à cinq branches dont une est transfrontalière. Sa desserte est aussi bien régionale que nationale et internationale. Elle est également le principal pôle d'échanges de l'agglomération strasbourgeoise."), 1, 100);
		l2.addStation(new Station(13, "Nancy, Gare de Nancy-Ville", "Place Thiers", "54000", "Nancy", "FRANCE",48.689836,6.1722613, "La gare de Nancy-Ville, anciennement dénommée gare de « Nancy-Saint-Jean », est une gare ferroviaire française de la ligne de Noisy-le-Sec à Strasbourg-Ville (Paris – Strasbourg), située à proximité du centre-ville de Nancy, préfecture du département de Meurthe-et-Moselle, en région Lorraine. <br> Mise en service en 1852, elle se situe au carrefour de grands axes européens : Est-Ouest (Paris – Vienne), et Nord-Sud (Luxembourg – Lyon – Marseille), mais également au cœur du trafic régional lorrain (sillon mosellan / Vosges). Le train de luxe Orient-Express y faisait une escale quotidienne jusqu'en 2002. <br> L'arrivée du TGV-Est le 10 juin 2007 a accru le flux de voyageurs du fait de la réduction du temps de parcours entre Paris et Nancy de 3 heures à 90 minutes. Marquant la limite ouest du centre-ville, la gare est au centre de l'agglomération. Elle souffre depuis sa construction de son exiguïté. Le manque de place disponible est surtout pénalisant à sa sortie nord, dans la tranchée accueillant les voies en direction de Metz et Paris1."), 2, 113);
		l2.addStation(new Station(14, "Metz, Gare de Metz-Ville", "Place du Général de Gaulle", "57000", "Metz", "FRANCE",49.1098419,6.1749279, "La gare de Metz-Ville (dénomination officielle donnée par la Société nationale des chemins de fer français pour la différencier des autres gares messines), usuellement appelée gare de Metz, est une gare ferroviaire française située à proximité du centre-ville de Metz, préfecture du département de la Moselle. <br> Inaugurée en 1908 par la Direction générale impériale des chemins de fer d'Alsace-Lorraine, elle remplace l'ancienne gare de Metz mise en service en 1878. Le bâtiment voyageurs, pour ses façades et toitures (hors verrière), son salon d’honneur, le décor du buffet et son hall de départ, fait l’objet d’une inscription au titre des monuments historiques depuis le 15 janvier 19752. <br> C'est une gare de la Société nationale des chemins de fer français (SNCF) desservie par des TGV, et des trains régionaux TER Lorraine, ainsi que TER Alsace et TER Champagne-Ardenne."), 3, 47);
		
		l3.addStation(new Station(15, "Orléans, Gare d'Orléans", "Avenue de Paris", "45000", "Orléans", "FRANCE",47.9079059,1.9024873, "La gare d'Orléans est une gare ferroviaire française située sur le territoire de la commune d'Orléans, dans le département du Loiret, en région Centre-Val de Loire. C'est une gare de la Société nationale des chemins de fer français (SNCF) desservie par les trains des réseaux Intercités, Interloire et TER Centre-Val de Loire. <br> Gare en impasse et de bifurcation, elle est située au point kilométrique (PK) 121,0302 de la ligne des Aubrais - Orléans à Orléans et au PK 72,1 de la ligne de Chartres à Orléans exploitée en trafic fret. Elle est aussi l'origine de la ligne d'Orléans à Gien partiellement déclassée, du raccordement d'Orléans vers Vierzon et du raccordement d'Orléans vers Tours. Son altitude est de 114 mètres."), 2, 111);
		l3.addStation(new Station(16, "Tours, Gare SNCF de Tours", "Place du Général Leclerc", "37000", "Tours", "FRANCE",47.3892142,0.6920998, "La gare de Tours est une gare ferroviaire française située sur le territoire de la commune de Tours, dans le département d'Indre-et-Loire, en région Centre-Val de Loire. <br> La gare est exploitée par la Société nationale des chemins de fer français (SNCF). Elle est desservie par le TGV, des trains Intercités et des trains express régionaux (TER). Néanmoins, sa situation de gare terminus nécessitant un rebroussement lui a fait perdre le trafic de certains trains qui s'arrêtent seulement à la gare de Saint-Pierre-des-Corps. <br> Les voyageurs ont la possibilité de rejoindre facilement l'une ou l'autre gare par une navette ferroviaire qui est devenue également un moyen de transport en commun urbain."), 3, 102);
		l3.addStation(new Station(17, "Poitiers, Gare de Poitiers", "Boulevard Pont Achard", "86000", "Poitiers", "FRANCE",46.582491,0.3317063, "La gare de Poitiers est une gare ferroviaire française de la ligne de Paris-Austerlitz à Bordeaux-Saint-Jean, située dans le centre de la ville de Poitiers, dans le département de la Vienne, en région Aquitaine-Limousin-Poitou-Charentes. <br> Elle est l'une des rares gares dont les quais sont accessibles à la fois par un passage souterrain et par une passerelle aérienne. Cette dernière relie les deux rues bordant les voies de chemin de fer et permet d'accéder au parc de stationnement et au centre de conférences."), 4, 91);
		l3.addStation(new Station(18, "Bayonne, Gare de Bayonne", "Place Pereire", "64100", "Bayonne", "FRANCE",43.496848,-1.4724837, "La gare de Bayonne est une gare ferroviaire française des lignes de Bordeaux-Saint-Jean à Irun et de Toulouse à Bayonne, située à proximité du centre ville de Bayonne, quartier Saint Esprit, sous préfecture du département des Pyrénées-Atlantiques en région Aquitaine-Limousin-Poitou-Charentes. C'est une gare de la Société nationale des chemins de fer français (SNCF) desservie par le TGV, des trains grandes lignes et des trains régionaux TER Aquitaine. <br> Gare de bifurcation, elle est située au point kilométrique (PK) 197,5552 de la ligne de Bordeaux-Saint-Jean à Irun, entre la gare de Boucau et la gare de Biarritz, et au PK 321,383 de la ligne de Toulouse à Bayonne. Elle est aussi la gare d'origine de la ligne de Bayonne à Saint-Jean-Pied-de-Port et de la ligne de Bayonne aux Allées-Marines exploitée uniquement pour le fret."), 7, 163);
		
		l4.addStation(new Station(19, "Troyes, Gare de Troyes", "Rue du Ravelin", "10014", "Troyes", "FRANCE",48.2961167,4.0630401, "La gare de Troyes est une gare ferroviaire française de la ligne de Paris-Est à Mulhouse-Ville, située sur le territoire de la commune de Troyes, dans le département de l'Aube en région Champagne-Ardenne. Elle est mise en service en 1848 par la Compagnie du chemin de fer de Montereau à Troyes. C'est en 2014 une gare Société nationale des chemins de fer français (SNCF), desservie par des trains : Intercités, TER Champagne-Ardenne et de fret. <br> La gare de bifurcation de Troyes est située au point kilométrique (PK) 166,1932 de la ligne de Paris-Est à Mulhouse-Ville entre les gares, ouvertes au service voyageurs, de Romilly-sur-Seine et de Vendeuvre et au PK 90,32 de la ligne de Coolus à Sens partiellement déclassée. Elle est également l'origine de la ligne de Troyes à Brienne-le-Château ouverte au service du fret, de la ligne de Saint-Julien (Troyes) à Saint-Florentin - Vergigny (partiellement déclassée) et de la ligne de Saint-Julien (Troyes) à Gray partiellement déclassée elle aussi."), 2, 143);
		l4.addStation(new Station(20, "Dijon, Gare de Dijon-Ville", "Cour de la gare", "21000", "Dijon", "FRANCE",47.3235004,5.0249542, "La gare de Dijon-Ville est une gare ferroviaire française de la ligne de Paris-Lyon à Marseille-Saint-Charles et d'une étoile ferroviaire comprenant plusieurs lignes. Dite aussi Gare Foch, elle est la gare principale de la ville de Dijon, dans le département de la Côte-d'Or, en région Bourgogne-Franche-Comté. La gare secondaire de Dijon-Porte-Neuve est située sur la ligne de Dijon-Ville à Is-sur-Tille. <br> Elle est mise en service en 1849 par la l'Administration des chemins de fer de l'État avant de devenir en 1852 une gare du réseau de la Compagnie du chemin de fer de Lyon à la Méditerranée (PLM). <br> C'est une gare de la Société nationale des chemins de fer français (SNCF), desservie par le TGV depuis 1981. Elle est également une gare régionale du réseau TER Bourgogne."), 3, 133);
		l4.addStation(new Station(21, "Valence, Gare de Valence-Rhône-Alpes-Sud TGV", "Rue Denis Papin", "26000", "Valence", "FRANCE",44.9279752,4.8913464, "La gare de Valence-Ville est une gare ferroviaire française située sur le territoire de la commune de Valence, dans le département de la Drôme, en région Auvergne-Rhône-Alpes. La gare de Valence-ville est ouverte au public tous les jours de la semaine, en continu. Ses seules heures de fermetures sont les après-midi des jours fériés. En plus de la salle d'attente, un service de bagages, ainsi qu'un service d'objets trouvés sont à dispositions des voyageurs. <br> Gare de bifurcation, elle est située au point kilométrique 616,939 de la ligne de Paris-Lyon à Marseille-Saint-Charles entre les gares de Tain-l'Hermitage - Tournon et de Livron. Elle est également l'origine de la ligne de Valence à Moirans. Son altitude est de 123 m."), 5, 99);
		l4.addStation(new Station(22, "Marseille, Gare de Marseille-Saint-Charles", "Square Narvik", "13232", "Marseille", "FRANCE",43.3032794,5.3779533, "La gare de Marseille-Saint-Charles est une gare ferroviaire française, la principale de l'agglomération de Marseille. Cette gare en cul-de-sac a été construite par l'ingénieur Gustave Desplaces sur le rebord d'un plateau proche du centre-ville, antérieurement occupé par des « campagnes ». Elle communique avec le centre-ville par un escalier monumental, construit en 1925. <br> La gare Saint-Charles est, depuis longtemps, un point de passage des voyageurs à destination de la Corse et de l'Afrique, jadis également du Moyen-Orient et de l'Asie. Ils embarquent, soit sur un paquebot de croisière, soit sur un ferry-boat, au bassin de La Joliette. Les voyageurs, arrivant principalement du nord de l'Europe, de Paris et de la Grande-Bretagne, peuvent effectuer une halte d'une nuit dans les nombreux hôtels édifiés sur le boulevard d'Athènes. <br> Son trafic est passé de 7,1 millions de passagers annuels en 2000 à 11,5 millions en 20131 notamment dû à l'effet TGV mettant Marseille à 3 h de Paris, 1 h 40 de Lyon et 4 h 504 de Lille."), 7, 90);
		l4.addStation(new Station(23, "Toulon, Gare de Toulon", "Place de l'Europe", "83000", "Toulon", "FRANCE",43.1280607,5.9278816, "La gare de Toulon est une gare ferroviaire française de la ligne de Marseille-Saint-Charles à Vintimille (frontière), située à proximité immédiate du centre de la ville de Toulon (chef-lieu de département du Var), entre le Mont Faron et le port, en région Provence-Alpes-Côte d'Azur. <br> C'est une gare de la Société nationale des chemins de fer français (SNCF), bénéficiant du service d'information en ligne Gare en mouvement, desservie par des TGV, des trains grandes lignes SNCF Intercités & Intercités de nuit et un train grande ligne EC Thello. C'est aussi une gare du réseau des trains express régionaux TER Provence-Alpes-Côte d'Azur."), 8, 50);
		l4.addStation(new Station(24, "Nice, Gare de Nice-Ville", "Avenue Thiers", "06000", "Nice", "FRANCE",43.704612,7.2597443, "La gare de Nice-Ville (localement nommée gare Thiers) est une gare ferroviaire française de la ligne de Marseille-Saint-Charles à Vintimille (frontière), située dans le quartier Thiers à proximité du centre-ville de Nice, préfecture du département des Alpes-Maritimes, en région Provence-Alpes-Côte d'Azur (PACA). C'est la principale gare de la ville. <br> Elle est mise en service en 1865 par la Compagnie des chemins de fer de Paris à Lyon et à la Méditerranée (PLM) et devient en 1928 une gare de bifurcation avec l'ouverture de la ligne de Nice à Breil-sur-Roya. <br> C'est une gare de la Société nationale des chemins de fer français (SNCF), desservie par des trains de grandes lignes (trains internationaux vers Milan et Moscou, TGV vers Paris, Bruxelles, Monaco, Vintimille et des grandes villes de France, Intercités vers certaines métropoles régionales), et par des trains régionaux (TER Provence-Alpes-Côte d'Azur)."), 9, 129);
		
		l5.addStation(new Station(25, "La Rochelle, Gare de La Rochelle-Ville", "Place Pierre Semard", "17000", "La Rochelle", "FRANCE",46.152642,-1.1475027, "La gare de La Rochelle-Ville est une gare ferroviaire, située sur le territoire de la commune de La Rochelle, dans le département de la Charente-Maritime, en région Aquitaine-Limousin-Poitou-Charentes. <br> Elle fait partie de la ligne de Nantes-Orléans à Saintes et est le terminus de la ligne en provenance de Saint-Benoît (gare située près de Poitiers et qui permet les liaisons avec Paris) ainsi que l'origine de la ligne de La Rochelle-Ville à La Rochelle-Pallice qui dessert le grand port maritime de La Rochelle. <br> Gare de la Société nationale des chemins de fer français (SNCF), elle permet des liaisons TGV quotidiennes vers Paris, Poitiers et Tours, et des liaisons Intercités vers Bordeaux et Nantes. La ligne TGV, inaugurée en 1993, met La Rochelle à trois heures de Paris."), 1, 100);
		l5.addStation(new Station(26, "Limoges, Gare de Limoges-Bénédictins", "Place Maison Dieu", "87036", "Limoges", "FRANCE",45.8362619,1.2653575, "La gare de Limoges-Bénédictins est une gare ferroviaire française, la principale des deux gares de la commune de Limoges, dans le département de la Haute-Vienne, en région Aquitaine-Limousin-Poitou-Charentes. <br> Principal nœud de la région Limousin avec 2,5 millions de voyageurs annuels, elle est située au cœur d'une étoile ferroviaire à huit branches, au carrefour de quatre lignes la reliant à Paris via Châteauroux et Orléans, Toulouse via Brive et Montauban, Poitiers via Le Dorat, Angoulême et Périgueux. Elle est également une gare routière régionale. <br> Ouverte en 1856 par la Compagnie du chemin de fer de Paris à Orléans, c'est aujourd'hui une gare de la Société nationale des chemins de fer français (SNCF) desservie par les trains des réseaux Intercités, TER Centre-Val de Loire, TER Limousin et TER Poitou-Charentes."), 3, 93);
		l5.addStation(new Station(27, "Clermont-Ferrand, Gare de Clermont-Ferrand", "Avenue de l'Union Soviétique", "63000", "Clermont-Ferrand", "FRANCE",45.7788661,3.0984426, "La gare de Clermont-Ferrand est une gare ferroviaire française, de la ligne de Saint-Germain-des-Fossés à Nîmes-Courbessac, située à proximité du centre de la ville de Clermont-Ferrand dans le département du Puy-de-Dôme et la région administrative Auvergne-Rhône-Alpes. C'est une gare de la Société nationale des chemins de fer français (SNCF) desservie par des trains Intercités et TER Auvergne. <br> La gare est située à l'écart du centre-ville et du plateau central, sur le site du Château-Rouge. Le quartier est toutefois « enclavé par rapport au reste du tissu urbain, par de très fortes coupures engendrées par le faisceau ferroviaire » s'étendant de l'avenue Édouard-Michelin, à sens unique (entrée), jusqu'à la zone industrielle du Brézet à l'est. En revanche, il « occupe une situation péricentrale, […] à proximité du centre-ville […] et des grands boulevards de ceinture », et bénéficie « d'une bonne accessibilité routière »."), 4, 131);
		l5.addStation(new Station(28, "Grenoble, Gare de Grenoble", "Place de la Gare", "38000", "Grenoble", "FRANCE",45.191506,5.7122818, "La gare de Grenoble est une gare ferroviaire française située sur la commune de Grenoble dans le département de l'Isère en région Auvergne-Rhône-Alpes. En tout, l'agglomération grenobloise compte sept autres gares, celles d'Échirolles, de Grenoble-Universités-Gières, de Jarrie-Vizille, de Pont-de-Claix, de Saint-Égrève-Saint-Robert, de Saint-Georges-de-Commiers et de Vif. <br> La gare de Grenoble est située au point kilométrique (PK) 130,538 (altitude 212 m de la ligne Lyon-Perrache - Marseille-Saint-Charles (Via Grenoble). Elle est également l'origine de la ligne Grenoble - Montmélian."), 6, 93);
		
		l6.addStation(new Station(29, "Saint-Étienne, Gare de Saint-Etienne-Châteaucreux", "Esplanade de France", "42000", "Saint-Etienne", "FRANCE",45.4434262,4.3972351, "La gare de Saint-Étienne-Châteaucreux est une gare ferroviaire française de la ligne de Moret - Veneux-les-Sablons à Lyon-Perrache, situé à Châteaucreux, quartier légèrement excentré de la ville de Saint-Étienne, dans le département de la Loire, en région Rhône-Alpes. <br> Elle est mise en service en 1857 par la Compagnie du chemin de fer Grand-Central de France. Au début des années 2010, c'est une gare de la Société nationale des chemins de fer français (SNCF) desservie par des TGV qui effectuent des missions entre Paris Gare de Lyon et Saint-Étienne, via Lyon-Part-Dieu. C'est également une gare régionale du réseau TER Rhône-Alpes. <br> Principale gare voyageurs de la ville et de la Communauté d'agglomération Saint-Étienne Métropole, elle permet des correspondances avec les autres transports en commun de Saint-Étienne que sont le tramway, les bus et trolleybus gérés par la Société de transports de l'agglomération stéphanoise."), 1, 100);
		l6.addStation(new Station(30, "Annecy, Gare d'Annecy", "Place de la gare", "74000", "Annecy", "FRANCE",45.901763,6.1207468, "La gare d'Annecy est une gare ferroviaire française des lignes d'Aix-les-Bains-Le Revard à Annemasse et d'Annecy à Albertville, située dans la ville d'Annecy, préfecture du département de la Haute-Savoie en région Rhône-Alpes. C'est une gare voyageurs de la Société nationale des chemins de fer français (SNCF) desservie par des trains grandes lignes TGV et Intercités, c'est également une gare régionale du réseau TER Rhône-Alpes desservie par des trains express régionaux. <br> La gare de bifurcation d'Annecy est située au point kilométrique (PK) 39,5942 de la ligne d'Aix-les-Bains-Le Revard à Annemasse, entre les gares ouvertes de Rumilly et de Pringy. Elle est aussi l'origine de la ligne d'Annecy à Albertville partiellement déclassée."), 3, 104);
		l6.addStation(new Station(31, "Genève, Gare de Genève-Cornavin", "Place de Cornavin", "1203", "Gen�ve", "SUISSE",46.2105273,6.140853, "La gare de Genève, communément appelée gare Cornavin, est la principale gare ferroviaire du canton de Genève, avant celles de Genève-Aéroport, Genève-Eaux-Vives (en reconstruction), Genève-Sécheron et Lancy-Pont-Rouge. Neuvième gare de Suisse, Cornavin voit passer 65 000 passagers1 et 720 trains par jour. <br> La gare de Genève-Cornavin est le passage ou le terminus de nombreux trains suisses des CFF, mais aussi de trains français de la SNCF avec les TGV et les TER de la région Rhône-Alpes. La gare est située aux limites des quartiers de Saint-Gervais, des Grottes et des Pâquis. La place homonyme située à l'avant du bâtiment principal est un carrefour important des Transports publics genevois (TPG) où se croisent tramways, bus, taxis et vélos, parfois de manière un peu chaotique."), 4, 28);
		
		l7.addStation(new Station(32, "Toulouse, Gare de Toulouse-Matabiau", "Boulevard Pierre Semard", "31079", "Toulouse", "FRANCE",43.6112684,1.4515309, "La gare de Toulouse-Matabiau est une gare ferroviaire française des lignes : de Bordeaux-Saint-Jean à Sète, de Toulouse à Bayonne et de Brive-la-Gaillarde à Toulouse-Matabiau via Capdenac. Elle est située à proximité du centre de la ville de Toulouse, dans le département de la Haute-Garonne, en région Languedoc-Roussillon-Midi-Pyrénées. <br> Elle est l'élément central du réseau ferroviaire de Toulouse. En outre, la gare routière de Toulouse, située à proximité, permet d'effectuer les correspondances avec de nombreuses lignes d'autocars. Son bâtiment voyageurs fait l’objet d’une inscription au titre des monuments historiques depuis le 28 décembre 1984."), 2, 204);
		
		l8.addStation(new Station(33, "Nîmes, Gare de Nîmes", "Boulevard Sergent Triaire", "30000", "Nîmes", "FRANCE",43.83251,4.3640123, "La gare de Nîmes est une gare ferroviaire française de la ligne de Tarascon à Sète-Ville, située sur le territoire de la commune de Nîmes, préfecture du département du Gard, en région Languedoc-Roussillon. <br> La première gare est établie en 1839 à l'est de la ville par la Compagnie des Mines de la Grand'Combe et des chemins de fer du Gard a été remplacée par l'actuelle gare construite postérieurement. C'est en 2014 une gare du réseau de la Société nationale des chemins de fer français (SNCF), desservie par le TGV, des trains Intercités et TER Languedoc-Roussillon."), 2, 38);
		l8.addStation(new Station(34, "Montpellier, Gare de Montpellier-Saint-Roch", "Place Auguste Gibert", "34000", "Montpellier", "FRANCE",43.6044652,3.8786331, "La gare de Montpellier-Saint-Roch est une gare ferroviaire française située sur le territoire de la commune de Montpellier dans le département de l'Hérault, en région Languedoc-Roussillon-Midi-Pyrénées. Elle accueille des trains de la SNCF. <br> Jusque dans les années 2000, elle était juste appelée la gare de Montpellier, ou gare PLM (de la compagnie des chemins de fer de Paris à Lyon et à la Méditerranée) par les amateurs d'histoire. Vers 2003, Georges Frêche a commencé à utiliser le terme de Gare Saint-Roch dans les institutions qu'il dirigeait : mairie de Montpellier, communauté d'agglomération de Montpellier et réseau de transports publics TaM. <br>Devenu président de la région Languedoc-Roussillon en 2004, il a pu faire modifier le nom officiel de la gare le 31 mars 2005. Le nom rappelle saint Roch, natif de la ville au xive siècle, le quartier du centre-ville situé autour de l'église Saint Roch, et l'opération immobilière Nouveau Saint-Roch sur les terrains de l'ancienne gare de marchandises."), 3, 44);
		
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
