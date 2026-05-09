package com.duoc.frontendS8.exception;

/**
 * El frontend no pudo contactar al backend (API caído, red, timeout).
 */
public class BackendUnavailableException extends RuntimeException {

	public BackendUnavailableException(String message, Throwable cause) {
		super(message, cause);
	}
}
