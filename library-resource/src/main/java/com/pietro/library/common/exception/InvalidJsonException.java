package com.pietro.library.common.exception;

public class InvalidJsonException extends RuntimeException {
	private static final long serialVersionUID = -1138192041343364355L;

	public InvalidJsonException(final String message) {
		super(message);
	}

	public InvalidJsonException(final Throwable throwable) {
		super(throwable);
	}

}
