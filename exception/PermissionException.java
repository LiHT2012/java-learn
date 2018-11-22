package com.backend.kfc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.backend.kfc.util.JacksonUtil;

@ResponseStatus(value = HttpStatus.FORBIDDEN,reason = "{\"message\" : \"User no permission.\"}")
public class PermissionException extends Exception {

	private PermissionUnit messageUnit;
	

	public PermissionUnit getMessageUnit() {
		return messageUnit;
	}

	public class PermissionUnit {
		public String resId;
		public String userId;

		public PermissionUnit(String resId,  String userId) {
			super();
			this.resId = resId;
			this.userId = userId;
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -7740211576524565228L;
		
	public PermissionException() {
		super();
	}

	public PermissionException(String message) {
		super(message);
	}
	
	public PermissionException(String resId, String userId) {
		messageUnit = new PermissionUnit( resId, userId);
	}
	
	@Override
	public String getMessage() {
		return JacksonUtil.obj2Json(messageUnit);
	}
}
