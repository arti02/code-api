package com.codingapi.exceptions;

import lombok.Getter;

@Getter
public class ApiError {

	private final String message;

	private ApiError(String message) {
		this.message = message;
	}

	public static ApiError of(String message) {
		return new ApiError(message);
	}
}
