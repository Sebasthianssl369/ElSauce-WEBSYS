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

public Mesa obtenerMesaSegunPersonasYZona(Integer personas, Integer zonaId) {
        Integer personasMax = switch (personas) {
        case 1,2 -> 2;
        case 3,4 -> 4;
        case 5,6 -> 6;
        case 7,8 -> 8;
        default -> personas;
    };
        return mesaRepository.findByZona_IdAndCapacidad(zonaId, personasMax);
   }
}
