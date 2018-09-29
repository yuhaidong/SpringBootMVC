package com.yhd.project.springbootmvc.module.login.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class User implements Serializable {

	private static final long serialVersionUID = -7898194272883238670L;
	
	private String id;
	private String loginName;
	private String password;
	
	//private String salt;
	

}
