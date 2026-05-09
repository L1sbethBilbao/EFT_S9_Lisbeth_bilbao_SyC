package com.duoc.frontendS8.web;

import com.duoc.frontendS8.service.BackendApiClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SessionJwt {

	private SessionJwt() {
	}

	public static String get(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			throw new IllegalStateException("Sesión no encontrada");
		}
		Object token = session.getAttribute(BackendApiClient.SESSION_JWT);
		if (!(token instanceof String s) || s.isBlank()) {
			throw new IllegalStateException("Token JWT no disponible");
		}
		return s;
	}
}
