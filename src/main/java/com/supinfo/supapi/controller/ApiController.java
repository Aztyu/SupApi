package com.supinfo.supapi.controller;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.supinfo.supapi.entity.Reservation;
import com.supinfo.supapi.entity.Response;
import com.supinfo.supapi.entity.Travel;
import com.supinfo.supapi.interfaces.job.IRailJob;

@Controller
public class ApiController {
	@Autowired
	IRailJob rail_job;
	
	@RequestMapping(value = "/reservations/{user_id}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody Response getReservations(Model model, HttpServletRequest request, @PathVariable long user_id) {
		Response resp = new Response();
		try{
			List<Reservation> r = rail_job.getReservations(user_id);
			if(r == null){
				throw new Exception();
			}
			
			resp.setReservations(r);
			resp.setHtml_status(200);
			resp.setHtml_message("OK");	
			return resp;
		}catch(Exception ex){
			resp.setHtml_status(401);
			resp.setHtml_message("OK");
			ex.printStackTrace();
			return resp;
		}
	}
}
