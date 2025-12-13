package be.ucll.group8.craftmanshipgroep8.chats.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageIdTest {

    @Test
    void given_valid_uuid_when_create_message_id_then_message_id_is_created_successfully() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        MessageId messageId = new MessageId(uuid);

        // Then
        assertNotNull(messageId);
        assertEquals(uuid, messageId.id());
    }

    @Test
    void given_no_argument_when_create_message_id_then_generate_random_uuid() {
        // Given & When
        MessageId messageId = new MessageId();

        // Then
        assertNotNull(messageId);
        assertNotNull(messageId.id());
    }

    @Test
    void given_null_uuid_when_create_message_id_then_throw_exception() {
        // Given
        UUID nullUuid = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new MessageId(nullUuid)
        );
        assertTrue(exception.getMessage().contains("MessageId cannot be null"));
    }

    @Test
    void given_same_uuid_when_create_message_ids_then_message_ids_are_equal() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        MessageId messageId1 = new MessageId(uuid);
        MessageId messageId2 = new MessageId(uuid);

        // Then
        assertEquals(messageId1, messageId2);
    }

    @Test
    void given_different_uuids_when_create_message_ids_then_message_ids_are_not_equal() {
        // Given
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        // When
        MessageId messageId1 = new MessageId(uuid1);
        MessageId messageId2 = new MessageId(uuid2);

        // Then
        assertNotEquals(messageId1, messageId2);
    }

    @Test
    void given_multiple_message_ids_when_created_without_argument_then_all_are_unique() {
        // Given & When
        MessageId messageId1 = new MessageId();
        MessageId messageId2 = new MessageId();
        MessageId messageId3 = new MessageId();

        // Then
        assertNotEquals(messageId1, messageId2);
        assertNotEquals(messageId2, messageId3);
        assertNotEquals(messageId1, messageId3);
    }

    @Test
    void given_message_id_when_get_id_then_return_correct_uuid() {
        // Given
        UUID expectedUuid = UUID.randomUUID();
        MessageId messageId = new MessageId(expectedUuid);

        // When
        UUID actualUuid = messageId.id();

        // Then
        assertEquals(expectedUuid, actualUuid);
    }

    @Test
    void given_message_id_when_use_as_record_then_equals_and_hashcode_work_correctly() {
        // Given
        UUID uuid = UUID.randomUUID();
        MessageId messageId1 = new MessageId(uuid);
        MessageId messageId2 = new MessageId(uuid);
        MessageId messageId3 = new MessageId(UUID.randomUUID());

        // When & Then
        assertEquals(messageId1, messageId2);
        assertEquals(messageId1.hashCode(), messageId2.hashCode());
        assertNotEquals(messageId1, messageId3);
        assertNotEquals(messageId1.hashCode(), messageId3.hashCode());
    }
}
