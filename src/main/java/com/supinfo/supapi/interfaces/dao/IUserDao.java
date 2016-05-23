package com.supinfo.supapi.interfaces.dao;

import com.supinfo.supapi.entity.User;

public interface IUserDao {
	public void createUser(User user);
    public User getUser(String login, String password);
	public String getUserSalt(String login);
	public User getGoogleUser(String email);
	boolean userExists(String email);
}
