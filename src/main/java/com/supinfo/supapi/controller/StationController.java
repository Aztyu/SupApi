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

@Controller
public class StationController {
	@Autowired
	IRailJob rail_job;
	
	@RequestMapping(value = "/rail", method = RequestMethod.GET)
	public String mainUserPage(Model model) {
		rail_job.initRail();
		
		return "main";
	}
	
	@RequestMapping(value = "/station/search", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody Response searchStationByName(Model model, HttpServletRequest request) {
		Response resp = new Response();
		try{
			String search = request.getParameter("search");
			
			List<Station> stations = new ArrayList<Station>();
			if(search != null){
				stations.addAll(rail_job.searchStationByName(search));
			}
			
			resp.setHtml_status(200);
			resp.setHtml_message("OK");
			resp.setStations(stations);
			
			return resp;
		}catch(Exception ex){
			resp.setHtml_status(401);
			resp.setHtml_message("OK");
			return resp;
		}
	}
}
