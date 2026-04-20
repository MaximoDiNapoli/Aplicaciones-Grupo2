package com.ecomerce.src.controller;

import com.ecomerce.src.dto.UserRequest;
import com.ecomerce.src.dto.UserResponse;
import com.ecomerce.src.entity.Carrito;
import com.ecomerce.src.entity.Compra;
import com.ecomerce.src.entity.DireccionEnvio;
import com.ecomerce.src.service.CarritoService;
import com.ecomerce.src.service.CompraService;
import com.ecomerce.src.service.DireccionEnvioService;
import com.ecomerce.src.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompraService compraService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private DireccionEnvioService direccionEnvioService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(
        @RequestParam(required=false) String rol,
        @RequestParam(required=false) String ciudad,
        @RequestParam(required = false) String codigopostal
    ) {
        List<UserResponse> users = this.userService.getUsers(rol, ciudad, codigopostal);
        return ResponseEntity.ok(users);
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        UserResponse user = this.userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // PUT /api/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer id, @RequestBody UserRequest userDetails) {
        UserResponse usuarioUpdated = this.userService.updateUser(id, userDetails);
        return ResponseEntity.ok(usuarioUpdated);
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints de Relaciones ---

    @GetMapping("/{id}/compras")
    public ResponseEntity<?> getUserCompras(@PathVariable Integer id) {
        // Lógica para traer compras del usuario
        this.userService.getUserById(id);

        List<Compra> comprasPorUsuario = this.compraService.listarPorUsuario(id);
        return ResponseEntity.ok(comprasPorUsuario);
    }

    @GetMapping("/{id}/carrito")
    public ResponseEntity<List<Carrito>> getUserCarrito(@PathVariable Integer id) {
        this.userService.getUserById(id);
        return ResponseEntity.ok(this.carritoService.listarPorUsuario(id));
    }

    @GetMapping("/{id}/direcciones")
    public ResponseEntity<List<DireccionEnvio>> getUserDirecciones(@PathVariable Integer id) {
        this.userService.getUserById(id);
        return ResponseEntity.ok(this.direccionEnvioService.listarPorUsuario(id));
    }
}
