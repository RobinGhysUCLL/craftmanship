package be.ucll.group8.craftmanshipgroep8.user.repository;

import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.domain.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User robin;
    private User roel;

    @BeforeEach
    void setUp() {
        robin = new User("ghys_pad", "ghys.pad@example.com", "Password123!");
        roel = new User("roel_crabbe", "roel.crabbe@example.com", "Password123!");
    }

    @Test
    void given_valid_user_when_save_then_user_is_saved_successfully() {
        // Given

        // When
        User savedUser = userRepository.save(robin);

        // Then
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(robin.getUserName(), savedUser.getUserName());
        assertEquals(robin.getEmail(), savedUser.getEmail());
    }

    @Test
    void given_saved_user_when_find_by_id_then_return_user() {
        // Given
        User savedUser = userRepository.save(robin);
        UserId userId = savedUser.getId();

        // When
        Optional<User> foundUser = userRepository.findById(userId);

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getUserName(), foundUser.get().getUserName());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    void given_non_existent_id_when_find_by_id_then_return_empty() {
        // Given
        UserId nonExistentId = new UserId();

        // When
        Optional<User> foundUser = userRepository.findById(nonExistentId);

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void given_saved_user_when_find_by_username_then_return_user() {
        // Given
        userRepository.save(robin);

        // When
        User foundUser = userRepository.findByUserName("ghys_pad");

        // Then
        assertNotNull(foundUser);
        assertEquals(robin.getUserName(), foundUser.getUserName());
        assertEquals(robin.getEmail(), foundUser.getEmail());
    }

    @Test
    void given_non_existent_username_when_find_by_username_then_return_null() {
        // Given
        userRepository.save(robin);

        // When
        User foundUser = userRepository.findByUserName("raf");

        // Then
        assertNull(foundUser);
    }

    @Test
    void given_saved_user_when_find_user_by_username_then_return_user() {
        // Given
        userRepository.save(roel);

        // When
        User foundUser = userRepository.findUserByUserName("roel_crabbe");

        // Then
        assertNotNull(foundUser);
        assertEquals(roel.getUserName(), foundUser.getUserName());
        assertEquals(roel.getEmail(), foundUser.getEmail());
    }

    @Test
    void given_saved_user_when_find_user_by_email_then_return_user() {
        // Given
        userRepository.save(robin);

        // When
        User foundUser = userRepository.findUserByEmail("ghys.pad@example.com");

        // Then
        assertNotNull(foundUser);
        assertEquals(robin.getEmail(), foundUser.getEmail());
        assertEquals(robin.getUserName(), foundUser.getUserName());
    }

    @Test
    void given_non_existent_email_when_find_user_by_email_then_return_null() {
        // Given
        userRepository.save(robin);

        // When
        User foundUser = userRepository.findUserByEmail("gert@example.com");

        // Then
        assertNull(foundUser);
    }

    @Test
    void given_multiple_users_when_find_all_then_return_all_users() {
        // Given
        userRepository.save(robin);
        userRepository.save(roel);

        // When
        List<User> allUsers = userRepository.findAll();

        // Then
        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.getUserName().equals("ghys_pad")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getUserName().equals("roel_crabbe")));
    }

    @Test
    void given_saved_user_when_update_user_then_user_is_updated() {
        // Given
        User savedUser = userRepository.save(robin);
        String newEmail = "robin.ghys@example.be";

        // When
        savedUser.setEmail(newEmail);
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertNotNull(updatedUser);
        assertEquals(newEmail, updatedUser.getEmail());
        assertEquals(savedUser.getId(), updatedUser.getId());
    }

    @Test
    void given_saved_user_when_delete_then_user_is_removed() {
        // Given
        User savedUser = userRepository.save(robin);
        UserId userId = savedUser.getId();

        // When
        userRepository.deleteById(userId);
        Optional<User> deletedUser = userRepository.findById(userId);

        // Then
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void given_saved_user_when_delete_by_entity_then_user_is_removed() {
        // Given
        User savedUser = userRepository.save(robin);
        UserId userId = savedUser.getId();

        // When
        userRepository.delete(savedUser);
        Optional<User> deletedUser = userRepository.findById(userId);

        // Then
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void given_empty_repository_when_find_all_then_return_empty_list() {
        // Given

        // When
        List<User> allUsers = userRepository.findAll();

        // Then
        assertNotNull(allUsers);
        assertTrue(allUsers.isEmpty());
    }

    @Test
    void given_multiple_users_when_count_then_return_correct_count() {
        // Given
        userRepository.save(robin);
        userRepository.save(roel);

        // When
        long count = userRepository.count();

        // Then
        assertEquals(2, count);
    }

    @Test
    void given_saved_user_when_exists_by_id_then_return_true() {
        // Given
        User savedUser = userRepository.save(robin);
        UserId userId = savedUser.getId();

        // When
        boolean exists = userRepository.existsById(userId);

        // Then
        assertTrue(exists);
    }

    @Test
    void given_non_existent_user_when_exists_by_id_then_return_false() {
        // Given
        UserId nonExistentId = new UserId();

        // When
        boolean exists = userRepository.existsById(nonExistentId);

        // Then
        assertFalse(exists);
    }

    @Test
    void given_users_with_different_cases_when_find_by_username_then_respect_case_sensitivity() {
        // Given
        User lowerCaseUser = new User("raf", "raf@example.com", "Password123!");
        User upperCaseUser = new User("RAF", "RAF@example.com", "Password123!");
        userRepository.save(lowerCaseUser);
        userRepository.save(upperCaseUser);

        // When
        User foundLower = userRepository.findByUserName("raf");
        User foundUpper = userRepository.findByUserName("RAF");

        // Then
        assertNotNull(foundLower);
        assertNotNull(foundUpper);
        assertEquals("raf@example.com", foundLower.getEmail());
        assertEquals("RAF@example.com", foundUpper.getEmail());
    }
}
