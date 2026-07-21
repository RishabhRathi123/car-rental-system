package com.example.carrentalsystem.services.auth;

import com.example.carrentalsystem.dto.*;
import com.example.carrentalsystem.entity.User;
import com.example.carrentalsystem.enums.UserRole;
import com.example.carrentalsystem.repository.UserRepository;
import com.example.carrentalsystem.utils.JWTUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    @PostConstruct
    public void createAdminAccount(){
        User adminAccount = userRepository.findByRole(UserRole.ADMIN);
        if (adminAccount == null) {
            User newAdminAccount = new User();
            newAdminAccount.setUsername("Admin");
            newAdminAccount.setPassword(new BCryptPasswordEncoder().encode("Admin123"));
            newAdminAccount.setEmail("admin@test.com");
            newAdminAccount.setRole(UserRole.ADMIN);
            userRepository.save(newAdminAccount);
            System.out.println("Admin account created successfully");
        }
    }
    @Override
    public UserDto createCustomer(SignupRequest signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setRole(UserRole.CUSTOMER); // default to CUSTOMER
        User createdUser = userRepository.save(user);
        UserDto userDto = new UserDto();
        userDto.setId(createdUser.getId());
        return userDto;
    }

    @Override
    public boolean hasCustomerWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }

    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        User user = userRepository.findFirstByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String jwt = jwtUtil.generateToken(user);
        return AuthenticationResponse.builder()
                .jwt(jwt)
                .userId(user.getId())
                .userRole(user.getRole().name())
                .userName(user.getUsername())
                .build();
    }

}
