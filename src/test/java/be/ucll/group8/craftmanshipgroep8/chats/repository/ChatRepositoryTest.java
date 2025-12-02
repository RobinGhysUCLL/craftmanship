package be.ucll.group8.craftmanshipgroep8.chats.repository;

import be.ucll.group8.craftmanshipgroep8.chats.domain.Chat;
import be.ucll.group8.craftmanshipgroep8.chats.domain.ChatId;
import be.ucll.group8.craftmanshipgroep8.chats.domain.Message;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ChatRepositoryTest {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private Chat testChat1;
    private Chat testChat2;

    @BeforeEach
    void setUp() {
        chatRepository.deleteAll();
        userRepository.deleteAll();

        testUser1 = new User("ghys_pad", "ghys.pad@example.com", "Password123!");
        testUser2 = new User("roel_crabbe", "roel.crabbe@example.com", "SecurePass456!");

        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);

        testChat1 = new Chat(testUser1);
        testChat2 = new Chat(testUser2);
    }

    @Test
    void given_valid_chat_when_save_then_chat_is_saved_successfully() {
        // Given

        // When
        Chat savedChat = chatRepository.save(testChat1);

        // Then
        assertNotNull(savedChat);
        assertEquals(testUser1.getId(), savedChat.getUser().getId());
        assertEquals(testUser1.getEmail(), savedChat.getUser().getEmail());
    }

    @Test
    void given_non_existent_id_when_find_by_id_then_return_empty() {
        // Given
        ChatId nonExistentId = new ChatId();

        // When
        Optional<Chat> foundChat = chatRepository.findById(nonExistentId);

        // Then
        assertFalse(foundChat.isPresent());
    }

    @Test
    void given_saved_chat_when_find_by_user_email_then_return_chat() {
        // Given
        chatRepository.save(testChat1);

        // When
        Chat foundChat = chatRepository.findByUserEmail("ghys.pad@example.com");

        // Then
        assertNotNull(foundChat);
        assertEquals(testUser1.getEmail(), foundChat.getUser().getEmail());
        assertEquals(testUser1.getUserName(), foundChat.getUser().getUserName());
    }

    @Test
    void given_non_existent_email_when_find_by_user_email_then_return_null() {
        // Given
        chatRepository.save(testChat1);

        // When
        Chat foundChat = chatRepository.findByUserEmail("nonexistent@example.com");

        // Then
        assertNull(foundChat);
    }

    @Test
    void given_multiple_chats_when_find_by_user_email_then_return_correct_chat() {
        // Given
        chatRepository.save(testChat1);
        chatRepository.save(testChat2);

        // When
        Chat foundChat1 = chatRepository.findByUserEmail("ghys.pad@example.com");
        Chat foundChat2 = chatRepository.findByUserEmail("roel.crabbe@example.com");

        // Then
        assertNotNull(foundChat1);
        assertNotNull(foundChat2);
        assertEquals("ghys_pad", foundChat1.getUser().getUserName());
        assertEquals("roel_crabbe", foundChat2.getUser().getUserName());
    }

    @Test
    void given_chat_with_messages_when_find_by_user_email_then_return_chat_with_messages() {
        // Given
        Message message1 = new Message("What is Java?", false);
        Message message2 = new Message("Java is a programming language.", true);
        testChat1.addMessage(message1);
        testChat1.addMessage(message2);
        chatRepository.save(testChat1);

        // When
        Chat foundChat = chatRepository.findByUserEmail("ghys.pad@example.com");

        // Then
        assertNotNull(foundChat);
        assertEquals(2, foundChat.getMessages().size());
        assertFalse(foundChat.getMessages().get(0).getAi());
        assertTrue(foundChat.getMessages().get(1).getAi());
    }

    @Test
    void given_multiple_chats_when_find_all_then_return_all_chats() {
        // Given
        chatRepository.save(testChat1);
        chatRepository.save(testChat2);

        // When
        List<Chat> allChats = chatRepository.findAll();

        // Then
        assertNotNull(allChats);
        assertEquals(2, allChats.size());
        assertTrue(allChats.stream().anyMatch(c -> c.getUser().getEmail().equals("ghys.pad@example.com")));
        assertTrue(allChats.stream().anyMatch(c -> c.getUser().getEmail().equals("roel.crabbe@example.com")));
    }

    @Test
    void given_empty_repository_when_find_all_then_return_empty_list() {
        // Given

        // When
        List<Chat> allChats = chatRepository.findAll();

        // Then
        assertNotNull(allChats);
        assertTrue(allChats.isEmpty());
    }

    @Test
    void given_multiple_chats_when_count_then_return_correct_count() {
        // Given
        chatRepository.save(testChat1);
        chatRepository.save(testChat2);

        // When
        long count = chatRepository.count();

        // Then
        assertEquals(2, count);
    }

    @Test
    void given_non_existent_chat_when_exists_by_id_then_return_false() {
        // Given
        ChatId nonExistentId = new ChatId();

        // When
        boolean exists = chatRepository.existsById(nonExistentId);

        // Then
        assertFalse(exists);
    }

    @Test
    void given_user_without_chat_when_find_by_user_email_then_return_null() {
        // Given
        User userWithoutChat = new User("no_chat_user", "nochat@example.com", "Pass123!");
        userRepository.save(userWithoutChat);

        // When
        Chat foundChat = chatRepository.findByUserEmail("nochat@example.com");

        // Then
        assertNull(foundChat);
    }

    @Test
    void given_saved_chats_when_delete_all_then_all_chats_are_removed() {
        // Given
        chatRepository.save(testChat1);
        chatRepository.save(testChat2);
        assertEquals(2, chatRepository.count());

        // When
        chatRepository.deleteAll();

        // Then
        assertEquals(0, chatRepository.count());
        assertNull(chatRepository.findByUserEmail("ghys.pad@example.com"));
        assertNull(chatRepository.findByUserEmail("roel.crabbe@example.com"));
    }
}
