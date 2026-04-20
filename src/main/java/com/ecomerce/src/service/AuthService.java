package com.ecomerce.src.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.auth.AuthRequest;
import com.ecomerce.src.dto.auth.AuthResponse;
import com.ecomerce.src.dto.auth.RegisterRequest;
import com.ecomerce.src.entity.AuthUser;
import com.ecomerce.src.repository.AuthUserRepository;
import com.ecomerce.src.security.JwtService;

@Service
public class AuthService {

	private final AuthUserRepository authUserRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthService(
			AuthUserRepository authUserRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			AuthenticationManager authenticationManager) {
		this.authUserRepository = authUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
	}

	public AuthResponse register(RegisterRequest request) {
		if (authUserRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Ya existe un usuario con ese email");
		}

		AuthUser user = new AuthUser();
		user.setNombre(request.getNombre());
		user.setEmail(request.getEmail());
		user.setTelefono(request.getTelefono());
		user.setRol("USER");
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		authUserRepository.save(user);

		String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPasswordHash())
				.roles("USER")
				.build());
		return new AuthResponse(token, "Bearer");
	}

	public AuthResponse login(AuthRequest request) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		AuthUser user = authUserRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));

		String role = user.getRol() == null || user.getRol().isBlank() ? "USER" : user.getRol().trim();
		String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPasswordHash())
				.roles(role)
				.build());
		return new AuthResponse(token, "Bearer");
	}
}
