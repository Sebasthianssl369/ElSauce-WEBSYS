package com.ElSauce.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ElSauce.demo.model.Mesa;
import com.ElSauce.demo.repository.MesaRepository;

@Service
public class MesaService {
    @Autowired
    private MesaRepository mesaRepository;

    public List<Mesa> obtenerTodasLasMesas() {
        return mesaRepository.findAll();
    }

    public void guardarMesa(Mesa mesa) {
        mesaRepository.save(mesa);
    }

    public void eliminarMesa(Integer id) {
        mesaRepository.deleteById(id);
    }

    public int obtenerMesaSegunPersonas(int personas) {

        if (personas <= 2) return 1;  // mesa 2
        if (personas <= 4) return 2;  // mesa 4
        if (personas <= 6) return 3;  // mesa 6
        if (personas <= 8) return 4;  // mesa 8

        return -1; // no disponible
    }
}
