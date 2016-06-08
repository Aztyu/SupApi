package com.supinfo.supapi.job;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.supinfo.supapi.entity.User;
import com.supinfo.supapi.interfaces.dao.IUserDao;
import com.supinfo.supapi.interfaces.job.IUserJob;
import com.supinfo.supapi.utils.BaseUtil;

public class UserJob implements IUserJob{
	
	@Autowired
	private IUserDao dao;

	@Override
	public void createUser(User user, String password) throws Exception {
		try {
			String salt = BaseUtil.generateSalt();
	        String hash = BaseUtil.getSaltedPassword(salt, password);
		
	        user.setSalt(salt);
	        user.setPassword(hash);
	        
	        if(!dao.userExists(user.getEmail())){
	        	dao.createUser(user);
	        }else{
	        	throw new Exception();
	        }
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void editUser(User user, String password) throws Exception {
		if (dao.userCheck(user.getId(), password) == true) {
			dao.updateUser(user);
		}
	}

	@Override
	public User getUser(String login, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		String salted = BaseUtil.getSaltedPassword(dao.getUserSalt(login), password);
		return dao.getUser(login, salted);
	}
	
	@Override
	public User getUserById(Long id, String password) throws Exception {
		return dao.getUserById(id, password);
	}
	
	@Override
	public User getUserFromGoogle(String google_id) throws Exception{
		NetHttpTransport transport = new NetHttpTransport();
		GsonFactory jsonFactory = new GsonFactory();
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory )
			    .setAudience(Arrays.asList("62410611142-3mese73l1crnh0hc3p5rdr4k0eicdpb5.apps.googleusercontent.com"))
			    .setIssuer("accounts.google.com")
			    .build();
		
		GoogleIdToken idToken = verifier.verify(google_id);
		
		if (idToken != null) {
			Payload payload = idToken.getPayload();
			String email = payload.getEmail();
			
			User user = dao.getGoogleUser(email);
			
			if(user == null){
				user = new User();
				user.setGoogleUser(true);
				user.setEmail(email);
				
				String salt = BaseUtil.generateSalt();
		        String hash = BaseUtil.getSaltedPassword(salt, BaseUtil.generatePassword());
			
		        user.setSalt(salt);
		        user.setPassword(hash);
		        
				user.setFirstName((String) payload.get("name"));
				user.setLastName((String) payload.get("family_name"));
			
				if(!dao.userExists(user.getEmail())){
		        	dao.createUser(user);
		        }else{
		        	throw new Exception();
		        }
			}
			
		  	return user;
		} else {
			return null;
		}
	}

	@Override
	public User getUserFromFacebook(String id, String email, String name) throws Exception {
		
		User user = dao.getFacebookUser(email);
		
		if(user == null){
			user = new User();
			user.setFacebookUser(true);
			user.setEmail(email);
			
			String salt = BaseUtil.generateSalt();
	        String hash = BaseUtil.getSaltedPassword(salt, BaseUtil.generatePassword());
		
	        
			user.setFirstName(name);
			user.setLastName(name);
			
	        user.setSalt(salt);
	        user.setPassword(hash);
		
			if(!dao.userExists(user.getEmail())){
	        	dao.createUser(user);
	        }else{
	        	throw new Exception();
	        }
		}
	  	return user;
	}
}
