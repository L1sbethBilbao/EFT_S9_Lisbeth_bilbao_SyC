package com.duoc.frontendS8.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfig {

	/**
	 * {@link JdkClientHttpRequestFactory} permite PATCH correctamente; el {@link RestTemplate}
	 * por defecto usa {@code HttpURLConnection}, que en la práctica falla al llamar al backend
	 * (p. ej. actualizar estado de adopción) y termina en error 500 en el front.
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate(new JdkClientHttpRequestFactory());
	}
}
