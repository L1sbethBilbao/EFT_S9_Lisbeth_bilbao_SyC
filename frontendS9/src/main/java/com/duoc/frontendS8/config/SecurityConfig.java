package com.duoc.frontendS8.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * Directivas separadas con ';' (gramática CSP). Antes estaban unidas solo con espacios y ZAP/navegadores
	 * interpretaban mal la política (duplicados, error con 'none', falsos "CSP no configurada").
	 * img-src y font-src solo 'self': sin data:/https: amplios (ZAP 10055 “Directiva Wildcard”).
	 */
	private static final String CSP_DIRECTIVES = String.join("; ",
			"default-src 'self'",
			"script-src 'self'",
			"style-src 'self'",
			"img-src 'self'",
			"font-src 'self'",
			"connect-src 'self'",
			"frame-src 'none'",
			"frame-ancestors 'self'",
			"worker-src 'none'",
			"manifest-src 'none'",
			"media-src 'self'",
			"base-uri 'self'",
			"form-action 'self'",
			"object-src 'none'");

	@Bean
	CookieCsrfTokenRepository cookieCsrfTokenRepository() {
		// HttpOnly + SameSite en la cookie CSRF (Spring Security 7: usar CookieCustomizer, no setCookieHttpOnly eliminado).
		CookieCsrfTokenRepository repo = new CookieCsrfTokenRepository();
		repo.setCookieCustomizer(builder -> builder.sameSite("Lax").httpOnly(true));
		return repo;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, CookieCsrfTokenRepository cookieCsrfTokenRepository)
			throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/",
								"/login",
								"/logout",
								"/css/**",
								"/js/**",
								"/error",
								"/mascotas/**",
								"/adopciones/disponibles",
								"/adopciones/solicitar",
								"/adopciones/gracias",
								"/adopciones/seguimiento",
								"/adopciones/seguimiento/salir").permitAll()
						.requestMatchers(HttpMethod.POST, "/adopciones/solicitar", "/adopciones/seguimiento/mensaje").permitAll()
						.requestMatchers("/adopciones/gestion", "/adopciones/gestion/**").hasRole("COORDINADOR")
						.requestMatchers("/animales/**").hasAnyRole("COORDINADOR", "GESTOR")
						.requestMatchers("/duenos/**").hasAnyRole("COORDINADOR", "GESTOR")
						.requestMatchers("/veterinarios/nuevo").hasRole("COORDINADOR")
						.requestMatchers("/veterinarios/*/editar").hasRole("COORDINADOR")
						.requestMatchers(HttpMethod.POST, "/veterinarios").hasRole("COORDINADOR")
						.requestMatchers(HttpMethod.POST, "/veterinarios/*/actualizar").hasRole("COORDINADOR")
						.requestMatchers(HttpMethod.POST, "/veterinarios/*/eliminar").hasRole("COORDINADOR")
						.requestMatchers("/veterinarios/**").hasAnyRole("COORDINADOR", "VETERINARIO")
						.requestMatchers("/citas/**").hasAnyRole("COORDINADOR", "VETERINARIO")
						.requestMatchers("/registros-medicos/**").hasAnyRole("COORDINADOR", "VETERINARIO")
						.requestMatchers("/facturas/**").hasAnyRole("COORDINADOR", "VETERINARIO"))
				.formLogin(form -> form.disable())
				.httpBasic(basic -> basic.disable())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.permitAll())
				.csrf(csrf -> csrf.csrfTokenRepository(cookieCsrfTokenRepository))
				.headers(headers -> headers
						.contentSecurityPolicy(csp -> csp.policyDirectives(CSP_DIRECTIVES))
						.referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
						.frameOptions(Customizer.withDefaults())
						.xssProtection(xss -> xss.headerValue(HeaderValue.ENABLED_MODE_BLOCK))
						.contentTypeOptions(Customizer.withDefaults()));
		return http.build();
	}
}
