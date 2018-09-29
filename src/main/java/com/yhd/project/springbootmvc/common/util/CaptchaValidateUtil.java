package com.yhd.project.springbootmvc.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CaptchaValidateUtil {

	public static boolean validateResponse(HttpServletRequest request,
			HttpServletResponse response) {

		boolean validated = false;

		response.setContentType("text/html;charset=utf-8");
		String validateC = (String) request.getSession().getAttribute(
				"validateCode");
		String veryCode = request.getParameter("captcha");
		if (veryCode == null || "".equals(veryCode)) {
			// 验证码为空
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
