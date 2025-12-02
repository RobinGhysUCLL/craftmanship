package be.ucll.group8.craftmanshipgroep8.chats.domain;

import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {

    private User robin;
    private Chat testChat;

    @BeforeEach
    void setUp() {
        robin = new User("ghys_pad", "ghys.pad@example.com", "Password123!");
        testChat = new Chat(robin);
    }

    @Test
    void given_valid_user_when_create_chat_then_chat_is_created_successfully() {
        // Given

        // When
        Chat chat = new Chat(robin);

        // Then
        assertNotNull(chat);
        assertEquals(robin, chat.getUser());
        assertNotNull(chat.getMessages());
        assertTrue(chat.getMessages().isEmpty());
    }

    @Test
    void given_new_chat_when_get_user_then_return_associated_user() {
        // Given

        // When
        User retrievedUser = testChat.getUser();

        // Then
        assertNotNull(retrievedUser);
        assertEquals("ghys_pad", retrievedUser.getUserName());
        assertEquals("ghys.pad@example.com", retrievedUser.getEmail());
    }

    @Test
    void given_new_chat_when_get_messages_then_return_empty_list() {
        // Given

        // When
        List<Message> messages = testChat.getMessages();

        // Then
        assertNotNull(messages);
        assertTrue(messages.isEmpty());
        assertEquals(0, messages.size());
    }

    @Test
    void given_chat_when_add_message_then_message_is_added_to_list() {
        // Given
        Message message = new Message("Hello, how can I help you?", false);

        // When
        testChat.addMessage(message);

        // Then
        assertEquals(1, testChat.getMessages().size());
        assertTrue(testChat.getMessages().contains(message));
        assertEquals("Hello, how can I help you?", testChat.getMessages().get(0).getMessage());
    }

    @Test
    void given_chat_when_add_multiple_messages_then_all_messages_are_added() {
        // Given
        Message message1 = new Message("Hello!", false);
        Message message2 = new Message("Hi there!", true);
        Message message3 = new Message("How are you?", false);

        // When
        testChat.addMessage(message1);
        testChat.addMessage(message2);
        testChat.addMessage(message3);

        // Then
        assertEquals(3, testChat.getMessages().size());
        assertEquals(message1, testChat.getMessages().get(0));
        assertEquals(message2, testChat.getMessages().get(1));
        assertEquals(message3, testChat.getMessages().get(2));
    }

    @Test
    void given_chat_when_add_ai_and_user_messages_then_messages_maintain_order() {
        // Given
        Message userMessage = new Message("What is Java?", false);
        Message aiMessage = new Message("Java is a programming language.", true);

        // When
        testChat.addMessage(userMessage);
        testChat.addMessage(aiMessage);

        // Then
        assertEquals(2, testChat.getMessages().size());
        assertFalse(testChat.getMessages().get(0).getAi());
        assertTrue(testChat.getMessages().get(1).getAi());
    }

    @Test
    void given_chat_with_messages_when_set_messages_then_messages_are_replaced() {
        // Given
        Message oldMessage = new Message("Old message", false);
        testChat.addMessage(oldMessage);

        List<Message> newMessages = new ArrayList<>();
        newMessages.add(new Message("New message 1", false));
        newMessages.add(new Message("New message 2", true));

        // When
        testChat.setMessages(newMessages);

        // Then
        assertEquals(2, testChat.getMessages().size());
        assertEquals("New message 1", testChat.getMessages().get(0).getMessage());
        assertEquals("New message 2", testChat.getMessages().get(1).getMessage());
        assertFalse(testChat.getMessages().contains(oldMessage));
    }

    @Test
    void given_chat_when_set_empty_messages_then_messages_list_is_empty() {
        // Given
        testChat.addMessage(new Message("Test message", false));
        List<Message> emptyList = new ArrayList<>();

        // When
        testChat.setMessages(emptyList);

        // Then
        assertTrue(testChat.getMessages().isEmpty());
        assertEquals(0, testChat.getMessages().size());
    }

    @Test
    void given_chat_when_add_same_message_multiple_times_then_message_appears_multiple_times() {
        // Given
        Message message = new Message("Repeated message", false);

        // When
        testChat.addMessage(message);
        testChat.addMessage(message);

        // Then
        assertEquals(2, testChat.getMessages().size());
        assertEquals(message, testChat.getMessages().get(0));
        assertEquals(message, testChat.getMessages().get(1));
    }
}
