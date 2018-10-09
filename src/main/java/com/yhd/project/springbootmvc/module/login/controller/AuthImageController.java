package com.yhd.project.springbootmvc.module.login.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yhd.project.springbootmvc.common.util.VerifyCodeUtils;

/**
 * 类名称：AuthImageController.java 
 * 类描述：验证码 
 * 创建时间：2018-4-3, 上午11:30:49
 * 
 * @version 1.0
 * @since JDK版本 1.8
 * @author yhd
 */
@Controller
public class AuthImageController {

	@RequestMapping(value = "/authImage", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String AuthImage(HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws ServletException, IOException {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");

		// 生成随机字串
		String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
		System.out.println("随机生成的验证码为：" + verifyCode);
		// 存入会话session
		HttpSession session = request.getSession(true);
		// 删除以前的
		session.removeAttribute("verCode");
		session.setAttribute("verCode", verifyCode.toLowerCase());

		// 将验证码存放到cookie中
		// Cookie cookie=new Cookie("verCode", verifyCode.toLowerCase());
		Cookie cookie = new Cookie("verCode", verifyCode);
		response.addCookie(cookie);

		// 生成图片
		int w = 100, h = 28;
		String base64 = VerifyCodeUtils.outputImageBase64(w, h, verifyCode);
		System.out.println(base64);
		return base64;
	}
}
