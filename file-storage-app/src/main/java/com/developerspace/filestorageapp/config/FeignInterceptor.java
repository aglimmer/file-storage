package com.developerspace.filestorageapp.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author wangz
 * @Date 2022-01-10 13:35
 */
@Slf4j
@Configuration
public class FeignInterceptor implements RequestInterceptor {

	/**
	 *
	 **/
	@Override
	public void apply(RequestTemplate requestTemplate) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			String []customHeaderNames = {"AccessToken","RefreshToken","AUTHORIZATION"};
			for(String headerName:customHeaderNames){
				String headerValue = request.getHeader(headerName);
				if(headerValue!=null){
					requestTemplate.header(headerName,headerValue);
				}
			}
//			String authorizationName="Authorization";
//			String authorization= request.getHeader(authorizationName);
//			if(StringUtils.isEmpty(authorization))
//			{
//				authorizationName="authorization";
//				authorization= request.getHeader(authorizationName);
//			}
			//requestTemplate.header("AUTHORIZATION", "123456");
			Map<String, Collection<String>> headers = requestTemplate.headers();
			Set<String> headNames = headers.keySet();
			headNames.forEach(key -> {
				log.info("key = " + key + ", value=" + headers.get(key));
			});
		}
	}

}
