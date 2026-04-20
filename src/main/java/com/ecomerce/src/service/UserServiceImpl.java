package com.ecomerce.src.service;

import com.ecomerce.src.dto.UserRequest;
import com.ecomerce.src.dto.UserResponse;
import com.ecomerce.src.entity.User;
import com.ecomerce.src.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserResponse> getUsers(String rol, String ciudad, String codigopostal) {
        List<User> usersFound;

        if (rol != null){
            usersFound = this.userRepository.findByRol(rol);
        }else{
            usersFound = this.userRepository.findAll();
        }

        return usersFound.stream().map(usuario -> new UserResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getTelefono(), usuario.getRol(), usuario.getCreatedAt())).collect(Collectors.toList());
    }

    public UserResponse getUserById(Integer id) {
        User usuario = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));
        return this.crearResponseDTO(usuario);// new UsuarioResponseDTO(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getTelefono(), usuario.getRol(), usuario.getCreatedAt());
    }

    public UserResponse updateUser(Integer id, UserRequest userDetails) {
        User usuario = this.userRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no existe, no se puede actualizar."));

        if(userDetails.getNombre() != null) usuario.setNombre(userDetails.getNombre());
        if(userDetails.getEmail() != null) usuario.setEmail(userDetails.getEmail());
        if(userDetails.getTelefono() != null) usuario.setTelefono(userDetails.getTelefono());
        if(userDetails.getRol() != null) usuario.setRol(userDetails.getRol());

        User usuarioUpdated = this.userRepository.save(usuario);

        return this.crearResponseDTO(usuarioUpdated);
    }

    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("No se puede borrar: Usuario no encontrado");
        }
        this.userRepository.deleteById(id);
    }

    private UserResponse crearResponseDTO(User usuario) {
        return new UserResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getRol(),
                usuario.getCreatedAt()
        );
    }

}
