package com.developerspace.filestorageapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author wangz
 * @Date 2022-01-10 16:41
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		//配置允许跨域的路径
		registry.addMapping("/**")
				//配置允许访问的跨域资源的请求域名
				.allowCredentials(true)
				.allowedOrigins("*")
				//配置允许访问该跨域资源服务器的请求方法
				.allowedMethods("*")
				//配置允许请求 头部head的访问
				.allowedHeaders("*");
	}
}
