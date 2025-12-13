package be.ucll.group8.craftmanshipgroep8.chats.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChatIdTest {

    @Test
    void given_valid_uuid_when_create_chat_id_then_chat_id_is_created_successfully() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        ChatId chatId = new ChatId(uuid);

        // Then
        assertNotNull(chatId);
        assertEquals(uuid, chatId.id());
    }

    @Test
    void given_no_argument_when_create_chat_id_then_generate_random_uuid() {
        // Given & When
        ChatId chatId = new ChatId();

        // Then
        assertNotNull(chatId);
        assertNotNull(chatId.id());
    }

    @Test
    void given_null_uuid_when_create_chat_id_then_throw_exception() {
        // Given
        UUID nullUuid = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new ChatId(nullUuid)
        );
        assertTrue(exception.getMessage().contains("MessageId cannot be null"));
    }

    @Test
    void given_same_uuid_when_create_chat_ids_then_chat_ids_are_equal() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        ChatId chatId1 = new ChatId(uuid);
        ChatId chatId2 = new ChatId(uuid);

        // Then
        assertEquals(chatId1, chatId2);
    }

    @Test
    void given_different_uuids_when_create_chat_ids_then_chat_ids_are_not_equal() {
        // Given
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        // When
        ChatId chatId1 = new ChatId(uuid1);
        ChatId chatId2 = new ChatId(uuid2);

        // Then
        assertNotEquals(chatId1, chatId2);
    }

    @Test
    void given_multiple_chat_ids_when_created_without_argument_then_all_are_unique() {
        // Given & When
        ChatId chatId1 = new ChatId();
        ChatId chatId2 = new ChatId();
        ChatId chatId3 = new ChatId();

        // Then
        assertNotEquals(chatId1, chatId2);
        assertNotEquals(chatId2, chatId3);
        assertNotEquals(chatId1, chatId3);
    }

    @Test
    void given_chat_id_when_get_id_then_return_correct_uuid() {
        // Given
        UUID expectedUuid = UUID.randomUUID();
        ChatId chatId = new ChatId(expectedUuid);

        // When
        UUID actualUuid = chatId.id();

        // Then
        assertEquals(expectedUuid, actualUuid);
    }
}
