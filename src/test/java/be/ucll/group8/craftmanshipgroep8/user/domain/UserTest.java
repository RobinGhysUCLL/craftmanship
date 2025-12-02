package be.ucll.group8.craftmanshipgroep8.user.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void given_valid_user_when_added_to_db_then_user_successfully_gets_added() {
        // Given
        String userName = "ghys_pad";
        String email = "ghys.pad@example.com";
        String password = "Password123!";

        // When
        User user = new User(userName, email, password);

        // Then
        assertNotNull(user);
        assertEquals(userName, user.getUserName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertNotNull(user.getId());
    }

    @Test
    void given_valid_user_when_pass_validation_then_all_fields_are_valid() {
        // Given
        User user = new User("roel_crabbe", "roel@example.com", "Password123!");

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void given_invalid_user_when_username_is_null_then_fail_validation() {
        // Given
        User user = new User(null, "raf@example.com", "Password123!");

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Username cannot be null.", violation.getMessage());
        assertEquals("userName", violation.getPropertyPath().toString());
    }

    @Test
    void given_invalid_user_when_password_is_null_then_fail_validation() {
        // Given
        User user = new User("refref", "refref@example.com", null);

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Password cannot be null.", violation.getMessage());
        assertEquals("password", violation.getPropertyPath().toString());
    }

    @Test
    void given_invalid_user_when_email_is_invalid_then_fail_validation() {
        // Given
        User user = new User("raf", "invalid-email", "Password123!");

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Email format is invalid")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"robak@example.com", "robak.tabak@test.be"})
    void given_user_when_email_is_valid_then_pass_validation(String validEmail) {
        // Given
        User user = new User("robak", validEmail, "Password123!");

        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void given_valid_user_when_username_is_called_then_update_username() {
        // Given
        User user = new User("ra", "raf@example.com", "Password123!");
        String newUserName = "raf";

        // When
        user.setUserName(newUserName);

        // Then
        assertEquals(newUserName, user.getUserName());
    }

    @Test
    void given_valid_user_when_password_is_called_then_update_password() {
        // Given
        User user = new User("jhonDoe", "jhonDoe@example.com", "oldPassword!");
        String newPassword = "Password123!";

        // When
        user.setPassword(newPassword);

        // Then
        assertEquals(newPassword, user.getPassword());
    }

    @Test
    void given_valid_user_when_email_is_called_then_update_email() {
        // Given
        User user = new User("roel", "roel@example.com", "Password123!");
        String newEmail = "roel@example.be";

        // When
        user.setEmail(newEmail);

        // Then
        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void given_users_when_multiple_users_are_created_then_generate_unique_ids() {
        // Given
        User user1 = new User("user1", "user1@example.com", "password1");
        User user2 = new User("user2", "user2@example.com", "password2");

        // When
        UserId id1 = user1.getId();
        UserId id2 = user2.getId();

        // Then
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }
}
