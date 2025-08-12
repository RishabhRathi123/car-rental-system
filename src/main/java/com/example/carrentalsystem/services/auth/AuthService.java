package com.example.carrentalsystem.services.auth;

import com.example.carrentalsystem.dto.*;
import com.example.carrentalsystem.entity.User;

public interface AuthService {
    UserDto createCustomer(SignupRequest signupRequest);
    boolean hasCustomerWithEmail(String email);
    AuthenticationResponse login(AuthenticationRequest request);
}
