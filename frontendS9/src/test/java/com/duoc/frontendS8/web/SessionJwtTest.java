package com.duoc.frontendS8.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.duoc.frontendS8.service.BackendApiClient;

class SessionJwtTest {

	@Test
	void getDevuelveToken() {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.getSession(true).setAttribute(BackendApiClient.SESSION_JWT, "abc123");
		assertThat(SessionJwt.get(req)).isEqualTo("abc123");
	}

	@Test
	void getSinSesion() {
		MockHttpServletRequest req = new MockHttpServletRequest();
		assertThatThrownBy(() -> SessionJwt.get(req)).isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("Sesión");
	}

	@Test
	void getTokenBlank() {
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.getSession(true).setAttribute(BackendApiClient.SESSION_JWT, " ");
		assertThatThrownBy(() -> SessionJwt.get(req)).isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("JWT");
	}
}
