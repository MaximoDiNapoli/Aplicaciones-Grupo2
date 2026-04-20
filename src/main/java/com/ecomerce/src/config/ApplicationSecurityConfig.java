package com.ecomerce.src.config;

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

import com.ecomerce.src.entity.AuthUser;
import com.ecomerce.src.repository.AuthUserRepository;

@Configuration
public class ApplicationSecurityConfig {

	private final AuthUserRepository authUserRepository;

	public ApplicationSecurityConfig(AuthUserRepository authUserRepository) {
		this.authUserRepository = authUserRepository;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
			AuthUser user = authUserRepository.findByEmail(username)
					.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
			String role = user.getRol() == null || user.getRol().isBlank() ? "USER" : user.getRol().trim();
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
