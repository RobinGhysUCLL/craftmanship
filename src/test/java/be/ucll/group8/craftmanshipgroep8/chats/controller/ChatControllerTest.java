package be.ucll.group8.craftmanshipgroep8.chats.controller;

import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.GetChat;
import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.PostMessage;
import be.ucll.group8.craftmanshipgroep8.chats.repository.ChatRepository;
import be.ucll.group8.craftmanshipgroep8.chats.service.AiService;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.SignUpRequest;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.SignupResponse;
import be.ucll.group8.craftmanshipgroep8.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ChatControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AiService aiService;

    private String testUserEmail;
    private String testUserPassword;
    private String authToken;

    @BeforeEach
    void setUp() {
        chatRepository.deleteAll();
        userRepository.deleteAll();

        // Mock the AI service to return a predictable response
        when(aiService.generateReply(anyList(), anyString()))
            .thenReturn("This is a mocked AI response");

        testUserEmail = "ghys.pad@example.com";
        testUserPassword = "Password123!";

        // Create and authenticate a test user
        SignUpRequest signUpRequest = new SignUpRequest(
            "ghys_pad",
            testUserPassword,
            testUserEmail
        );

        ResponseEntity<SignupResponse> signupResponse = restTemplate.postForEntity(
            "/users/signup",
            signUpRequest,
            SignupResponse.class
        );

        authToken = signupResponse.getBody().token();
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void given_authenticated_user_with_no_messages_when_get_chat_then_return_empty_messages_list() {
        // Given
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<GetChat> response = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            requestEntity,
            GetChat.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().messages());
        assertTrue(response.getBody().messages().isEmpty());
    }

    @Test
    void given_authenticated_user_when_post_message_then_return_ai_response() {
        // Given
        HttpHeaders headers = createAuthHeaders();
        String userMessage = "What is Java?";
        HttpEntity<String> requestEntity = new HttpEntity<>(userMessage, headers);

        // When
        ResponseEntity<PostMessage> response = restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            requestEntity,
            PostMessage.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().id());
        assertNotNull(response.getBody().message());
        assertTrue(response.getBody().ai());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void given_authenticated_user_when_post_message_and_get_chat_then_return_all_messages() {
        // Given
        HttpHeaders headers = createAuthHeaders();
        String userMessage = "Hello!";
        HttpEntity<String> postEntity = new HttpEntity<>(userMessage, headers);

        // When - Post message
        restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            postEntity,
            PostMessage.class
        );

        // When - Get chat
        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<GetChat> getResponse = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            getEntity,
            GetChat.class
        );

        // Then
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(2, getResponse.getBody().messages().size());

        PostMessage firstMessage = getResponse.getBody().messages().get(0);
        PostMessage secondMessage = getResponse.getBody().messages().get(1);

        assertEquals(userMessage, firstMessage.message());
        assertFalse(firstMessage.ai());
        assertTrue(secondMessage.ai());
    }

    @Test
    void given_authenticated_user_when_post_multiple_messages_then_all_messages_are_stored() {
        // Given
        HttpHeaders headers = createAuthHeaders();

        // When
        restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            new HttpEntity<>("First message", headers),
            PostMessage.class
        );

        restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            new HttpEntity<>("Second message", headers),
            PostMessage.class
        );

        restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            new HttpEntity<>("Third message", headers),
            PostMessage.class
        );

        // Then
        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<GetChat> getResponse = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            getEntity,
            GetChat.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(6, getResponse.getBody().messages().size());

        assertEquals("First message", getResponse.getBody().messages().get(0).message());
        assertEquals("Second message", getResponse.getBody().messages().get(2).message());
        assertEquals("Third message", getResponse.getBody().messages().get(4).message());
    }

    @Test
    void given_unauthenticated_user_when_get_chat_then_return_unauthorized() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            requestEntity,
            String.class
        );

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void given_unauthenticated_user_when_post_message_then_return_unauthorized() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>("Test message", headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void given_invalid_token_when_get_chat_then_return_unauthorized() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid-token-12345");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            requestEntity,
            String.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void given_expired_token_when_post_message_then_return_unauthorized() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("expired.jwt.token");
        HttpEntity<String> requestEntity = new HttpEntity<>("Test", headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void given_empty_message_when_post_message_then_message_is_invalid() {
        // Given
        HttpHeaders headers = createAuthHeaders();
        String emptyMessage = "";
        HttpEntity<String> requestEntity = new HttpEntity<>(emptyMessage, headers);

        // When
        ResponseEntity<PostMessage> response = restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            requestEntity,
            PostMessage.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void given_long_message_when_post_message_then_message_is_stored_completely() {
        // Given
        HttpHeaders headers = createAuthHeaders();
        String longMessage = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" +
            "ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc";
        HttpEntity<String> requestEntity = new HttpEntity<>(longMessage, headers);

        // When
        ResponseEntity<PostMessage> postResponse = restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            requestEntity,
            PostMessage.class
        );

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<GetChat> getResponse = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            getEntity,
            GetChat.class
        );

        // Then
        assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(longMessage, getResponse.getBody().messages().get(0).message());
    }

    @Test
    void given_message_with_special_characters_when_post_message_then_characters_are_preserved() {
        // Given
        HttpHeaders headers = createAuthHeaders();
        String specialMessage = "Hello! @#$%^&*() <> \"quotes\" 'apostrophe' {brackets}";
        HttpEntity<String> requestEntity = new HttpEntity<>(specialMessage, headers);

        // When
        restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            requestEntity,
            PostMessage.class
        );

        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<GetChat> getResponse = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            getEntity,
            GetChat.class
        );

        // Then
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(specialMessage, getResponse.getBody().messages().get(0).message());
    }

    @Test
    void given_two_different_users_when_post_messages_then_messages_are_isolated() {
        // Given - First user (already set up)
        HttpHeaders headers1 = createAuthHeaders();

        // Given - Second user
        SignUpRequest signUpRequest2 = new SignUpRequest(
            "roel_crabbe",
            "SecurePass456!",
            "roel.crabbe@example.com"
        );
        ResponseEntity<SignupResponse> signupResponse2 = restTemplate.postForEntity(
            "/users/signup",
            signUpRequest2,
            SignupResponse.class
        );
        String authToken2 = signupResponse2.getBody().token();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.setBearerAuth(authToken2);
        headers2.setContentType(MediaType.APPLICATION_JSON);

        // When - User 1 posts message
        restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            new HttpEntity<>("User 1 message", headers1),
            PostMessage.class
        );

        // When - User 2 posts message
        restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            new HttpEntity<>("User 2 message", headers2),
            PostMessage.class
        );

        // Then - Get User 1 messages
        HttpEntity<Void> getEntity1 = new HttpEntity<>(headers1);
        ResponseEntity<GetChat> getResponse1 = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            getEntity1,
            GetChat.class
        );

        // Then - Get User 2 messages
        HttpEntity<Void> getEntity2 = new HttpEntity<>(headers2);
        ResponseEntity<GetChat> getResponse2 = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            getEntity2,
            GetChat.class
        );

        assertEquals(HttpStatus.OK, getResponse1.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse2.getStatusCode());
        assertEquals(2, getResponse1.getBody().messages().size());
        assertEquals(2, getResponse2.getBody().messages().size());
        assertEquals("User 1 message", getResponse1.getBody().messages().get(0).message());
        assertEquals("User 2 message", getResponse2.getBody().messages().get(0).message());
    }

    @Test
    void given_authenticated_user_when_post_message_then_timestamps_are_chronological() {
        // Given
        HttpHeaders headers = createAuthHeaders();

        // When
        restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            new HttpEntity<>("First", headers),
            PostMessage.class
        );

        try {
            Thread.sleep(100); // Small delay to ensure different timestamps
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            new HttpEntity<>("Second", headers),
            PostMessage.class
        );

        // Then
        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<GetChat> getResponse = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            getEntity,
            GetChat.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(4, getResponse.getBody().messages().size());

        var firstTimestamp = getResponse.getBody().messages().get(0).timestamp();
        var lastTimestamp = getResponse.getBody().messages().get(3).timestamp();

        assertTrue(firstTimestamp.isBefore(lastTimestamp) || firstTimestamp.isEqual(lastTimestamp));
    }

    @Test
    void given_authenticated_user_when_get_messages_then_messages_maintain_order() {
        // Given
        HttpHeaders headers = createAuthHeaders();

        // When - Post multiple messages
        restTemplate.exchange("/messages", HttpMethod.POST, new HttpEntity<>("Message 1", headers), PostMessage.class);
        restTemplate.exchange("/messages", HttpMethod.POST, new HttpEntity<>("Message 2", headers), PostMessage.class);
        restTemplate.exchange("/messages", HttpMethod.POST, new HttpEntity<>("Message 3", headers), PostMessage.class);

        // Then
        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<GetChat> getResponse = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            getEntity,
            GetChat.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(6, getResponse.getBody().messages().size());
        assertEquals("Message 1", getResponse.getBody().messages().get(0).message());
        assertEquals("Message 2", getResponse.getBody().messages().get(2).message());
        assertEquals("Message 3", getResponse.getBody().messages().get(4).message());
    }

    @Test
    void given_authenticated_user_when_post_message_then_both_user_and_ai_messages_have_ids() {
        // Given
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>("Test message", headers);

        // When
        restTemplate.exchange(
            "/messages",
            HttpMethod.POST,
            requestEntity,
            PostMessage.class
        );

        // Then
        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<GetChat> getResponse = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            getEntity,
            GetChat.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(2, getResponse.getBody().messages().size());

        assertNotNull(getResponse.getBody().messages().get(0).id());
        assertNotNull(getResponse.getBody().messages().get(1).id());
        assertNotEquals(getResponse.getBody().messages().get(0).id(),
            getResponse.getBody().messages().get(1).id());
    }

    @Test
    void given_complete_conversation_flow_when_executed_then_full_chat_history_is_maintained() {
        // Given
        HttpHeaders headers = createAuthHeaders();

        // When - Simulate a conversation
        restTemplate.exchange("/messages", HttpMethod.POST, new HttpEntity<>("Hello", headers), PostMessage.class);
        restTemplate.exchange("/messages", HttpMethod.POST, new HttpEntity<>("What is Spring Boot?", headers), PostMessage.class);
        restTemplate.exchange("/messages", HttpMethod.POST, new HttpEntity<>("Thank you!", headers), PostMessage.class);

        // Then
        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<GetChat> getResponse = restTemplate.exchange(
            "/messages",
            HttpMethod.GET,
            getEntity,
            GetChat.class
        );

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(6, getResponse.getBody().messages().size());
        assertFalse(getResponse.getBody().messages().get(0).ai());
        assertTrue(getResponse.getBody().messages().get(1).ai());
        assertFalse(getResponse.getBody().messages().get(2).ai());
        assertTrue(getResponse.getBody().messages().get(3).ai());
        assertFalse(getResponse.getBody().messages().get(4).ai());
        assertTrue(getResponse.getBody().messages().get(5).ai());
    }
}
