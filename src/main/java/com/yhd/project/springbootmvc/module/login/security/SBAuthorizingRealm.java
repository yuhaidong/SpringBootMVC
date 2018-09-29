package com.yhd.project.springbootmvc.module.login.security;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.yhd.project.springbootmvc.module.login.entity.User;
import com.yhd.project.springbootmvc.module.login.service.UserService;

public class SBAuthorizingRealm extends AuthorizingRealm {

	/**
	 * 执行授权逻辑
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection arg0) {
		
		System.out.println("执行授权逻辑");
		
		return null;
	}

	/**
	 * 执行认证逻辑
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authToken) throws AuthenticationException {
		
		System.out.println("执行认证逻辑");
		
		UsernamePasswordToken token = (UsernamePasswordToken) authToken;
		User user = userService.findByUserName(token.getUsername());

		if (user != null) {
			
			SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getLoginName(),
					user.getPassword(), getName());
			// 当验证都通过后，把用户信息放在session里，前段页面会调用
	        Session session = SecurityUtils.getSubject().getSession();
	        //session.setAttribute("user", user);
	        session.setAttribute("username", user.getLoginName());
			
			return authenticationInfo;
		}

		return null;
	}
	
	protected UserService userService;

//	@Autowired
//	public void setFlxoaUserMng(YhdmvcUserManager yhdmvcUserManager) {
//		this.yhdmvcUserManager = yhdmvcUserManager;
//	}
	
	@Autowired
	public void setProjectmvcUserManager(UserService userService) {
		this.userService = userService;
	}

}
