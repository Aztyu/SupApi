package com.supinfo.supapi.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.supinfo.supapi.database.PersistenceManager;
import com.supinfo.supapi.entity.Line;
import com.supinfo.supapi.entity.Reservation;
import com.supinfo.supapi.entity.Station;
import com.supinfo.supapi.entity.Train;
import com.supinfo.supapi.entity.TrainTrip;
import com.supinfo.supapi.entity.User;
import com.supinfo.supapi.enumeration.Sens;
import com.supinfo.supapi.interfaces.dao.IRailDao;
import com.supinfo.supapi.interfaces.dao.ITrainDao;
import com.supinfo.supapi.utils.Pair;

public class RailDao implements IRailDao{

	//Train
	
	@Override
	public void createLine(Line line) {
		try{
			EntityManager em = PersistenceManager.getEntityManager();
	        EntityTransaction et = em.getTransaction();
	        et.begin();
	        em.merge(line);
	        et.commit();
	        em.close();
		}catch(Exception ex){
			ex.printStackTrace();
			String toto = ex.getMessage();
		}
	}

	//Station
	
	@Override
	public List<Station> searchStationByName(String search) {
		EntityManager em = PersistenceManager.getEntityManager();
		Query query = em.createQuery("SELECT s FROM Station AS s WHERE s.name LIKE :search");
		query.setParameter("search", "%" + search + "%");
		List<Station> stations = query.getResultList();
		return stations;
	}
	
	@Override
	public Station findStation(long station_id) {
		EntityManager em = PersistenceManager.getEntityManager();
		Query query = em.createQuery("SELECT s FROM Station AS s WHERE s.id = :station_id");
		query.setParameter("station_id", station_id);
		List results = query.getResultList();
		if(results.isEmpty()){
			return null;
		}else{
			return (Station)results.get(0);
		}
	}
	
	@Override
	public List<Station> getStations() {
		EntityManager em = PersistenceManager.getEntityManager();
		Query query = em.createQuery("SELECT s FROM Station AS s");
		List<Station> stations = query.getResultList();
		return stations;
	}
	
	@Override
	public long getDistanceforLine(long departure_id, long arrival_id, long line_id, Sens sens) {
		EntityManager em = PersistenceManager.getEntityManager();
		
		Query query; 
		if(sens == Sens.ALLER){
			query = em.createQuery("SELECT SUM(sa.distance) FROM StationLineAssociation AS sa WHERE sa.line.id = :line_id AND sa.station_order > :departure_id AND sa.station_order <= :arrival_id ORDER BY sa.station_order ASC");
		}else{
			query = em.createQuery("SELECT SUM(sa.distance) FROM StationLineAssociation AS sa WHERE sa.line.id = :line_id AND sa.station_order <= :departure_id AND sa.station_order > :arrival_id ORDER BY sa.station_order ASC");
		}
		query.setParameter("line_id", line_id);
		query.setParameter("departure_id", departure_id);
		query.setParameter("arrival_id", arrival_id);
		List results = query.getResultList();
		if(results == null || results.isEmpty()){
			return (long) 0.0;
		}else{
			Long distance = (Long)results.get(0);
			return (long) ((distance != null)?distance:0.0);
		}
	}
	
	@Override
	public TrainTrip findTrainTrip(Line line, Date date_down, Date date_up, Sens sens) {
		EntityManager em = PersistenceManager.getEntityManager();
		
		Query query; 
		if(sens == Sens.ALLER){
			query = em.createQuery("SELECT tt FROM TrainTrip AS tt WHERE tt.aller = TRUE AND tt.departure_date <= :end AND tt.departure_date >= :start");
		}else{
			query = em.createQuery("SELECT tt FROM TrainTrip AS tt WHERE tt.aller = FALSE AND tt.departure_date < :end AND tt.departure_date > :start");
		}
		query.setParameter("start", date_down);
		query.setParameter("end", date_up);
		
		List results = query.getResultList();
		if(results == null || results.isEmpty()){
			return null;
		}else{
			return (TrainTrip) results.get(0);
		}
	}
	
	@Override
	public Train findAvailableTrain(Line line, Sens sens, Calendar cal_down, Calendar cal_up) {
		EntityManager em = PersistenceManager.getEntityManager();
		
		Query query;
		query = em.createQuery("SELECT t FROM Train as t LEFT JOIN t.trips tt WHERE ((tt.departure_date <= :date_start OR tt.departure_date >= :date_end) OR tt.id IS NULL) AND t.line_id = :line_id GROUP BY t.id order by sum(tt.id) asc");
		query.setParameter("date_start", cal_down.getTime());
		query.setParameter("date_end", cal_up.getTime());
		query.setParameter("line_id", line.getId());
		query.setMaxResults(1);
		
		List results = query.getResultList();
		if(results == null || results.isEmpty()){
			return null;
		}else{
			return (Train) results.get(0);
		}
	}
	
	@Override
	public void createTrain(Train train) {
		EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.persist(train);
        et.commit();
        em.close();
	}
	
	@Override
	public void createTrainTrip(TrainTrip tt) {
		EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.merge(tt);
        et.commit();
        em.close();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Station> getNodeStations() {
		EntityManager em = PersistenceManager.getEntityManager();
		Query query = em.createQuery("SELECT s FROM Station as s LEFT JOIN s.lines sla GROUP BY s.id HAVING count(sla.id) > 1");
		List<Station> stations = query.getResultList();
		return stations;
	}
	
	@Override
	public void saveReservation(Reservation reserv){
		EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.merge(reserv);
        et.commit();
        em.close();
	}
}
