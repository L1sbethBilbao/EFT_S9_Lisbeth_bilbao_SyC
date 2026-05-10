package com.duoc.backendS8.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.duoc.backendS8.dto.LoginRequest;

import jakarta.persistence.EntityNotFoundException;

class GlobalExceptionHandlerTest {

	private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

	@Test
	void badCredentials() {
		var res = handler.badCredentials(new BadCredentialsException("x"));
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		assertThat(res.getBody()).isNotNull();
		assertThat(res.getBody().message()).isEqualTo("x");
	}

	@Test
	void notFound() {
		var res = handler.notFound(new EntityNotFoundException("nf"));
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(res.getBody().message()).isEqualTo("nf");
	}

	@Test
	void validationMapsFieldErrors() throws NoSuchMethodException {
		LoginRequest target = new LoginRequest("", "");
		BeanPropertyBindingResult errors = new BeanPropertyBindingResult(target, "loginRequest");
		errors.addError(new FieldError("loginRequest", "username", "no vacío"));
		MethodParameter param = MethodParameter.forExecutable(
				AuthController.class.getDeclaredMethod("login", LoginRequest.class),
				0);
		MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, errors);
		var res = handler.validation(ex);
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(res.getBody().details()).containsExactly("username: no vacío");
	}

	@Test
	void illegalState() {
		var res = handler.conflict(new IllegalStateException("estado"));
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(res.getBody().message()).isEqualTo("estado");
	}

	@Test
	void dataIntegrityReturnsConflict() {
		var res = handler.dataIntegrity(new DataIntegrityViolationException("fk"));
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(res.getBody()).isNotNull();
		assertThat(res.getBody().message()).contains("registros relacionados");
	}
}
