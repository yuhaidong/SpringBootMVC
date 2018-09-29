package com.yhd.project.springbootmvc.module.login.service;

import com.yhd.project.springbootmvc.module.login.entity.User;

public interface UserService {

	public User findByUserName(String username);
	
//	public User updateUser(User user);
}
