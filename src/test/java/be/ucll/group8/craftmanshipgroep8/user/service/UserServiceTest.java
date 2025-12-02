package be.ucll.group8.craftmanshipgroep8.user.service;

import be.ucll.group8.craftmanshipgroep8.config.service.JwtService;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.AuthenticationResponse;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.SignUpRequest;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.SignupResponse;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User robin;
    private SignUpRequest signUpRequest;

    @BeforeEach
    void setUp() {
        robin = new User("ghys_pad", "ghys.pad@example.com", "Password123!");
        signUpRequest = new SignUpRequest("roel_crabbe", "Password123!", "roel@example.com");
    }

    @Test
    void given_existing_username_when_find_user_by_username_then_return_user() {
        // Given
        when(userRepository.findUserByUserName("ghys_pad")).thenReturn(robin);

        // When
        User foundUser = userService.findUserByUsername("ghys_pad");

        // Then
        assertNotNull(foundUser);
        assertEquals("ghys_pad", foundUser.getUserName());
        assertEquals("ghys.pad@example.com", foundUser.getEmail());
        verify(userRepository, times(1)).findUserByUserName("ghys_pad");
    }

    @Test
    void given_non_existing_username_when_find_user_by_username_then_return_null() {
        // Given
        when(userRepository.findUserByUserName("raf")).thenReturn(null);

        // When
        User foundUser = userService.findUserByUsername("raf");

        // Then
        assertNull(foundUser);
        verify(userRepository, times(1)).findUserByUserName("raf");
    }

    @Test
    void given_existing_username_when_user_exists_by_username_then_return_true() {
        // Given
        when(userRepository.findUserByUserName("ghys_pad")).thenReturn(robin);

        // When
        boolean exists = userService.userExistsByUsername("ghys_pad");

        // Then
        assertTrue(exists);
        verify(userRepository, times(1)).findUserByUserName("ghys_pad");
    }

    @Test
    void given_non_existing_username_when_user_exists_by_username_then_return_false() {
        // Given
        when(userRepository.findUserByUserName("raf")).thenReturn(null);

        // When
        boolean exists = userService.userExistsByUsername("raf");

        // Then
        assertFalse(exists);
        verify(userRepository, times(1)).findUserByUserName("raf");
    }

    @Test
    void given_existing_email_when_find_user_by_email_then_return_user() {
        // Given
        when(userRepository.findUserByEmail("ghys.pad@example.com")).thenReturn(robin);

        // When
        User foundUser = userService.findUserByEmail("ghys.pad@example.com");

        // Then
        assertNotNull(foundUser);
        assertEquals("ghys.pad@example.com", foundUser.getEmail());
        assertEquals("ghys_pad", foundUser.getUserName());
        verify(userRepository, times(1)).findUserByEmail("ghys.pad@example.com");
    }

    @Test
    void given_non_existing_email_when_find_user_by_email_then_return_null() {
        // Given
        when(userRepository.findUserByEmail("raf@example.com")).thenReturn(null);

        // When
        User foundUser = userService.findUserByEmail("raf@example.com");

        // Then
        assertNull(foundUser);
        verify(userRepository, times(1)).findUserByEmail("raf@example.com");
    }

    @Test
    void given_existing_email_when_user_exists_by_email_then_return_true() {
        // Given
        when(userRepository.findUserByEmail("ghys.pad@example.com")).thenReturn(robin);

        // When
        boolean exists = userService.userExistsByEmail("ghys.pad@example.com");

        // Then
        assertTrue(exists);
        verify(userRepository, times(1)).findUserByEmail("ghys.pad@example.com");
    }

    @Test
    void given_valid_credentials_when_authenticate_then_return_authentication_response() {
        // Given
        String email = "ghys.pad@example.com";
        String password = "Password123!";
        String token = "jwt-token-12345";

        when(userRepository.findUserByEmail(email)).thenReturn(robin);
        when(passwordEncoder.matches(password, robin.getPassword())).thenReturn(true);
        when(jwtService.generateToken(robin)).thenReturn(token);

        // When
        AuthenticationResponse response = userService.authenticate(email, password);

        // Then
        assertNotNull(response);
        assertEquals(token, response.token());
        assertEquals("ghys_pad", response.username());
        verify(userRepository, times(2)).findUserByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, robin.getPassword());
        verify(jwtService, times(1)).generateToken(robin);
    }

    @Test
    void given_non_existing_email_when_authenticate_then_throw_exception() {
        // Given
        String email = "raf@example.com";
        String password = "Password123!";

        when(userRepository.findUserByEmail(email)).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.authenticate(email, password));
        assertTrue(exception.getMessage().contains("bestaat niet"));
        verify(userRepository, times(1)).findUserByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void given_incorrect_password_when_authenticate_then_throw_exception() {
        // Given
        String email = "ghys.pad@example.com";
        String wrongPassword = "WrongPassword!";

        when(userRepository.findUserByEmail(email)).thenReturn(robin);
        when(passwordEncoder.matches(wrongPassword, robin.getPassword())).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.authenticate(email, wrongPassword));
        assertTrue(exception.getMessage().contains("Onjuist wachtwoord"));
        verify(userRepository, times(2)).findUserByEmail(email);
        verify(passwordEncoder, times(1)).matches(wrongPassword, robin.getPassword());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void given_valid_signup_request_when_signup_then_return_signup_response() {
        // Given
        String hashedPassword = "Password123!";
        String token = "jwt-token-12345";
        User savedUser = new User(signUpRequest.username(), signUpRequest.email(), hashedPassword);

        when(userRepository.findUserByUserName(signUpRequest.username())).thenReturn(null);
        when(userRepository.findUserByEmail(signUpRequest.email())).thenReturn(null);
        when(passwordEncoder.encode(signUpRequest.password())).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(token);

        // When
        SignupResponse response = userService.signup(signUpRequest);

        // Then
        assertNotNull(response);
        assertEquals(token, response.token());
        verify(userRepository, times(1)).findUserByUserName(signUpRequest.username());
        verify(userRepository, times(1)).findUserByEmail(signUpRequest.email());
        verify(passwordEncoder, times(1)).encode(signUpRequest.password());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    @Test
    void given_existing_username_when_signup_then_throw_exception() {
        // Given
        when(userRepository.findUserByUserName(signUpRequest.username())).thenReturn(robin);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.signup(signUpRequest));
        assertTrue(exception.getMessage().contains("bestaat al"));
        assertTrue(exception.getMessage().contains(signUpRequest.username()));
        verify(userRepository, times(1)).findUserByUserName(signUpRequest.username());
        verify(userRepository, never()).findUserByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void given_existing_email_when_signup_then_throw_exception() {
        // Given
        when(userRepository.findUserByUserName(signUpRequest.username())).thenReturn(null);
        when(userRepository.findUserByEmail(signUpRequest.email())).thenReturn(robin);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.signup(signUpRequest));
        assertTrue(exception.getMessage().contains("bestaat al"));
        assertTrue(exception.getMessage().contains(signUpRequest.email()));
        verify(userRepository, times(1)).findUserByUserName(signUpRequest.username());
        verify(userRepository, times(1)).findUserByEmail(signUpRequest.email());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void given_signup_request_when_signup_then_password_is_hashed() {
        // Given
        String plainPassword = signUpRequest.password();
        String hashedPassword = "Password123!";
        String token = "jwt-token-12345";

        when(userRepository.findUserByUserName(signUpRequest.username())).thenReturn(null);
        when(userRepository.findUserByEmail(signUpRequest.email())).thenReturn(null);
        when(passwordEncoder.encode(plainPassword)).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals(hashedPassword, user.getPassword());
            return user;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn(token);

        // When
        SignupResponse response = userService.signup(signUpRequest);

        // Then
        assertNotNull(response);
        verify(passwordEncoder, times(1)).encode(plainPassword);
    }

    @Test
    void given_authenticate_request_when_authenticate_then_verify_password_matches() {
        // Given
        String email = "ghys.pad@example.com";
        String password = "Password123!";
        String token = "jwt-token-12345";

        when(userRepository.findUserByEmail(email)).thenReturn(robin);
        when(passwordEncoder.matches(password, robin.getPassword())).thenReturn(true);
        when(jwtService.generateToken(robin)).thenReturn(token);

        // When
        AuthenticationResponse response = userService.authenticate(email, password);

        // Then
        assertNotNull(response);
        verify(passwordEncoder, times(1)).matches(password, robin.getPassword());
    }
}
