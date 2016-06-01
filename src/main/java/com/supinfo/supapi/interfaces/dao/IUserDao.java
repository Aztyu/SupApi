package com.supinfo.supapi.interfaces.dao;

import com.supinfo.supapi.entity.User;

public interface IUserDao {
	public void createUser(User user);
    public User getUser(String login, String password);
	public String getUserSalt(String login);
	public User getGoogleUser(String email);
	public User getFacebookUser(String email);
	boolean userExists(String email);
	public boolean userCheck(long id, String password);
	public User getUserById(Long id, String password);
	public void updateUser(User user);
}
