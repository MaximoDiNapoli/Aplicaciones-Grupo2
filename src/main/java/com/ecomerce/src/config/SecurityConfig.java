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
						.requestMatchers(HttpMethod.GET, "/api/productos", "/api/productos/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/productos", "/api/productos/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/productos", "/api/productos/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/productos", "/api/productos/**").hasAnyRole("VENDEDOR", "ADMINISTRADOR")
						.requestMatchers(HttpMethod.GET, "/api/categorias", "/api/categorias/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/categorias", "/api/categorias/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/categorias", "/api/categorias/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/categorias", "/api/categorias/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.GET, "/api/estados", "/api/estados/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/estados", "/api/estados/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/estados", "/api/estados/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/estados", "/api/estados/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.GET, "/api/metodos-pago", "/api/metodos-pago/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/metodos-pago", "/api/metodos-pago/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/metodos-pago", "/api/metodos-pago/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/metodos-pago", "/api/metodos-pago/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.GET, "/api/direcciones", "/api/direcciones/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.POST, "/api/direcciones", "/api/direcciones/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/direcciones", "/api/direcciones/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/direcciones", "/api/direcciones/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.GET, "/api/carrito", "/api/carrito/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.POST, "/api/carrito", "/api/carrito/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/carrito", "/api/carrito/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/carrito", "/api/carrito/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.GET, "/api/compras", "/api/compras/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.POST, "/api/compras", "/api/compras/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/compras", "/api/compras/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/compras", "/api/compras/**").hasRole("COMPRADOR")
						.requestMatchers(HttpMethod.GET, "/api/detalle-compras", "/api/detalle-compras/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.POST, "/api/detalle-compras", "/api/detalle-compras/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.PUT, "/api/detalle-compras", "/api/detalle-compras/**").hasRole("ADMINISTRADOR")
						.requestMatchers(HttpMethod.DELETE, "/api/detalle-compras", "/api/detalle-compras/**").hasRole("ADMINISTRADOR")
						.requestMatchers("/api/users/me").authenticated()
						.requestMatchers(HttpMethod.GET, "/api/users/*/compras").authenticated()
						.requestMatchers("/api/users", "/api/users/**").hasRole("ADMINISTRADOR")
						.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
