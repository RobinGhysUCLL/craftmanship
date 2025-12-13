package be.ucll.group8.craftmanshipgroep8.journal.controller;

import be.ucll.group8.craftmanshipgroep8.chats.service.AiService;  // Add this import
import be.ucll.group8.craftmanshipgroep8.journal.controller.Dto.GetJournalDto;
import be.ucll.group8.craftmanshipgroep8.journal.controller.Dto.PostJournalDto;
import be.ucll.group8.craftmanshipgroep8.journal.domain.Journal;
import be.ucll.group8.craftmanshipgroep8.journal.domain.Mood;
import be.ucll.group8.craftmanshipgroep8.journal.repository.JournalRepository;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.SignUpRequest;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.SignupResponse;
import be.ucll.group8.craftmanshipgroep8.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class JournalControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AiService aiService;

    private String testUserEmail;
    private String testUserPassword;
    private String authToken;

    @BeforeEach
    void setUp() {
        journalRepository.deleteAll();
        userRepository.deleteAll();
        when(aiService.generateReply(anyList(), anyString()))
            .thenReturn("Mocked AI response");

        testUserEmail = "ghys.pad@example.com";
        testUserPassword = "Password123!";

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
    void given_authenticated_user_with_no_journals_when_get_journals_then_return_empty_list() {
        // Given
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<List<GetJournalDto>> response = restTemplate.exchange(
            "/journals",
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<GetJournalDto>>() {}
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void given_unauthenticated_user_when_get_journals_then_return_unauthorized() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            "/journals",
            HttpMethod.GET,
            requestEntity,
            String.class
        );

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void given_unauthenticated_user_when_post_journal_then_return_unauthorized() {
        // Given
        HttpHeaders headers = new HttpHeaders();
        PostJournalDto journalDto = new PostJournalDto(
            "Test",
            "Content",
            Mood.HAPPY,
            null,
            LocalDateTime.now()
        );
        HttpEntity<PostJournalDto> requestEntity = new HttpEntity<>(journalDto, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            "/journals",
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
