package com.ElSauce.demo.controller.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ElSauce.demo.dto.UserRequest;
import com.ElSauce.demo.dto.UserResponse;
import com.ElSauce.demo.model.User;
import com.ElSauce.demo.service.UserService;

// Imports de Swagger / OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios")
public class UserApiController {

    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    private UserResponse convertir(User user) {
        return new UserResponse(
                user.getId(),
                user.getNombre(),
                user.getApellido(),
                user.getEmail(),
                user.getTelefono(),
                user.getRole().getNombre());
    }

    // LISTAR
    @Operation(summary = "Listar todos los usuarios")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> listar() {

        List<UserResponse> lista = userService.obtenerTodos()
                .stream()
                .map(this::convertir)
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    // OBTENER POR ID
    @Operation(summary = "Buscar usuario por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {

        Optional<User> user = userService.obtenerPorId(id);

        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertir(user.get()));
    }

    // REGISTRAR
    @Operation(summary = "Registrar nuevo usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody UserRequest request) {

        try {

            User user = new User();

            user.setNombre(request.getNombre());
            user.setApellido(request.getApellido());
            user.setEmail(request.getEmail());
            user.setTelefono(request.getTelefono());
            user.setNumeroDocumento(request.getNumeroDocumento());

            // En tu entidad el campo se llama passwordHash
            user.setPasswordHash(request.getPassword());

            User guardado = userService.registerUser(user);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(convertir(guardado));

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }

    // ELIMINAR
    @Operation(summary = "Eliminar usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario eliminado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        if (userService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        userService.eliminar(id);

        return ResponseEntity.ok("Usuario eliminado");
    }

}
