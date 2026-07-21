

import com.example.carrentalsystem.controller.AuthController;
import com.example.carrentalsystem.dto.*;
import com.example.carrentalsystem.entity.User;
import com.example.carrentalsystem.enums.UserRole;
import com.example.carrentalsystem.repository.UserRepository;
import com.example.carrentalsystem.services.auth.AuthService;
import com.example.carrentalsystem.services.jwt.UserService;
import com.example.carrentalsystem.utils.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignupCustomer_Success() {
        SignupRequest signupRequest = new SignupRequest("testuser", "test@example.com", "password", "customer");
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        when(authService.hasCustomerWithEmail(signupRequest.getEmail())).thenReturn(false);
        when(authService.createCustomer(signupRequest)).thenReturn(userDto);

        ResponseEntity<?> response = authController.signupCustomer(signupRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDto, response.getBody());
    }

    @Test
    void testSignupCustomer_EmailExists() {
        SignupRequest signupRequest = new SignupRequest("testuser", "test@example.com", "password", "customer");
        when(authService.hasCustomerWithEmail(signupRequest.getEmail())).thenReturn(true);

        ResponseEntity<?> response = authController.signupCustomer(signupRequest);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals("Email already exists", response.getBody());
    }

    @Test
    void testSignupCustomer_CreationFails() {
        SignupRequest signupRequest = new SignupRequest("testuser", "test@example.com", "password", "customer");
        when(authService.hasCustomerWithEmail(signupRequest.getEmail())).thenReturn(false);
        when(authService.createCustomer(signupRequest)).thenReturn(null);

        ResponseEntity<?> response = authController.signupCustomer(signupRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Customer not created", response.getBody());
    }

    @Test
    void testLogin_Success() {
        AuthenticationRequest authRequest = new AuthenticationRequest("test@example.com", "password");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setRole(UserRole.CUSTOMER);

        UserDetails userDetails = user;
        String token = "mock-jwt-token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userService.userDetailsService().loadUserByUsername(authRequest.getEmail())).thenReturn(userDetails);
        when(userRepository.findFirstByEmail(authRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        AuthenticationResponse response = authController.createAuthenticationToken(authRequest);

        assertEquals(token, response.getJwt());
        assertEquals(user.getId(), response.getUserId());
        assertEquals(user.getRole().name(), response.getUserRole());
        assertEquals(user.getUsername(), response.getUserName());
    }

    @Test
    void testLogin_BadCredentials() {
        AuthenticationRequest authRequest = new AuthenticationRequest("test@example.com", "wrongpass");
        doThrow(new BadCredentialsException("Incorrect username or password."))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(BadCredentialsException.class, () ->
                authController.createAuthenticationToken(authRequest));
    }
}
