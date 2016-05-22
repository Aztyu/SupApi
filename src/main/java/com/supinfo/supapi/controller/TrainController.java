package com.supinfo.supapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.supinfo.supapi.interfaces.job.ITrainJob;


@Controller
public class TrainController {
	
	@Autowired
    ITrainJob train_job;
	
	@RequestMapping(value = "/rail/init", method = RequestMethod.GET)
	public String mainUserPage(Model model) {
		train_job.initRail();
		
		return "main";
	}
}
