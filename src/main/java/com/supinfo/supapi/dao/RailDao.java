package com.supinfo.supapi.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.supinfo.supapi.database.PersistenceManager;
import com.supinfo.supapi.entity.Line;
import com.supinfo.supapi.entity.Station;
import com.supinfo.supapi.interfaces.dao.IRailDao;
import com.supinfo.supapi.interfaces.dao.ITrainDao;

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
}
