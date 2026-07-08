package com.ElSauce.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ElSauce.demo.model.Plato;
import com.ElSauce.demo.repository.PlatoRepository;

@Service
public class PlatoService {

    @Autowired
    private PlatoRepository repository;

    // Obtener todos los platos
    public List<Plato> obtenerTodos() {
        return repository.findAll();
    }

    // Obtener un plato por ID
    public Optional<Plato> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    // Guardar un plato
    public Plato guardar(Plato plato) {
        return repository.save(plato);
    }

    // Actualizar un plato
    public Plato actualizar(Long id, Plato plato) {

        Plato existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plato no encontrado"));

        existente.setNombre(plato.getNombre());
        existente.setPrecio(plato.getPrecio());
        existente.setDescripcion(plato.getDescripcion());
        existente.setImagenRuta(plato.getImagenRuta());

        return repository.save(existente);
    }

    // Eliminar un plato
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
