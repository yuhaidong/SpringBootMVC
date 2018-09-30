package com.yhd.project.springbootmvc.module.login.dao;

import com.yhd.project.springbootmvc.module.login.entity.User;

public interface UserMapper {

	public User findByAccount(String account);

	public User updateUser(User user);
}
