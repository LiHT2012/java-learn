package com.backend.kfc.exception;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionController {

//	private static final String USER_NOT_LOGIN_ERROR = "\"{\\\"message\\\" : \\\"User not login.\\\"}\"";

	@ExceptionHandler(GeneralException.class)
	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public String generalExceptionHandler(Exception ex) {
		return "{\"message\" : \"" + ex.getMessage() + "\"}";
	}

	@ExceptionHandler(UserNotLoginException.class)
	public ResponseEntity<Map<String, String>> userNotLoginExceptionHandler(Exception ex) {
		return new ResponseEntity<Map<String, String>>(Collections.singletonMap("message", "User not login"), HttpStatus.UNAUTHORIZED);
	}//不直接返回字符串，用于应对异常时 返回的content-type无故变成		text/html等非json返回结构

//	@ExceptionHandler(UserNotLoginException.class)
//	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
//	@ResponseBody
//	public Map<String, String> userNotLoginExceptionHandler(Exception ex) {
//		return Collections.singletonMap("message", "User not login");
//	}

}
