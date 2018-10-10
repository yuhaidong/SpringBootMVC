package com.yhd.project.springbootmvc.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CaptchaValidateUtil {

	public static boolean validateResponse(HttpServletRequest request,
			HttpServletResponse response) {

		boolean validated = false;

		response.setContentType("text/html;charset=utf-8");
		// 显示的验证码
		String validateC = (String) request.getSession().getAttribute(
				"verCode");
		// 输入的验证码
		String veryCode = request.getParameter("captcha");
		
		if (veryCode == null || "".equals(veryCode)) {
			// 输入的验证码字符为空
			validated = false;
		} else if (validateC == null || "".equals(validateC)) {
			// 显示的验证码有误，为空
			validated = false;
		} else {
			
			if (validateC.equals(veryCode)) {
				// 验证码正确
				validated = true;
			} else {
				// 验证码错误
				validated = false;
			}
		}

		return validated;
	}

}
