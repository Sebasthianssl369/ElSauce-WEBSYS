package com.ElSauce.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ElSauce.demo.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva,Long>{

}
