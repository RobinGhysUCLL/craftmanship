package be.ucll.group8.craftmanshipgroep8.user.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    void given_new_user_when_UUID_is_provided_then_create_user_id() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        UserId userId = new UserId(uuid);

        // Then
        assertNotNull(userId);
        assertEquals(uuid, userId.id());
    }

    @Test
    void given_new_user_when_no_argument_constructor_is_used_then_generate_random_UUID() {
        // Given & When
        UserId userId = new UserId();

        // Then
        assertNotNull(userId);
        assertNotNull(userId.id());
    }

    @Test
    void given_new_user_when_UUID_is_null_should_throw_exception() {
        // Given
        UUID nullUuid = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new UserId(nullUuid)
        );
        assertTrue(exception.getMessage().contains("UserId cannot be null"));
    }

    @Test
    void given_new_users_when_user_ids_have_same_UUID_should_be_equal() {
        // Given
        UUID uuid = UUID.randomUUID();
        UserId userId1 = new UserId(uuid);
        UserId userId2 = new UserId(uuid);

        // When & Then
        assertEquals(userId1, userId2);
    }

    @Test
    void given_new_users_when_user_ids_have_different_UUIDs_then_not_equal() {
        // Given
        UserId userId1 = new UserId();
        UserId userId2 = new UserId();

        // When & Then
        assertNotEquals(userId1, userId2);
    }

    @Test
    void given_new_users_when_called_multiple_times_then_generate_unique_ids() {
        // Given & When
        UserId userId1 = new UserId();
        UserId userId2 = new UserId();
        UserId userId3 = new UserId();

        // Then
        assertNotEquals(userId1, userId2);
        assertNotEquals(userId2, userId3);
        assertNotEquals(userId1, userId3);
    }
}
