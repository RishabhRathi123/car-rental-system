package com.example.carrentalsystem.service;

import com.example.carrentalsystem.entity.User;
import com.example.carrentalsystem.repository.jpa.UserRepository;
import com.example.carrentalsystem.repository.jdbc.UserJdbcRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserJdbcRepository userJdbcRepository;

    @Value("${spring.profiles.active:jpa}")
    private String activeProfile;

    public UserService(UserRepository userRepository, UserJdbcRepository userJdbcRepository) {
        this.userRepository = userRepository;
        this.userJdbcRepository = userJdbcRepository;
    }

    public User createUser(User user) {
        // Validate unique constraints
        if (existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }

        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        if ("jdbc".equals(activeProfile)) {
            return userJdbcRepository.save(user);
        } else {
            return userRepository.save(user);
        }
    }

    public Optional<User> getUserById(Long id) {
        if ("jdbc".equals(activeProfile)) {
            return userJdbcRepository.findById(id);
        } else {
            return userRepository.findById(id);
        }
    }

    public Optional<User> getUserByUsername(String username) {
        if ("jdbc".equals(activeProfile)) {
            return userJdbcRepository.findByUsername(username);
        } else {
            return userRepository.findByUsername(username);
        }
    }

    public Optional<User> getUserByEmail(String email) {
        if ("jdbc".equals(activeProfile)) {
            return userJdbcRepository.findByEmail(email);
        } else {
            return userRepository.findByEmail(email);
        }
    }

    public List<User> getAllUsers() {
        if ("jdbc".equals(activeProfile)) {
            return userJdbcRepository.findAll();
        } else {
            return userRepository.findAll();
        }
    }

    public User updateUser(User user) {
        // Check if user exists
        Optional<User> existingUser = getUserById(user.getId());
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }

        // Check for duplicate username/email (excluding current user)
        Optional<User> userWithSameUsername = getUserByUsername(user.getUsername());
        if (userWithSameUsername.isPresent() && !userWithSameUsername.get().getId().equals(user.getId())) {
            throw new RuntimeException("Username already exists: " + user.getUsername());
        }

        Optional<User> userWithSameEmail = getUserByEmail(user.getEmail());
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(user.getId())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        if ("jdbc".equals(activeProfile)) {
            return userJdbcRepository.save(user);
        } else {
            return userRepository.save(user);
        }
    }

    public void deleteUser(Long id) {
        if (!getUserById(id).isPresent()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        if ("jdbc".equals(activeProfile)) {
            userJdbcRepository.deleteById(id);
        } else {
            userRepository.deleteById(id);
        }
    }

    public boolean existsByUsername(String username) {
        if ("jdbc".equals(activeProfile)) {
            return userJdbcRepository.existsByUsername(username);
        } else {
            return userRepository.existsByUsername(username);
        }
    }

    public boolean existsByEmail(String email) {
        if ("jdbc".equals(activeProfile)) {
            return userJdbcRepository.existsByEmail(email);
        } else {
            return userRepository.existsByEmail(email);
        }
    }

    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> user = getUserByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user;
        }
        return Optional.empty();
    }
}
