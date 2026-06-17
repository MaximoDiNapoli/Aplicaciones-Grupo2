package com.ecomerce.src.service.impl;

import java.util.Locale;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecomerce.src.dto.UserRequest;
import com.ecomerce.src.dto.UserResponse;
import com.ecomerce.src.entity.User;
import com.ecomerce.src.exception.ResourceNotFoundException;
import com.ecomerce.src.repository.UserRepository;
import com.ecomerce.src.security.CurrentUserService;
import com.ecomerce.src.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final Set<String> ALLOWED_ROLES = Set.of("COMPRADOR", "VENDEDOR", "ADMINISTRADOR", "USER");

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, CurrentUserService currentUserService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse createUser(UserRequest userDetails) {
        if (userRepository.existsByEmail(userDetails.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }
        if (userDetails.getPassword() == null || userDetails.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es requerida");
        }

        String rol = (userDetails.getRol() == null || userDetails.getRol().isBlank())
                ? "COMPRADOR"
                : userDetails.getRol().trim().toUpperCase(Locale.ROOT);
        if (!ALLOWED_ROLES.contains(rol)) {
            throw new IllegalArgumentException("Rol invalido. Roles permitidos: COMPRADOR, VENDEDOR, ADMINISTRADOR");
        }

        User usuario = new User();
        usuario.setNombre(userDetails.getNombre());
        usuario.setEmail(userDetails.getEmail());
        usuario.setTelefono(userDetails.getTelefono());
        usuario.setRol(rol);
        usuario.setPasswordHash(passwordEncoder.encode(userDetails.getPassword()));

        return this.crearResponseDTO(this.userRepository.save(usuario));
    }

    @Override
    public List<UserResponse> getUsers(String rol, String ciudad, String codigopostal) {
        List<User> usersFound;

        if (rol != null) {
            usersFound = this.userRepository.findByRol(rol);
        } else {
            usersFound = this.userRepository.findAll();
        }

        return usersFound.stream()
                .map(usuario -> new UserResponse(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getEmail(),
                        usuario.getTelefono(),
                        usuario.getRol(),
                        usuario.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Integer id) {
        User usuario = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no encontrado"));
        return this.crearResponseDTO(usuario);
    }

    @Override
    public UserResponse updateUser(Integer id, UserRequest userDetails) {
        User usuario = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID " + id + " no existe, no se puede actualizar."));

        if (userDetails.getNombre() != null)
            usuario.setNombre(userDetails.getNombre());
        if (userDetails.getEmail() != null) {
            String nuevoEmail = userDetails.getEmail();
            if (!nuevoEmail.equals(usuario.getEmail()) && userRepository.existsByEmail(nuevoEmail)) {
                throw new IllegalArgumentException("Ya existe un usuario con ese email");
            }
            usuario.setEmail(nuevoEmail);
        }
        if (userDetails.getTelefono() != null)
            usuario.setTelefono(userDetails.getTelefono());
        if (userDetails.getRol() != null) {
            if (!isAuthenticatedAdmin()) {
                throw new AccessDeniedException("Solo un administrador puede modificar el rol de un usuario");
            }

            String rolNormalizado = userDetails.getRol().trim().toUpperCase(Locale.ROOT);
            if (!"COMPRADOR".equals(rolNormalizado)
                    && !"VENDEDOR".equals(rolNormalizado)
                    && !"ADMINISTRADOR".equals(rolNormalizado)
                    && !"USER".equals(rolNormalizado)) {
                throw new IllegalArgumentException("Rol invalido. Roles permitidos: COMPRADOR, VENDEDOR, ADMINISTRADOR");
            }
            usuario.setRol(rolNormalizado);
        }

        User usuarioUpdated = this.userRepository.save(usuario);

        return this.crearResponseDTO(usuarioUpdated);
    }

    @Override
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede borrar: Usuario no encontrado");
        }
        this.userRepository.deleteById(id);
    }

    @Override
    public UserResponse getCurrentUser() {
        return this.crearResponseDTO(currentUserService.getCurrentUser());
    }

    @Override
    public UserResponse updateCurrentUser(UserRequest userDetails) {
        if (userDetails.getRol() != null && !isAuthenticatedAdmin()) {
            throw new AccessDeniedException("Solo un administrador puede modificar el rol de un usuario");
        }
        Integer currentUserId = currentUserService.getCurrentUserId();
        return updateUser(currentUserId, userDetails);
    }

    private UserResponse crearResponseDTO(User usuario) {
        return new UserResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol(),
                usuario.getCreatedAt());
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