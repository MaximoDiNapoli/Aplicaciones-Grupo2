package com.ecomerce.src.service;

import java.util.Locale;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.auth.AuthRequest;
import com.ecomerce.src.dto.auth.AuthResponse;
import com.ecomerce.src.dto.auth.RegisterRequest;
import com.ecomerce.src.entity.User;
import com.ecomerce.src.repository.UserRepository;
import com.ecomerce.src.security.JwtService;

@Service
public class AuthService {

	private static final String ROLE_ADMINISTRADOR = "ADMINISTRADOR";
	private static final String DEFAULT_ROLE = "COMPRADOR";
	private static final Set<String> ALLOWED_ROLES = Set.of("COMPRADOR", "VENDEDOR", "ADMINISTRADOR", "USER");

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			AuthenticationManager authenticationManager) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
	}

	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Ya existe un usuario con ese email");
		}

		String requestedRole = resolveRequestedRole(request.getRol());
		if (ROLE_ADMINISTRADOR.equals(requestedRole) && !isAuthenticatedAdmin()) {
			throw new AccessDeniedException("Solo un administrador puede crear usuarios administradores");
		}

		User user = new User();
		user.setNombre(request.getNombre());
		user.setEmail(request.getEmail());
		user.setTelefono(request.getTelefono());
		user.setRol(requestedRole);
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		userRepository.save(user);

		String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPasswordHash())
				.roles(requestedRole)
				.build());
		return new AuthResponse(token, "Bearer");
	}

	public AuthResponse login(AuthRequest request) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));

		String role = resolveStoredRole(user.getRol());
		String token = jwtService.generateToken(org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPasswordHash())
				.roles(role)
				.build());
		return new AuthResponse(token, "Bearer");
	}

	private String resolveRequestedRole(String requestedRole) {
		if (requestedRole == null || requestedRole.isBlank()) {
			return DEFAULT_ROLE;
		}

		String normalizedRole = requestedRole.trim().toUpperCase(Locale.ROOT);
		if (!ALLOWED_ROLES.contains(normalizedRole)) {
			throw new IllegalArgumentException("Rol invalido. Roles permitidos: COMPRADOR, VENDEDOR, ADMINISTRADOR");
		}
		return normalizedRole;
	}

	private String resolveStoredRole(String storedRole) {
		if (storedRole == null || storedRole.isBlank()) {
			return DEFAULT_ROLE;
		}
		return storedRole.trim().toUpperCase(Locale.ROOT);
	}

	private boolean isAuthenticatedAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return false;
		}

		return authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch("ROLE_ADMINISTRADOR"::equals);
	}
}
