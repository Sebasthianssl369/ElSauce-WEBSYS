package com.ElSauce.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ElSauce.demo.model.Mesa;

@Repository
public interface MesaRepository extends JpaRepository<Mesa,Integer>{
    List<Mesa> findByZonaIdAndCapacidadGreaterThanEqualOrderByCapacidadAsc(Integer zonaId, Integer capacidad);
}
