package com.yhd.project.springbootmvc.module.login.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.yhd.project.springbootmvc.module.login.dao.UserMapper;
import com.yhd.project.springbootmvc.module.login.entity.User;
import com.yhd.project.springbootmvc.module.login.service.UserService;

@Service
@Lazy
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;
	
	@Override
	public User findByAccount(String account) {
		
		User user = userMapper.findByAccount(account);
		
		return user;
	}

//	@Override
//	public User updateUser(User user) {
//		
//		User currentUser = userMapper.updateUser(user);
//		
//		return currentUser;
//	}

}
