package com.ElSauce.demo.service;


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

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // Registro de usuario normal con rol_id = 2
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Role role = roleRepository.findById((short) 2)
                .orElseThrow(() -> new RuntimeException("Rol con id 2 no encontrado"));

        user.setRole(role);
        user.setIsActive(true);

        return userRepository.save(user);
    }

    // Login sencillo
     public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPasswordHash().equals(password)) {
            return user;
        }
        return null;
    }
}

