package com.ElSauce.demo.controller.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ElSauce.demo.dto.PlatoRequest;
import com.ElSauce.demo.dto.PlatoResponse;
import com.ElSauce.demo.model.Plato;
import com.ElSauce.demo.service.PlatoService;

// Imports de Swagger / OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/platos")
@Tag(name="Platos", description="Gestión del menú del restaurante")
public class PlatoApiController {

    private final PlatoService platoService;

    public PlatoApiController(PlatoService platoService) {
        this.platoService = platoService;
    }

    // Convierte Entidad -> DTO
    private PlatoResponse convertirRespuesta(Plato plato) {
        return new PlatoResponse(
                plato.getId(),
                plato.getNombre(),
                plato.getPrecio(),
                plato.getDescripcion(),
                plato.getImagenRuta()
        );
    }

    // GET TODOS
    @Operation(summary="Listar platos")
    @GetMapping
    public ResponseEntity<List<PlatoResponse>> listar() {

        List<PlatoResponse> respuesta = platoService.obtenerTodos()
                .stream()
                .map(this::convertirRespuesta)
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }

    // GET POR ID
    @Operation(summary="Buscar plato por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {

        Optional<Plato> plato = platoService.obtenerPorId(id);

        if (plato.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Plato no encontrado");
        }

        return ResponseEntity.ok(convertirRespuesta(plato.get()));
    }

    // POST
    @Operation(summary="Crear plato")
    @PostMapping
    public ResponseEntity<PlatoResponse> crear(@Valid @RequestBody PlatoRequest request) {

        Plato plato = new Plato();

        plato.setNombre(request.getNombre());
        plato.setPrecio(request.getPrecio());
        plato.setDescripcion(request.getDescripcion());
        plato.setImagenRuta(request.getImagenRuta());

        Plato guardado = platoService.guardar(plato);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertirRespuesta(guardado));
    }

    // PUT
    @Operation(summary="Actualizar plato")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PlatoRequest request) {

        try {

            Plato plato = new Plato();

            plato.setNombre(request.getNombre());
            plato.setPrecio(request.getPrecio());
            plato.setDescripcion(request.getDescripcion());
            plato.setImagenRuta(request.getImagenRuta());

            Plato actualizado = platoService.actualizar(id, plato);

            return ResponseEntity.ok(convertirRespuesta(actualizado));

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());

        }
    }

    // DELETE
    @Operation(summary="Eliminar plato")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        if (platoService.obtenerPorId(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Plato no encontrado");
        }

        platoService.eliminar(id);

        return ResponseEntity.ok("Plato eliminado correctamente");
    }
}
