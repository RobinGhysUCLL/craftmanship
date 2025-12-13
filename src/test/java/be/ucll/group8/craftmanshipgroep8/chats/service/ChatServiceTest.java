package be.ucll.group8.craftmanshipgroep8.chats.service;

import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.GetChat;
import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.PostMessage;
import be.ucll.group8.craftmanshipgroep8.chats.domain.Chat;
import be.ucll.group8.craftmanshipgroep8.chats.domain.Message;
import be.ucll.group8.craftmanshipgroep8.chats.repository.ChatRepository;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserService userService;

    @Mock
    private AiService aiService;

    @InjectMocks
    private ChatService chatService;

    private User robin;
    private Chat testChat;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testEmail = "ghys.pad@example.com";
        robin = new User("ghys_pad", testEmail, "Password123!");
        testChat = new Chat(robin);
        lenient().when(aiService.generateReply(anyList(), anyString())).thenReturn("AI generated response");
    }

    @Test
    void given_existing_user_with_messages_when_get_messages_then_return_messages() {
        // Given
        Message message1 = new Message("Hello!", false);
        Message message2 = new Message("Hi there!", true);
        testChat.addMessage(message1);
        testChat.addMessage(message2);

        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));

        // When
        GetChat result = chatService.getMessages(testEmail);

        // Then
        assertNotNull(result);
        assertNotNull(result.messages());
        assertEquals(2, result.messages().size());
        assertEquals("Hello!", result.messages().get(0).message());
        assertEquals("Hi there!", result.messages().get(1).message());
        assertFalse(result.messages().get(0).ai());
        assertTrue(result.messages().get(1).ai());
        verify(userService, times(1)).findUserByEmail(testEmail);
        verify(chatRepository, times(1)).findByUserEmail(testEmail);
    }

    @Test
    void given_existing_user_with_no_chat_when_get_messages_then_return_empty_list() {
        // Given
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.empty());

        // When
        GetChat result = chatService.getMessages(testEmail);

        // Then
        assertNotNull(result);
        assertNotNull(result.messages());
        assertTrue(result.messages().isEmpty());
        verify(userService, times(1)).findUserByEmail(testEmail);
        verify(chatRepository, times(1)).findByUserEmail(testEmail);
    }

    @Test
    void given_existing_user_with_chat_but_no_messages_when_get_messages_then_return_empty_list() {
        // Given
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));

        // When
        GetChat result = chatService.getMessages(testEmail);

        // Then
        assertNotNull(result);
        assertNotNull(result.messages());
        assertTrue(result.messages().isEmpty());
        verify(userService, times(1)).findUserByEmail(testEmail);
        verify(chatRepository, times(1)).findByUserEmail(testEmail);
    }

    @Test
    void given_non_existing_user_when_get_messages_then_throw_exception() {
        // Given
        when(userService.findUserByEmail(testEmail))
            .thenThrow(new RuntimeException("Gebruiker met email '" + testEmail + "' bestaat niet."));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> chatService.getMessages(testEmail));
        assertTrue(exception.getMessage().contains("bestaat niet"));
        assertTrue(exception.getMessage().contains(testEmail));
        verify(userService, times(1)).findUserByEmail(testEmail);
        verify(chatRepository, never()).findByUserEmail(anyString());
    }

    @Test
    void given_existing_user_without_chat_when_post_message_then_create_new_chat_and_add_messages() {
        // Given
        String messageText = "What is Java?";
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.empty());
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PostMessage result = chatService.postMessage(testEmail, messageText);

        // Then
        assertNotNull(result);
        assertNotNull(result.message());
        assertTrue(result.ai());

        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository, times(1)).save(chatCaptor.capture());

        Chat savedChat = chatCaptor.getValue();
        assertEquals(2, savedChat.getMessages().size());
        assertEquals(messageText, savedChat.getMessages().get(0).getMessage());
        assertFalse(savedChat.getMessages().get(0).getAi());
        assertTrue(savedChat.getMessages().get(1).getAi());

        verify(userService, times(1)).findUserByEmail(testEmail);
        verify(chatRepository, times(1)).findByUserEmail(testEmail);
    }

    @Test
    void given_existing_user_with_existing_chat_when_post_message_then_add_messages_to_existing_chat() {
        // Given
        String messageText = "Tell me about java";
        Message existingMessage = new Message("Previous message", false);
        testChat.addMessage(existingMessage);

        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PostMessage result = chatService.postMessage(testEmail, messageText);

        // Then
        assertNotNull(result);
        assertTrue(result.ai());

        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository, times(1)).save(chatCaptor.capture());

        Chat savedChat = chatCaptor.getValue();
        assertEquals(3, savedChat.getMessages().size());
        assertEquals("Previous message", savedChat.getMessages().get(0).getMessage());
        assertEquals(messageText, savedChat.getMessages().get(1).getMessage());
        assertFalse(savedChat.getMessages().get(1).getAi());
        assertTrue(savedChat.getMessages().get(2).getAi());

        verify(userService, times(1)).findUserByEmail(testEmail);
        verify(chatRepository, times(1)).findByUserEmail(testEmail);
    }

    @Test
    void given_non_existing_user_when_post_message_then_throw_exception() {
        // Given
        String messageText = "Hello";
        when(userService.findUserByEmail(testEmail))
            .thenThrow(new RuntimeException("Gebruiker met email '" + testEmail + "' bestaat niet."));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> chatService.postMessage(testEmail, messageText));
        assertTrue(exception.getMessage().contains("bestaat niet"));
        assertTrue(exception.getMessage().contains(testEmail));

        verify(userService, times(1)).findUserByEmail(testEmail);
        verify(chatRepository, never()).findByUserEmail(anyString());
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void given_valid_email_and_message_when_post_message_then_return_ai_response() {
        // Given
        String messageText = "Test question";
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PostMessage result = chatService.postMessage(testEmail, messageText);

        // Then
        assertNotNull(result);
        assertNotNull(result.id());
        assertNotNull(result.message());
        assertNotNull(result.timestamp());
        assertTrue(result.ai());
    }

    @Test
    void given_empty_message_when_post_message_then_still_create_messages() {
        // Given
        String emptyMessage = "";
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PostMessage result = chatService.postMessage(testEmail, emptyMessage);

        // Then
        assertNotNull(result);

        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository, times(1)).save(chatCaptor.capture());

        Chat savedChat = chatCaptor.getValue();
        assertEquals(2, savedChat.getMessages().size());
        assertEquals("", savedChat.getMessages().get(0).getMessage());
    }

    @Test
    void given_long_message_when_post_message_then_message_is_saved() {
        // Given
        String longMessage = "This is a very long message that contains multiple sentences. " +
            "It should be stored completely without any truncation.";
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PostMessage result = chatService.postMessage(testEmail, longMessage);

        // Then
        assertNotNull(result);

        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository, times(1)).save(chatCaptor.capture());

        Chat savedChat = chatCaptor.getValue();
        assertEquals(longMessage, savedChat.getMessages().get(0).getMessage());
    }

    @Test
    void given_multiple_messages_when_get_messages_then_return_all_messages_in_order() {
        // Given
        Message message1 = new Message("First", false);
        Message message2 = new Message("Second", true);
        Message message3 = new Message("Third", false);
        Message message4 = new Message("Fourth", true);
        testChat.addMessage(message1);
        testChat.addMessage(message2);
        testChat.addMessage(message3);
        testChat.addMessage(message4);

        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));

        // When
        GetChat result = chatService.getMessages(testEmail);

        // Then
        assertNotNull(result);
        assertEquals(4, result.messages().size());
        assertEquals("First", result.messages().get(0).message());
        assertEquals("Second", result.messages().get(1).message());
        assertEquals("Third", result.messages().get(2).message());
        assertEquals("Fourth", result.messages().get(3).message());
    }

    @Test
    void given_chat_with_null_messages_list_when_get_messages_then_return_empty_list() {
        // Given
        testChat.setMessages(null);

        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));

        // When
        GetChat result = chatService.getMessages(testEmail);

        // Then
        assertNotNull(result);
        assertNotNull(result.messages());
        assertTrue(result.messages().isEmpty());
    }

    @Test
    void given_messages_when_get_messages_then_dto_contains_all_message_properties() {
        // Given
        Message message = new Message("Test message", true);
        testChat.addMessage(message);

        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));

        // When
        GetChat result = chatService.getMessages(testEmail);

        // Then
        assertNotNull(result);
        assertEquals(1, result.messages().size());

        PostMessage dtoMessage = result.messages().get(0);
        assertEquals(message.getId().id(), dtoMessage.id());
        assertEquals(message.getMessage(), dtoMessage.message());
        assertEquals(message.getAi(), dtoMessage.ai());
        assertEquals(message.getTimestamp(), dtoMessage.timestamp());
    }

    @Test
    void given_post_message_when_save_fails_then_exception_is_propagated() {
        // Given
        String messageText = "Test";
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));
        when(aiService.generateReply(anyList(), anyString()))
            .thenThrow(new RuntimeException("AI service error"));

        // When & Then
        assertThrows(RuntimeException.class,
            () -> chatService.postMessage(testEmail, messageText));

        verify(aiService, times(1)).generateReply(anyList(), anyString());
    }

    @Test
    void given_multiple_post_messages_when_called_sequentially_then_all_messages_are_saved() {
        // Given
        String message1 = "First question";
        String message2 = "Second question";

        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        chatService.postMessage(testEmail, message1);
        chatService.postMessage(testEmail, message2);

        // Then
        verify(chatRepository, times(2)).save(any(Chat.class));
        verify(userService, times(2)).findUserByEmail(testEmail);
        verify(chatRepository, times(2)).findByUserEmail(testEmail);
    }

    @Test
    void given_user_and_message_when_post_message_then_both_user_and_ai_messages_are_added() {
        // Given
        String messageText = "User question";
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        chatService.postMessage(testEmail, messageText);

        // Then
        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository).save(chatCaptor.capture());

        Chat savedChat = chatCaptor.getValue();
        assertEquals(2, savedChat.getMessages().size());

        Message userMessage = savedChat.getMessages().get(0);
        Message aiMessage = savedChat.getMessages().get(1);

        assertEquals(messageText, userMessage.getMessage());
        assertFalse(userMessage.getAi());

        assertTrue(aiMessage.getAi());
        assertNotNull(aiMessage.getMessage());
    }

    @Test
    void given_existing_chat_when_post_message_then_chat_user_remains_unchanged() {
        // Given
        String messageText = "Test message";
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(chatRepository.findByUserEmail(testEmail)).thenReturn(Optional.of(testChat));
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User originalUser = testChat.getUser();

        // When
        chatService.postMessage(testEmail, messageText);

        // Then
        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository).save(chatCaptor.capture());

        assertEquals(originalUser, chatCaptor.getValue().getUser());
    }
}
