package com.supinfo.supapi.interfaces.job;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import com.supinfo.supapi.entity.User;

public interface IUserJob {
    public User getUser(String login, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    public User getUserById(Long id, String password) throws Exception;
	public void createUser(User user, String password) throws Exception;
	public void editUser(User user, String password) throws Exception;
	public User getUserFromGoogle(String google_id) throws GeneralSecurityException, IOException, Exception;
	public User getUserFromFacebook(String id, String email, String name) throws GeneralSecurityException, IOException, Exception;
}
