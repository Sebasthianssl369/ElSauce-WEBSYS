package com.ElSauce.demo.controller.api;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ElSauce.demo.model.Zona;
import com.ElSauce.demo.service.ZonaService;

// Imports de Swagger / OpenAPI
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/zonas")
@Tag(name="Zonas", description="Zonas del restaurante")
public class ZonaApiController {

    private final ZonaService zonaService;

    public ZonaApiController(ZonaService zonaService) {
        this.zonaService = zonaService;
    }

    // Listar todas las zonas
    @Operation(summary="Listar zonas")
    @ApiResponses({
        @ApiResponse(responseCode="200", description="Listado correcto")
    })
    @GetMapping
    public List<Zona> listar() {
        return zonaService.obtenerTodasLasZonas();
    }

    // Buscar una zona por ID
    @Operation(summary="Buscar zona por ID")
    @ApiResponses({
        @ApiResponse(responseCode="200", description="Zona encontrada"),
        @ApiResponse(responseCode="404", description="Zona no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Zona> obtener(@PathVariable Integer id) {

        Optional<Zona> zona = zonaService.buscarZonaPorID(id);

        return zona.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
