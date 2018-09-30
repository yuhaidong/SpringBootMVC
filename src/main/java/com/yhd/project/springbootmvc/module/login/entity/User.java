package com.yhd.project.springbootmvc.module.login.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class User implements Serializable {

	private static final long serialVersionUID = -7898194272883238670L;
	
	private Long id;
	private String account;
	private String password;
	
	private String displayName;
	private String discription;
	private Date updateTime;
	
	//private String salt;
	

}
