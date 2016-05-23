package com.supinfo.supapi.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.supinfo.supapi.entity.Response;
import com.supinfo.supapi.entity.Station;
import com.supinfo.supapi.interfaces.job.IRailJob;
import com.supinfo.supapi.interfaces.job.ITrainJob;


@Controller
public class TravelController {
	
	@Autowired
    IRailJob rail_job;
	
	@RequestMapping(value = "/travel/find", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Response searchStationByName(Model model, HttpServletRequest request) {
		Response resp = new Response();
		try{
			String departure_id = request.getParameter("departure_id");
			String departure_time = request.getParameter("departure_time");
			
			String arrival_id = request.getParameter("departure_id");
			String arrival_time = request.getParameter("departure_time");
			
			rail_job.findTravel(departure_id, departure_time, arrival_id, arrival_time);
			
			resp.setHtml_status(200);
			resp.setHtml_message("OK");
			//resp.setStations(stations);
			
			return resp;
		}catch(Exception ex){
			resp.setHtml_status(401);
			resp.setHtml_message("OK");
			return resp;
		}
	}
}
