package be.ucll.group8.craftmanshipgroep8.chats.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void given_valid_message_and_ai_when_create_message_then_message_is_created_successfully() {
        // Given
        String messageText = "Hello, how can I help you?";
        Boolean isAi = true;

        // When
        Message message = new Message(messageText, isAi);

        // Then
        assertNotNull(message);
        assertEquals(messageText, message.getMessage());
        assertEquals(isAi, message.getAi());
        assertNotNull(message.getId());
        assertNotNull(message.getTimestamp());
    }

    @Test
    void given_user_message_when_create_message_then_ai_is_false() {
        // Given
        String messageText = "What is Java?";

        // When
        Message message = new Message(messageText, false);

        // Then
        assertNotNull(message);
        assertEquals(messageText, message.getMessage());
        assertFalse(message.getAi());
    }

    @Test
    void given_ai_message_when_create_message_then_ai_is_true() {
        // Given
        String messageText = "Java is a programming language.";

        // When
        Message message = new Message(messageText, true);

        // Then
        assertNotNull(message);
        assertEquals(messageText, message.getMessage());
        assertTrue(message.getAi());
    }

    @Test
    void given_new_message_when_create_message_then_timestamp_is_current_time() {
        // Given
        String messageText = "Test message";
        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        Message message = new Message(messageText, false);
        LocalDateTime afterCreation = LocalDateTime.now();

        // Then
        assertNotNull(message.getTimestamp());
        assertTrue(message.getTimestamp().isAfter(beforeCreation.minusSeconds(1)));
        assertTrue(message.getTimestamp().isBefore(afterCreation.plusSeconds(1)));
    }

    @Test
    void given_new_message_when_get_id_then_return_unique_message_id() {
        // Given
        Message message = new Message("Test", false);

        // When
        MessageId id = message.getId();

        // Then
        assertNotNull(id);
        assertNotNull(id.id());
    }

    @Test
    void given_multiple_messages_when_created_then_each_has_unique_id() {
        // Given & When
        Message message1 = new Message("Message 1", false);
        Message message2 = new Message("Message 2", true);
        Message message3 = new Message("Message 3", false);

        // Then
        assertNotEquals(message1.getId(), message2.getId());
        assertNotEquals(message2.getId(), message3.getId());
        assertNotEquals(message1.getId(), message3.getId());
    }

    @Test
    void given_empty_message_text_when_create_message_then_message_is_created() {
        // Given
        String emptyMessage = "";

        // When
        Message message = new Message(emptyMessage, false);

        // Then
        assertNotNull(message);
        assertEquals("", message.getMessage());
    }

    @Test
    void given_long_message_text_when_create_message_then_message_is_stored_completely() {
        // Given
        String longMessage = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa." +
            "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb. " +
            "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc.";

        // When
        Message message = new Message(longMessage, true);

        // Then
        assertEquals(longMessage, message.getMessage());
        assertEquals(longMessage.length(), message.getMessage().length());
    }

    @Test
    void given_message_with_special_characters_when_create_message_then_characters_are_preserved() {
        // Given
        String specialMessage = "Hello! @#$%^&*() <> \"quotes\" 'apostrophe'";

        // When
        Message message = new Message(specialMessage, false);

        // Then
        assertEquals(specialMessage, message.getMessage());
    }

    @Test
    void given_two_messages_when_created_sequentially_then_timestamps_are_chronological() {
        // Given & When
        Message message1 = new Message("First message", false);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Message message2 = new Message("Second message", true);

        // Then
        assertTrue(message1.getTimestamp().isBefore(message2.getTimestamp()) ||
            message1.getTimestamp().isEqual(message2.getTimestamp()));
    }

    @Test
    void given_message_when_get_message_then_return_correct_text() {
        // Given
        String expectedText = "Expected message text";
        Message message = new Message(expectedText, false);

        // When
        String actualText = message.getMessage();

        // Then
        assertEquals(expectedText, actualText);
    }

    @Test
    void given_message_when_get_ai_then_return_correct_value() {
        // Given
        Message aiMessage = new Message("AI response", true);
        Message userMessage = new Message("User query", false);

        // When
        Boolean aiFlag1 = aiMessage.getAi();
        Boolean aiFlag2 = userMessage.getAi();

        // Then
        assertTrue(aiFlag1);
        assertFalse(aiFlag2);
    }

    @Test
    void given_null_message_text_when_create_message_then_message_is_created_with_null() {
        // Given
        String nullMessage = null;

        // When
        Message message = new Message(nullMessage, false);

        // Then
        assertNotNull(message);
        assertNull(message.getMessage());
    }
}
