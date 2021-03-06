package com.supinfo.supapi.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.supinfo.supapi.entity.Response;
import com.supinfo.supapi.entity.User;
import com.supinfo.supapi.interfaces.dao.IUserDao;
import com.supinfo.supapi.interfaces.job.IUserJob;

@Controller
public class UserController {
	
	@Autowired
	IUserJob user_job;
	
	@RequestMapping(value = "/user/login", method = RequestMethod.GET)
	public @ResponseBody Response mainLogin(Model model, HttpServletRequest request) {
		Response resp = new Response();
		try{
			String login = request.getParameter("login");
			String password = request.getParameter("password");
			
			User user = user_job.getUser((String)request.getParameter("login"), (String)request.getParameter("password"));
			user.setReservations(null);
        	
			resp.setHtml_message("OK");
			resp.setHtml_status(200);
			resp.setUser(user);
			return resp;
		}catch(Exception ex){
			resp.setHtml_message("Unauthorized access");
			resp.setHtml_status(401);
			return resp;
		}
	}
	
	@RequestMapping(value = "user/login/google", method = RequestMethod.GET)
    public @ResponseBody Response loginGoogleUser(Model model,HttpServletRequest request) {
		Response resp = new Response();
		try{
        	User user = user_job.getUserFromGoogle((String)request.getParameter("google_id"));
        	user.setReservations(null);
        	
        	resp.setHtml_message("OK");
			resp.setHtml_status(200);
			resp.setUser(user);
			
			return resp;
		}catch(Exception ex){
			resp.setHtml_message("Unauthorized access");
			resp.setHtml_status(401);
			return resp;
        }		
    }
	
	@RequestMapping(value = "user/login/facebook", method = RequestMethod.GET)
    public @ResponseBody Response loginFacebookUser(Model model,HttpServletRequest request) {
		Response resp = new Response();
		try{
        	User user = user_job.getUserFromFacebook(request.getParameter("id"), request.getParameter("email"), request.getParameter("name"));
        	user.setReservations(null);
        	
        	resp.setHtml_message("OK");
			resp.setHtml_status(200);
			resp.setUser(user);
			
			return resp;
		}catch(Exception ex){
			resp.setHtml_message("Unauthorized access");
			resp.setHtml_status(401);
			return resp;
        }		
    }
	
	@RequestMapping(value = "user/register", method = RequestMethod.GET)
    public @ResponseBody Response saveUser(Model model, HttpServletRequest request){
        Response resp = new Response();
        try{
	        User u = new User();
	        
	        u.setFirstName(request.getParameter("firstname"));
	        u.setEmail(request.getParameter("email"));
	        
	        user_job.createUser(u, request.getParameter("password"));
	        
	        resp.setHtml_message("OK");
			resp.setHtml_status(200);
			resp.setUser(u);
			return resp;
        }catch(Exception ex){
        	resp.setHtml_message("Error on user creation");
			resp.setHtml_status(400);
			return resp;
        }
    }
	
	@RequestMapping(value = "user/edit", method = RequestMethod.POST)
    public @ResponseBody Response editUser(Model model, HttpServletRequest request){
        Response resp = new Response();
        try{
	        User u = user_job.getUserById(Long.parseLong(request.getParameter("id")), request.getParameter("password"));
	        
        	u.setFirstName(request.getParameter("firstname"));
            u.setLastName(request.getParameter("lastname"));
            u.setPassword(request.getParameter("password"));
            u.setCountry(request.getParameter("country"));
            u.setCity(request.getParameter("city"));
            u.setAddress(request.getParameter("address"));
            u.setZipcode(Integer.parseInt(request.getParameter("zipcode")));
            u.setPhone(Integer.parseInt(request.getParameter("phone")));
            	        
	        user_job.editUser(u, request.getParameter("password"));
	        u.setReservations(null);
	        
	        resp.setHtml_message("OK");
			resp.setHtml_status(200);
			resp.setUser(u);
			return resp;
        }catch(Exception ex){
        	resp.setHtml_message("Error on user update");
			resp.setHtml_status(400);
			return resp;
        }
    }
}
