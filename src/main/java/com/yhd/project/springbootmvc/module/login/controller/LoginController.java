package com.yhd.project.springbootmvc.module.login.controller;

import static org.apache.shiro.web.filter.authc.FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//@RestController
@Controller
public class LoginController {

	/**
	 * 登录页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/login", method= RequestMethod.GET)
	public String login() {
		
		return "login";
	}
	
	/**
	 * 如果登录不成功，shiro会请求转发到这个action，然后再跳到登录页面
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws IOException
	 */
	//@ResponseBody()
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String submit(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) throws IOException {
		
		System.out.println("登录不成功！");
		
		Object error = request.getAttribute(DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
		if (error != null) {
			model.addAttribute("error", error);
		}
		
		return "login";
	}
	
	@RequestMapping("/403")
    public String hello2(Model model) {

		model.addAttribute("name", "张三");
		
        return "403";
    }
	
	@RequestMapping("/hello")
    public String hello(Model model) {

		model.addAttribute("name", "张三");
		
        return "test";
    }
}
