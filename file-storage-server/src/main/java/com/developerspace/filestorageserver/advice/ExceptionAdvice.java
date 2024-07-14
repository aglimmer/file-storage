package com.developerspace.filestorageserver.advice;



import com.developerspace.filestoragesdk.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author wangz
 * @Date 2022-02-02 10:19
 */
@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
	@ResponseBody
	@ExceptionHandler(value = RuntimeException.class)
	public Result onRuntimeException(RuntimeException e) {
		log.error(e.getMessage(),e);
		Result baseResponse = Result.error(e.getMessage());
		return baseResponse;
	}

	//自定义异常的返回内容
	@ResponseBody
	@ExceptionHandler(value = Exception.class)
	public Result onException(Exception e) {
		log.error(e.getMessage(),e);
		Result baseResponse = Result.error(e.getMessage());
		return baseResponse;
	}
}
