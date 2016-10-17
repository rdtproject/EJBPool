package com.pietro.library.common.model;

public enum HttpCode {
	CREATED(201), VALIDATION_ERROR(422);

	private int code;

	private HttpCode(final int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
