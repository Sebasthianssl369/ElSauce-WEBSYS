package com.ElSauce.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ElSauce.demo.model.Plato;

@Repository
public interface PlatoRepository extends JpaRepository<Plato,Long>{

}
