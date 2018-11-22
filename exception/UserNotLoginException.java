package com.backend.kfc.exception;

public class UserNotLoginException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 734946410368973876L;

	public UserNotLoginException() {
		super();
	}

	public UserNotLoginException(String message) {
		super(message);
	}
}
