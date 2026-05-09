package com.duoc.backendS8.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/adopciones/solicitudes").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/adopciones/seguimiento/mensajes").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/adopciones/seguimiento/consulta").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/adopciones/solicitudes").hasRole("COORDINADOR")
						.requestMatchers(HttpMethod.GET, "/api/adopciones/solicitudes/*").hasRole("COORDINADOR")
						.requestMatchers(HttpMethod.POST, "/api/adopciones/solicitudes/*/mensajes").hasRole("COORDINADOR")
						.requestMatchers(HttpMethod.PATCH, "/api/adopciones/solicitudes/*/estado").hasRole("COORDINADOR")
						.requestMatchers(HttpMethod.PATCH, "/api/adopciones/solicitudes/*/datos").hasRole("COORDINADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/adopciones/solicitudes/*").hasRole("COORDINADOR")
						.requestMatchers(HttpMethod.GET, "/api/animales").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/animales/*").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/animales").hasAnyRole("COORDINADOR", "GESTOR")
						.requestMatchers(HttpMethod.PUT, "/api/animales/*").hasAnyRole("COORDINADOR", "GESTOR")
						.requestMatchers(HttpMethod.DELETE, "/api/animales/*").hasAnyRole("COORDINADOR", "GESTOR")
						.requestMatchers("/api/duenos/**").hasAnyRole("COORDINADOR", "GESTOR")
						.requestMatchers(HttpMethod.POST, "/api/veterinarios").hasRole("COORDINADOR")
						.requestMatchers(HttpMethod.PUT, "/api/veterinarios/*").hasRole("COORDINADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/veterinarios/*").hasRole("COORDINADOR")
						.requestMatchers("/api/veterinarios/**").hasAnyRole("COORDINADOR", "VETERINARIO")
						.requestMatchers("/api/citas/**").hasAnyRole("COORDINADOR", "VETERINARIO")
						.requestMatchers("/api/registros-medicos/**").hasAnyRole("COORDINADOR", "VETERINARIO")
						.requestMatchers("/api/facturas/**").hasAnyRole("COORDINADOR", "VETERINARIO")
						.requestMatchers("/error").permitAll()
						.requestMatchers("/api/**").denyAll()
						.anyRequest().denyAll())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
