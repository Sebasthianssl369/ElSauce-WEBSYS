package com.ElSauce.demo.repository;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ElSauce.demo.model.HorarioPrefijado;

public interface HorarioPrefijadoRepository extends JpaRepository<HorarioPrefijado,Short> {

    @Query("SELECT h.hora FROM HorarioPrefijado h ORDER BY h.hora")
    List<LocalTime> findAllHoras();
}
