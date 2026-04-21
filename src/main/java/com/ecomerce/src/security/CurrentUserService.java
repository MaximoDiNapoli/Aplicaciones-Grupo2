package com.ecomerce.src.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ecomerce.src.entity.User;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.UserRepository;

@Service
public class CurrentUserService {

	private final UserRepository userRepository;

	public CurrentUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Integer getCurrentUserId() {
		Object details = getAuthentication().getDetails();
		if (details instanceof Number number) {
			return number.intValue();
		}

		String email = getCurrentUserEmail();
		return userRepository.findByEmail(email)
				.map(User::getId)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el usuario autenticado con email " + email));
	}

	public String getCurrentUserEmail() {
		String email = getAuthentication().getName();
		if (email == null || email.isBlank()) {
			throw new AccessDeniedException("No se pudo resolver el email del usuario autenticado");
		}
		return email;
	}

	public User getCurrentUser() {
		Integer userId = getCurrentUserId();
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el usuario autenticado con id " + userId));
	}

	public boolean isAdmin() {
		Authentication authentication = getAuthentication();
		return authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch("ROLE_ADMINISTRADOR"::equals);
	}

	private Authentication getAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AccessDeniedException("No autenticado");
		}
		return authentication;
	}
}
