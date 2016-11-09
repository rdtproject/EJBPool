package com.pietro.library.common.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class FieldNotValidException extends RuntimeException {
	private static final long serialVersionUID = 8093961635856419254L;

	private final String fieldName;

	public FieldNotValidException(final String fieldName, final String message) {
		super(message);
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	@Override
	public String toString() {
		return "FieldNotValidException [fieldName=" + fieldName + "]";
	}

}
