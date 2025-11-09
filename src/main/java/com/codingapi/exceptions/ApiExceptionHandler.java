package com.codingapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler({CodingApiException.class})
	public ResponseEntity<ApiError> handleBadRequest(Exception ex) {
		return ResponseEntity.badRequest().body(ApiError.of(ex.getMessage()));
//		return new ResponseEntity<>(ApiError.of(ex.getMessage()), ex.getStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleAll(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiError.of("Internal server error"));
	}
}