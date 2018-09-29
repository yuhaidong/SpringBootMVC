package com.yhd.project.springbootmvc.module.login.dao;

import com.yhd.project.springbootmvc.module.login.entity.User;

public interface UserMapper {

	public User findByUserName(String username);

	public User updateUser(User user);
}
