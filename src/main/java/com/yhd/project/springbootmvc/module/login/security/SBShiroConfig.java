package com.yhd.project.springbootmvc.module.login.security;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.Filter;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.yhd.project.springbootmvc.common.filter.CaptchaValidateFilter;
import com.yhd.project.springbootmvc.common.filter.CustomFormAuthenticationFilter;

@Configuration
public class SBShiroConfig {

	@Bean
	public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
		System.out.println("ShiroConfiguration.shirFilter()");
		
		// 创建shiro的Bean
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		
		// 设置拦截器
		Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();
		// 配置不会被拦截的链接 顺序判断
		filterChainDefinitionMap.put("/static/**", "anon");
		//配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
		filterChainDefinitionMap.put("/logout", "logout");
		//<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
		//<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
		filterChainDefinitionMap.put("/**", "authc");
		
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/login");
		// 登录成功后要跳转的链接
		shiroFilterFactoryBean.setSuccessUrl("/index");

		// 自定义拦截器
		Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
		// 登录权限验证拦截器
		filtersMap.put("customFormAuthenticationFilter", new CustomFormAuthenticationFilter());
		// 二维码验证拦截器
		filtersMap.put("captchaValidateFilter", new CaptchaValidateFilter());
		// 将自定义拦截器加入到shiro的Bean中
		shiroFilterFactoryBean.setFilters(filtersMap);
		
		// 将登录权限验证拦截器设置进shiro的filterchain中。必须设置！否则filter不会生效！
		filterChainDefinitionMap.put("/**", "customFormAuthenticationFilter");
		// 将二维码验证拦截器设置进shiro的filterchain中，必须设置！否则filter不会生效！
		filterChainDefinitionMap.put("/**", "captchaValidateFilter");

		//未授权界面;
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		
		return shiroFilterFactoryBean;
	}

	/**
	 * 凭证匹配器
	 * 
	 * 由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了,
	 * 所以我们需要修改下doGetAuthenticationInfo中的代码;
	 * 
	 * 可以扩展凭证匹配器，实现输入密码错误次数后锁定等功能，下一次
	 * 
	 * @return
	 */
	@Bean
	public HashedCredentialsMatcher hashedCredentialsMatcher(){
		
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		// 散列算法:这里使用MD5算法
		hashedCredentialsMatcher.setHashAlgorithmName("md5");
		// 散列的次数，比如散列两次，相当于md5(md5(""))
		//hashedCredentialsMatcher.setHashIterations(2);
		hashedCredentialsMatcher.setHashIterations(1);
		// storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
//	    hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
		
		return hashedCredentialsMatcher;
	}
	
//	public SBMatcher sbMatcher() {
//		
//		return new SBMatcher();
//	}

	/**
	 * 创建Realm
	 * 
	 * @return
	 */
	@Bean
	public SBAuthorizingRealm myShiroRealm(){
		
		// 创建Realm对象
		SBAuthorizingRealm myShiroRealm = new SBAuthorizingRealm();
		// 设置密码匹配器，是自定义的
		myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
//		myShiroRealm.setCredentialsMatcher(sbMatcher());
		
		return myShiroRealm;
	}

	/**
	 * 创建DefaultWebSecurityManager
	 * 
	 * @return
	 */
	@Bean
	public DefaultWebSecurityManager securityManager(){
		
		DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
		securityManager.setRealm(myShiroRealm());
		
		return securityManager;
	}

	/**
	 *  开启shiro aop注解支持.
	 *  使用代理方式;所以需要开启代码支持;
	 *  
	 * @param securityManager
	 * @return
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
		
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		
		return authorizationAttributeSourceAdvisor;
	}

	@Bean(name="simpleMappingExceptionResolver")
	public SimpleMappingExceptionResolver createSimpleMappingExceptionResolver() {
		
		SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();
		Properties mappings = new Properties();
		mappings.setProperty("DatabaseException", "databaseError");//数据库异常处理
		mappings.setProperty("UnauthorizedException","403");
		r.setExceptionMappings(mappings);  // None by default
		r.setDefaultErrorView("error");    // No default
		r.setExceptionAttribute("ex");     // Default is "exception"
		//r.setWarnLogCategory("example.MvcLogger");     // No default
		return r;
	}
}
