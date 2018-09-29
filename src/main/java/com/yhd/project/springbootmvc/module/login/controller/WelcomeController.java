package com.yhd.project.springbootmvc.module.login.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yhd.project.springbootmvc.module.login.entity.User;
import com.yhd.project.springbootmvc.module.login.service.UserService;

@Controller
public class WelcomeController {
	
	@Autowired
	private UserService userService;

	/**
	 * 登录到首页
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/index")
	public String index(HttpServletRequest request, ModelMap model) {
		
		return "index";
	}
	
	/**
	 * 返回登录用户信息
	 * 这里注解@ResponseBody是为了让函数返回对象，而不是跳转页面
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param modelMap
	 * @return
	 */
	@RequestMapping("/userlist")
	@ResponseBody
	public Map<String,Object> userlist(HttpServletRequest request,
			HttpServletResponse response, String userName, ModelMap modelMap) {

		String login_name = request.getSession().getAttribute("username")
				.toString();
		User user = userService.findByUserName(login_name);

		Map<String,Object> map = new HashMap<>();
		map.put("login_name", user.getLoginName());
		
		return map;
		
	}
}
