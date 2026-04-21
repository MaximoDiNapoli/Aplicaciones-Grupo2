package com.ecomerce.src.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecomerce.src.dto.UserRequest;
import com.ecomerce.src.dto.UserResponse;
import com.ecomerce.src.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(
        @RequestParam(required=false) String rol,
        @RequestParam(required=false) String ciudad,
        @RequestParam(required = false) String codigopostal
    ) {
        List<UserResponse> users = this.userService.getUsers(rol, ciudad, codigopostal);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(this.userService.getCurrentUser());
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@RequestBody UserRequest userDetails) {
        return ResponseEntity.ok(this.userService.updateCurrentUser(userDetails));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        UserResponse user = this.userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer id, @RequestBody UserRequest userDetails) {
        UserResponse usuarioUpdated = this.userService.updateUser(id, userDetails);
        return ResponseEntity.ok(usuarioUpdated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
