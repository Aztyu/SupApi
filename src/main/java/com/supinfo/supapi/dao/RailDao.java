package com.supinfo.supapi.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.supinfo.supapi.database.PersistenceManager;
import com.supinfo.supapi.entity.Line;
import com.supinfo.supapi.entity.Station;
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
			query = em.createQuery("SELECT SUM(sa.distance) FROM StationLineAssociation AS sa WHERE sa.line.id = :line_id AND sa.station.id > :departure_id AND sa.station.id <= :arrival_id ORDER BY sa.station.id ASC");
		}else{
			query = em.createQuery("SELECT SUM(sa.distance) FROM StationLineAssociation AS sa WHERE sa.line.id = :line_id AND sa.station.id >= :departure_id AND sa.station.id < :arrival_id ORDER BY sa.station.id ASC");
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
	public void getStationsFromStartToEndonLine(long id, long departure_id, long arrival_id) {
		// TODO Auto-generated method stub
		
	}
}
