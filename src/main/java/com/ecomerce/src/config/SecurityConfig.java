package com.ecomerce.src.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecomerce.src.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final AuthenticationProvider authenticationProvider;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.authenticationProvider = authenticationProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/health", "/api/auth/**", "/error").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/productos/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/productos/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
						.requestMatchers(HttpMethod.POST, "/api/categorias/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.POST, "/api/estados/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/estados/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/estados/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.POST, "/api/metodos-pago/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/metodos-pago/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/metodos-pago/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.POST, "/api/detalle-compras/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/detalle-compras/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/detalle-compras/**").hasRole("ADMINISTRADOR")
						.requestMatchers("/api/users/me").authenticated()
						.requestMatchers("/api/users/**").hasRole("ADMINISTRADOR")
						.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
