package com.supinfo.supapi.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.supinfo.supapi.entity.Reservation;
import com.supinfo.supapi.entity.Response;
import com.supinfo.supapi.entity.SearchStation;
import com.supinfo.supapi.entity.Station;
import com.supinfo.supapi.entity.Travel;
import com.supinfo.supapi.interfaces.job.IRailJob;
import com.supinfo.supapi.interfaces.job.ITrainJob;

@Controller
public class TravelController {
	
	@Autowired
    IRailJob rail_job;
	
	@RequestMapping(value = "/travel/find", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody Response searchStationByName(Model model, HttpServletRequest request, @RequestBody String json) {
		Response resp = new Response();
		try{
			String debug = json;
			
			ObjectMapper mapper = new ObjectMapper();
			SearchStation search_station = mapper.readValue(json, SearchStation.class);
			
			List<Travel> travel = rail_job.findTravel(search_station);
			
			resp.setTravels(travel);
			resp.setHtml_status(200);
			resp.setHtml_message("OK");	
			return resp;
		}catch(Exception ex){
			resp.setHtml_status(401);
			resp.setHtml_message("OK");
			return resp;
		}
	}
	
	@RequestMapping(value = "/travel/buy/{user_id}", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public @ResponseBody Response buyTravel(Model model, HttpServletRequest request, @PathVariable long user_id, @RequestBody String json) {
		Response resp = new Response();
		try{
			String debug = json;
			
			ObjectMapper mapper = new ObjectMapper();
			Travel travel = mapper.readValue(json, Travel.class);
			
			Reservation r = rail_job.createReservation(travel, user_id);
			if(r == null){
				throw new Exception();
			}
			
			resp.setReservation(r);
			resp.setHtml_status(200);
			resp.setHtml_message("OK");	
			return resp;
		}catch(Exception ex){
			resp.setHtml_status(401);
			resp.setHtml_message("OK");
			return resp;
		}
	}
}
