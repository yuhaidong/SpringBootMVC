package com.yhd.project.springbootmvc.common.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import com.yhd.project.springbootmvc.common.exception.CaptchaErrorException;
import com.yhd.project.springbootmvc.common.util.CaptchaValidateUtil;

/**
 * formAuthenticationFilter为基于Form表单的身份验证过滤器；
 * 此处的FlxoaAuthenticationFilter为自行定义的Filter bean
 * 
 * @author Yu Haidong
 *
 */
public class CustomFormAuthenticationFilter extends FormAuthenticationFilter {

	/**
	 * 返回URL
	 */
	public static final String RETURN_URL = "returnUrl";
	
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		AuthenticationToken token = createToken(request, response);
		
		if (token == null) {
			String msg = "create AuthenticationToken error";
			throw new IllegalStateException(msg);
		}
		
		HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
		HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
		String username = (String) token.getPrincipal();
		boolean adminLogin=false;
		
		//判断是否需要有验证码进行验证
		if (captchaEnable) {
			if (!CaptchaValidateUtil.validateResponse(httpServletRequest, httpServletResponse)) {
				return onLoginFailure(token,adminLogin,new CaptchaErrorException(), request, response);
			}
		}
		
		//判断是否开启从白名单中读取允许登录后台的IP的功能
		
		
		try {
			Subject subject = getSubject(request, response);
			subject.login(token);
			return onLoginSuccess(token,adminLogin,subject, request, response);
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return onLoginFailure(token,adminLogin, e, request, response);
		}
	}
	
	/**
	 * 登录失败
	 */
	private boolean onLoginFailure(AuthenticationToken token,boolean adminLogin,AuthenticationException e, ServletRequest request,
			ServletResponse response) {
		
		return super.onLoginFailure(token, e, request, response);
	}
	
	/**
	 * 登录成功
	 */
	private boolean onLoginSuccess(AuthenticationToken token,boolean adminLogin,Subject subject,
			ServletRequest request, ServletResponse response)
			throws Exception {
		
		System.out.println("登陆成功！");
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String username = (String) subject.getPrincipal();
//		CmsUser user = cmsUserMng.findByUsername(username);
//		String ip = RequestUtils.getIpAddr(req);
//		userMng.updateLoginInfo(user.getId(), ip);
//		//管理登录
//		if(adminLogin){
//			cmsLogMng.loginSuccess(req, user);
//		}
//		// 清除需要验证码cookie
//		removeCookieErrorRemaining(req, res);
		
		UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)token;
		String login_name = usernamePasswordToken.getUsername();
		HttpSession session = req.getSession();
		session.setAttribute("login_name", login_name);
		
		return super.onLoginSuccess(token, subject, request, response);
	}
	
//	@Override
//	protected boolean isLoginRequest(ServletRequest req, ServletResponse resp) {
//		// 其中的loginUrl和adminUrl都是在shiro-context.xml中所配置的值
//		return pathsMatch(getLoginUrl(), req)
//				|| pathsMatch(getAdminLogin(), req);
//	}

	public boolean onPreHandle(ServletRequest request,
			ServletResponse response, Object mappedValue) throws Exception {
		boolean isAllowed = isAccessAllowed(request, response, mappedValue);
		
		// 登录跳转，即如果已经登录成功了，则不用再次登录
		if (isAllowed && isLoginRequest(request, response)) {
			 try {
				 
				 issueSuccessRedirect(request, response);
				 
			 } catch (Exception e) {
				 e.printStackTrace();
				 //logger.error("", e);
			 }
			return false;
		}
		return isAllowed || onAccessDenied(request, response, mappedValue);
	}

	@Override
	protected void issueSuccessRedirect(ServletRequest request,
			ServletResponse response) throws Exception {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String successUrl = req.getParameter(RETURN_URL);
		if (StringUtils.isBlank(successUrl)) {
			if (req.getRequestURI().startsWith(
					req.getContextPath() + getAdminPrefix())) {
				// 后台直接返回首页
				successUrl = getAdminIndex();
				// 清除SavedRequest
				WebUtils.getAndClearSavedRequest(request);
				WebUtils.issueRedirect(request, response, successUrl, null,
						true);
				return;
			} else {
				successUrl = getSuccessUrl();
			}
		}
		WebUtils.redirectToSavedRequest(req, res, successUrl);
	}
	
	private String adminPrefix;
	private String adminIndex;
	private String adminLogin;
	private boolean captchaEnable;//是否开启验证码支持
	private boolean loginWhiteListEnable;//是否开启登录白名单支持
	
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
	
	public boolean isCaptchaEnable() {
		return captchaEnable;
	}

	public void setCaptchaEnable(boolean captchaEnable) {
		this.captchaEnable = captchaEnable;
	}
	
	public boolean isLoginWhiteListEnable() {
		return loginWhiteListEnable;
	}

	public void setLoginWhiteListEnable(boolean loginWhiteListEnable) {
		this.loginWhiteListEnable = loginWhiteListEnable;
	}
}
