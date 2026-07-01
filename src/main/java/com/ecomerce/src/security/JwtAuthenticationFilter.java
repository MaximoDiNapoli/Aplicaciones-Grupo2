package com.ecomerce.src.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecomerce.src.entity.User;
import com.ecomerce.src.repository.UserRepository;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final UserRepository userRepository;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
			UserRepository userRepository) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String jwt = authHeader.substring(7);
		// El subject del token es el id del usuario; resolvemos el usuario por id.
		Integer userId;
		try {
			userId = jwtService.extractUserId(jwt);
		} catch (JwtException | IllegalArgumentException exception) {
			filterChain.doFilter(request, response);
			return;
		}

		if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			User user = userRepository.findById(userId).orElse(null);
			if (user != null && jwtService.isTokenValid(jwt)) {
				// Reutilizamos el UserDetailsService (por email) para conservar una unica
				// fuente de las autoridades por rol; el email queda como principal name.
				UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities());
				authToken.setDetails(userId);
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		filterChain.doFilter(request, response);
	}
}
