package com.developerspace.filestorageserver.advice;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 在方法返回前做一些处理
 * @Author wangz
 * @Date 2022-02-02 10:38
 */

@Slf4j
//@ControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice {

	/**
	 * 若该方法返回true，则将会进一步调用beforeBodyWrite
	 **/
	@Override
	public boolean supports(MethodParameter returnType, Class converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		log.info("body=" + body);
		//若是json类型
		if (selectedContentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
			Object dataObj = JSON.toJSON(body);
			if (dataObj instanceof JSONArray) {
				return body;
			}
			JSONObject jsonResponse = (JSONObject) dataObj;
			jsonResponse.put("msg","hello,welcome");
		}
		return body;
	}
}
