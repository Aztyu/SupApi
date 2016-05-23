package com.supinfo.supapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}
