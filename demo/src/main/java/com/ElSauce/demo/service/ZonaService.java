package com.ElSauce.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ElSauce.demo.model.Zona;
import com.ElSauce.demo.repository.ZonaRepository;

@Service
public class ZonaService {
    @Autowired
    private ZonaRepository zonaRepository;

    public Optional<Zona> buscarZonaPorID(Integer id){
        return zonaRepository.findById(id);
    }
}
