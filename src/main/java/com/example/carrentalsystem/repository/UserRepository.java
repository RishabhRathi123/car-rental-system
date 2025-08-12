package com.example.carrentalsystem.repository;


import com.example.carrentalsystem.entity.User;
import com.example.carrentalsystem.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);
    Optional<User> findFirstByUsername(String username);
    User findByRole(UserRole userRole);
}
