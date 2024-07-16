package com.digisprint.exception;

public class UserNotFoundException extends Exception {

	private static final long serialVersionUID = 6141754114666186761L;

	public UserNotFoundException(String errorMessage) {
		super(errorMessage);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

	protected UserNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
