package com.codingapi.exceptions;

public class ApiError {

	String message;

	private ApiError(String message) {
		this.message = message;
	}

	public static ApiError of(String message) {
		return new ApiError(message);
	}

}
