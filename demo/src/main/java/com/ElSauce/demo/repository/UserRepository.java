package com.ElSauce.demo.repository;

import com.ElSauce.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar por email (login)
    User findByEmail(String email);

    // Verificar existencia por email rápido (para registro)
    boolean existsByEmail(String email);
}

