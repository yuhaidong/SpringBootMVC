package com.yhd.project.springbootmvc.common.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import com.yhd.project.springbootmvc.common.util.CaptchaValidateUtil;

public class CaptchaValidateFilter extends AccessControlFilter {

	private boolean jcaptchaEbabled = true;// 是否开启验证码支持
	private String failureKeyAttribute = "shiroLoginFailure"; //验证失败后存储到的属性名
	
	public void setJcaptchaEbabled(boolean jcaptchaEbabled) {  
        this.jcaptchaEbabled = jcaptchaEbabled;  
    }  
    public void setFailureKeyAttribute(String failureKeyAttribute) {  
        this.failureKeyAttribute = failureKeyAttribute;  
    } 

	@Override
	protected boolean isAccessAllowed(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {

		// 1、设置验证码是否开启属性，页面可以根据该属性来决定是否显示验证码
		request.setAttribute("jcaptchaEbabled", jcaptchaEbabled);

		HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
		HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
		// 2、判断验证码是否禁用 或不是表单提交（允许访问）
		if (jcaptchaEbabled == false
				|| !"post".equalsIgnoreCase(httpServletRequest.getMethod())) {
			return true;
		}

		return CaptchaValidateUtil.validateResponse(httpServletRequest, httpServletResponse);
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		
		//如果验证码失败了，存储失败key属性  
        request.setAttribute(failureKeyAttribute, "jCaptcha.error");  
		
//		return false;
		return true;
	}

	@Override
	public void setLoginUrl(String loginUrl) {
		// TODO Auto-generated method stub
		super.setLoginUrl(loginUrl);
	}
	
	private String adminPrefix;
	private String adminIndex;
	private String adminLogin;

	public String getAdminPrefix() {
		return adminPrefix;
	}

	public void setAdminPrefix(String adminPrefix) {
		this.adminPrefix = adminPrefix;
	}

	public String getAdminIndex() {
		return adminIndex;
	}

	public void setAdminIndex(String adminIndex) {
		this.adminIndex = adminIndex;
	}

	public String getAdminLogin() {
		return adminLogin;
	}

	public void setAdminLogin(String adminLogin) {
		this.adminLogin = adminLogin;
	}

}
