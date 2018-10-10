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
	
	/**
	 * 非常重要！
	 * 这里要注意，shiro处理filter是有顺序的，先定义的先处理，后定义的后处理
	 * 比如：
	 * 1	如果代码中有2个关于filter的设置，顺序是先设置所有url都可以匿名访问，后设置自定义的filter，即：
	 * 			条件1：filterChainDefinitionMap.put("/**", "anon");
	 * 			条件2：filterChainDefinitionMap.put("/login", "customFormAuthenticationFilter");
	 * 		这里有个特点：
	 * 			条件2包含的自定义filter
	 * 		是被包含在：
	 * 			条件1对应的默认filter
	 * 		之内的，如果想让：
	 * 			条件2包含的自定义filter
	 * 		被执行，应该让系统按照：
	 * 			条件2包含的自定义filter --> 条件1对应的默认filter
	 * 		所以注意！如果按照上面的设置：
	 * 			条件1 --> 条件2
	 * 		这种顺序来设置，当在浏览器中执行“/login”这个url的时候，是不会经过“条件2”这个自定义的filter的，
	 * 		因为在执行filter的时候，会遵循“最近匹配原则”去匹配url对应的filter，
	 * 
	 * 				if(满足“条件1”对应的url)
	 * 					执行“条件1”对应的filter
	 * 				else if(满足“条件2”对应的url)
	 * 					执行“条件2”对应的filter
	 * 				...
	 * 		以此类推，执行到“条件1”对应的filter，所有filter就结束了！所以一定要注意这里的设置顺序！
	 * 
	 * @param securityManager
	 * @return
	 */
	@Bean
	public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
		System.out.println("ShiroConfiguration.shirFilter()");
		
		// 创建shiro的Bean
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 必须设置 SecurityManager
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		
		// 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
		shiroFilterFactoryBean.setLoginUrl("/login");
		// 登录成功后要跳转的链接
		shiroFilterFactoryBean.setSuccessUrl("/index");
		
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");
		
		/**
		 * 1.设置自定义filters拦截器
		 */
		Map<String, Filter> filtersMap = new LinkedHashMap<String, Filter>();
		
		// 登录权限验证拦截器。注意这里设置成authc，user，logout使用的拦截器都是CustomFormAuthenticationFilter()
		filtersMap.put("authc", new CustomFormAuthenticationFilter());
		filtersMap.put("user", new CustomFormAuthenticationFilter());
		filtersMap.put("logout", new CustomFormAuthenticationFilter());
		// 二维码验证拦截器
		filtersMap.put("captchaValidateFilter", new CaptchaValidateFilter());
		// 将自定义拦截器加入到shiro的Bean中
		shiroFilterFactoryBean.setFilters(filtersMap);
		
		// 
		/**
		 * 2.设置filterChainDefinitions拦截器
		 * 
		 * 过滤链定义，从上向下顺序执行，一般将/**放在最为下边。
		 * authc:	所有url都必须认证通过才可以访问
		 * anon:	所有url都都可以匿名访问
		 * 
		 */
		Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();
		
		filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
		filterChainDefinitionMap.put("/captchaImage", "anon");
		// login时拦截执行二维码验证，用户名密码验证
		filterChainDefinitionMap.put("/login", "captchaValidateFilter, authc");
		// 所有访问默认都要被拦截检查
		filterChainDefinitionMap.put("/**", "user");
		
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
