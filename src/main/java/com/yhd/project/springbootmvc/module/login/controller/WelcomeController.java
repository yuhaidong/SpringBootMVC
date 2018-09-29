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

	@RequestMapping("/index")
	public String index(HttpServletRequest request, ModelMap model) {
		
//		String userName = request.getAttribute("login_name").toString();
		//String userName = request.getSession().getAttribute("login_name") + "";
		
		//if ("" != userName) {
		//	model.addAttribute("userName", userName);
		//}
		
		return "index";
	}
	
	@RequestMapping("/userlist")
	@ResponseBody
	public Map<String,Object> userlist(HttpServletRequest request,
			HttpServletResponse response, String userName, ModelMap modelMap) {

		String login_name = request.getSession().getAttribute("username")
				.toString();
		User user = userService.findByUserName(login_name);

		Map<String,Object> map = new HashMap<>();
		map.put("login_name", user.getLoginName());
//		modelMap.addAttribute("login_name", login_name);
		
		return map;
		
	}
}
