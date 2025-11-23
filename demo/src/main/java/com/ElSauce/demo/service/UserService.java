package com.ElSauce.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.ElSauce.demo.model.Role;
import com.ElSauce.demo.model.User;
import com.ElSauce.demo.repository.RoleRepository;
import com.ElSauce.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registro de usuario normal con rol_id = 2
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Role role = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Rol con id 2 no encontrado"));

        user.setRole(role);
        user.setIsActive(true);

        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);
        return userRepository.save(user);
    }

    // Login sencillo
     public User login(String email, String password) {
    User user = userRepository.findByEmail(email);
    if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
        return user;
    }
    return null;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }
}

