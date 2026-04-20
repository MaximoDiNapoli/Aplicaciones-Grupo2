package com.ecomerce.src.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecomerce.src.repository.UserRepository;

@Configuration
public class ApplicationSecurityConfig {

	private static final String DEFAULT_ROLE = "COMPRADOR";

	private final UserRepository userRepository;

	public ApplicationSecurityConfig(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
			com.ecomerce.src.entity.User user = userRepository.findByEmail(username)
					.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
			String role = user.getRol() == null || user.getRol().isBlank()
					? DEFAULT_ROLE
					: user.getRol().trim().toUpperCase(Locale.ROOT);
			return User.withUsername(user.getEmail())
					.password(user.getPasswordHash())
					.roles(role)
					.build();
		};
	}

	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
