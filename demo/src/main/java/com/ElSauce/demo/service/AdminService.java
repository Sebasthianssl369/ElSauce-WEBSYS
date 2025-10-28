package com.ElSauce.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ElSauce.demo.model.User;
import com.ElSauce.demo.repository.RoleRepository;
import com.ElSauce.demo.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Login exclusivo para administradores (role_id = 1)
     */
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);

        // Verifica existencia, contraseña y rol de administrador
        if (user != null &&
            user.getRole() != null &&
            user.getRole().getId() == 1 &&
            passwordEncoder.matches(password, user.getPasswordHash())) {

            return user; // Login exitoso
        }

        return null; // No autorizado o no válido
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

