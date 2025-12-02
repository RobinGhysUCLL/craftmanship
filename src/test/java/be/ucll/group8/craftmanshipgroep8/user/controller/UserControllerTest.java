package be.ucll.group8.craftmanshipgroep8.user.controller;

import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.AuthenticationRequest;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.AuthenticationResponse;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.SignUpRequest;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.SignupResponse;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void given_valid_signup_request_when_signup_then_return_created_with_token() {
        // Given
        SignUpRequest signUpRequest = new SignUpRequest(
            "ghys_pad",
            "Password123!",
            "ghys.pad@example.com"
        );

        // When
        ResponseEntity<SignupResponse> response = restTemplate.postForEntity(
            "/users/signup",
            signUpRequest,
            SignupResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().token());
        assertFalse(response.getBody().token().isEmpty());
    }

    @Test
    void given_existing_username_when_signup_then_return_error() {
        // Given
        User existingUser = new User("ghys_pad", "existing@example.com", "hashedPassword");
        userRepository.save(existingUser);

        SignUpRequest signUpRequest = new SignUpRequest(
            "ghys_pad",
            "Password123!",
            "newemail@example.com"
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/users/signup",
            signUpRequest,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("bestaat al"));
        assertTrue(response.getBody().contains("ghys_pad"));
    }

    @Test
    void given_existing_email_when_signup_then_return_error() {
        // Given
        User existingUser = new User("existing_user", "ghys.pad@example.com", "hashedPassword");
        userRepository.save(existingUser);

        SignUpRequest signUpRequest = new SignUpRequest(
            "new_username",
            "Password123!",
            "ghys.pad@example.com"
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/users/signup",
            signUpRequest,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("bestaat al"));
        assertTrue(response.getBody().contains("ghys.pad@example.com"));
    }

    @Test
    void given_invalid_email_when_signup_then_return_validation_error() {
        // Given
        SignUpRequest signUpRequest = new SignUpRequest(
            "ghys_pad",
            "Password123!",
            "invalid-email"
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/users/signup",
            signUpRequest,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_blank_username_when_signup_then_return_validation_error() {
        // Given
        SignUpRequest signUpRequest = new SignUpRequest(
            "",
            "Password123!",
            "ghys.pad@example.com"
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/users/signup",
            signUpRequest,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_blank_password_when_signup_then_return_validation_error() {
        // Given
        SignUpRequest signUpRequest = new SignUpRequest(
            "ghys_pad",
            "",
            "ghys.pad@example.com"
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/users/signup",
            signUpRequest,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_valid_credentials_when_login_then_return_token_and_username() {
        // Given
        String hashedPassword = passwordEncoder.encode("Password123!");
        User user = new User("ghys_pad", "ghys.pad@example.com", hashedPassword);
        userRepository.save(user);

        AuthenticationRequest authRequest = new AuthenticationRequest(
            "ghys.pad@example.com",
            "Password123!"
        );

        // When
        ResponseEntity<AuthenticationResponse> response = restTemplate.postForEntity(
            "/users/login",
            authRequest,
            AuthenticationResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().token());
        assertEquals("ghys_pad", response.getBody().username());
        assertFalse(response.getBody().token().isEmpty());
    }

    @Test
    void given_non_existing_email_when_login_then_return_error() {
        // Given
        AuthenticationRequest authRequest = new AuthenticationRequest(
            "nonexistent@example.com",
            "Password123!"
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/users/login",
            authRequest,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("bestaat niet"));
    }

    @Test
    void given_incorrect_password_when_login_then_return_error() {
        // Given
        String hashedPassword = passwordEncoder.encode("Password123!");
        User user = new User("ghys_pad", "ghys.pad@example.com", hashedPassword);
        userRepository.save(user);

        AuthenticationRequest authRequest = new AuthenticationRequest(
            "ghys.pad@example.com",
            "WrongPassword!"
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/users/login",
            authRequest,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Onjuist wachtwoord"));
    }

    @Test
    void given_null_email_when_login_then_return_validation_error() {
        // Given
        AuthenticationRequest authRequest = new AuthenticationRequest(
            null,
            "Password123!"
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/users/login",
            authRequest,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_null_password_when_login_then_return_validation_error() {
        // Given
        AuthenticationRequest authRequest = new AuthenticationRequest(
            "ghys.pad@example.com",
            null
        );

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/users/login",
            authRequest,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void given_complete_signup_and_login_flow_when_executed_then_user_can_authenticate() {
        // Given - Signup
        SignUpRequest signUpRequest = new SignUpRequest(
            "roel_crabbe",
            "SecurePass456!",
            "roel.crabbe@example.com"
        );

        // When - Signup
        ResponseEntity<SignupResponse> signupResponse = restTemplate.postForEntity(
            "/users/signup",
            signUpRequest,
            SignupResponse.class
        );

        // Then - Signup successful
        assertEquals(HttpStatus.OK, signupResponse.getStatusCode());
        assertNotNull(signupResponse.getBody());
        String signupToken = signupResponse.getBody().token();
        assertNotNull(signupToken);

        // Given - Login with same credentials
        AuthenticationRequest authRequest = new AuthenticationRequest(
            "roel.crabbe@example.com",
            "SecurePass456!"
        );

        // When - Login
        ResponseEntity<AuthenticationResponse> loginResponse = restTemplate.postForEntity(
            "/users/login",
            authRequest,
            AuthenticationResponse.class
        );

        // Then - Login successful
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertNotNull(loginResponse.getBody().token());
        assertEquals("roel_crabbe", loginResponse.getBody().username());
    }

    @Test
    void given_multiple_users_when_signup_then_all_users_saved_independently() {
        // Given
        SignUpRequest user1Request = new SignUpRequest(
            "ghys_pad",
            "Password123!",
            "ghys.pad@example.com"
        );

        SignUpRequest user2Request = new SignUpRequest(
            "roel_crabbe",
            "SecurePass456!",
            "roel.crabbe@example.com"
        );

        // When
        ResponseEntity<SignupResponse> response1 = restTemplate.postForEntity(
            "/users/signup",
            user1Request,
            SignupResponse.class
        );

        ResponseEntity<SignupResponse> response2 = restTemplate.postForEntity(
            "/users/signup",
            user2Request,
            SignupResponse.class
        );

        // Then
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertNotEquals(response1.getBody().token(), response2.getBody().token());
        assertEquals(2, userRepository.count());
        assertNotNull(userRepository.findUserByEmail("ghys.pad@example.com"));
        assertNotNull(userRepository.findUserByEmail("roel.crabbe@example.com"));
    }

    @Test
    void given_user_when_password_is_stored_then_password_is_hashed() {
        // Given
        SignUpRequest signUpRequest = new SignUpRequest(
            "ghys_pad",
            "PlainPassword123!",
            "ghys.pad@example.com"
        );

        // When
        restTemplate.postForEntity(
            "/users/signup",
            signUpRequest,
            SignupResponse.class
        );

        // Then
        User savedUser = userRepository.findUserByEmail("ghys.pad@example.com");
        assertNotNull(savedUser);
        assertNotEquals("PlainPassword123!", savedUser.getPassword());
        assertTrue(passwordEncoder.matches("PlainPassword123!", savedUser.getPassword()));
    }
}
