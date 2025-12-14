package be.ucll.group8.craftmanshipgroep8.journal.service;

import be.ucll.group8.craftmanshipgroep8.journal.controller.Dto.GetJournalDto;
import be.ucll.group8.craftmanshipgroep8.journal.controller.Dto.PostJournalDto;
import be.ucll.group8.craftmanshipgroep8.journal.domain.Journal;
import be.ucll.group8.craftmanshipgroep8.journal.domain.Mood;
import be.ucll.group8.craftmanshipgroep8.journal.repository.JournalRepository;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock
    private JournalRepository journalRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private JournalService journalService;

    private User robin;
    private String testEmail;
    private Journal testJournal1;
    private Journal testJournal2;

    @BeforeEach
    void setUp() {
        testEmail = "ghys.pad@example.com";
        robin = new User("ghys_pad", testEmail, "Password123!");

        testJournal1 = new Journal(
            "My First Journal",
            "Today was a great day!",
            Mood.HAPPY,
            Arrays.asList("personal", "gratitude"),
            LocalDateTime.now(),
            robin
        );

        testJournal2 = new Journal(
            "Work Reflection",
            "Had a productive meeting.",
            Mood.CALM,
            Arrays.asList("work", "productivity"),
            LocalDateTime.now(),
            robin
        );
    }

    @Test
    void given_existing_user_with_journals_when_get_journals_then_return_journal_dtos() {
        // Given
        List<Journal> journals = Arrays.asList(testJournal1, testJournal2);
        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(journalRepository.findAllByUserEmail(testEmail)).thenReturn(journals);

        // When
        List<GetJournalDto> result = journalService.getJournals(testEmail);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("My First Journal", result.get(0).title());
        assertEquals("Work Reflection", result.get(1).title());
        assertEquals(Mood.HAPPY, result.get(0).mood());
        assertEquals(Mood.CALM, result.get(1).mood());
        verify(userService, times(1)).userExistsByEmail(testEmail);
        verify(journalRepository, times(1)).findAllByUserEmail(testEmail);
    }

    @Test
    void given_existing_user_with_no_journals_when_get_journals_then_return_empty_list() {
        // Given
        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(journalRepository.findAllByUserEmail(testEmail)).thenReturn(new ArrayList<>());

        // When
        List<GetJournalDto> result = journalService.getJournals(testEmail);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService, times(1)).userExistsByEmail(testEmail);
        verify(journalRepository, times(1)).findAllByUserEmail(testEmail);
    }

    @Test
    void given_non_existing_user_when_get_journals_then_throw_exception() {
        // Given
        when(userService.userExistsByEmail(testEmail)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> journalService.getJournals(testEmail));
        assertTrue(exception.getMessage().contains("bestaat niet"));
        assertTrue(exception.getMessage().contains(testEmail));
        verify(userService, times(1)).userExistsByEmail(testEmail);
        verify(journalRepository, never()).findAllByUserEmail(anyString());
    }

    @Test
    void given_journals_when_get_journals_then_dto_contains_all_properties() {
        // Given
        List<Journal> journals = Arrays.asList(testJournal1);
        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(journalRepository.findAllByUserEmail(testEmail)).thenReturn(journals);

        // When
        List<GetJournalDto> result = journalService.getJournals(testEmail);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        GetJournalDto dto = result.get(0);
        assertEquals(testJournal1.getId().id(), dto.id());
        assertEquals(testJournal1.getTitle(), dto.title());
        assertEquals(testJournal1.getContent(), dto.content());
        assertEquals(testJournal1.getMood(), dto.mood());
        assertEquals(testJournal1.getTags(), dto.tags());
        assertEquals(testJournal1.getDate(), dto.date());
    }

    @Test
    void given_journals_with_tags_when_get_journals_then_tags_are_included() {
        // Given
        List<Journal> journals = Arrays.asList(testJournal1);
        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(journalRepository.findAllByUserEmail(testEmail)).thenReturn(journals);

        // When
        List<GetJournalDto> result = journalService.getJournals(testEmail);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).tags());
        assertEquals(2, result.get(0).tags().size());
        assertTrue(result.get(0).tags().contains("personal"));
        assertTrue(result.get(0).tags().contains("gratitude"));
    }

    @Test
    void given_valid_post_journal_dto_when_post_journal_then_journal_is_created() {
        // Given
        PostJournalDto postDto = new PostJournalDto(
            "New Journal",
            "Journal content",
            Mood.EXCITED,
            Arrays.asList("new", "test"),
            LocalDateTime.now()
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Journal result = journalService.postJournal(testEmail, postDto);

        // Then
        assertNotNull(result);
        assertEquals(postDto.title(), result.getTitle());
        assertEquals(postDto.content(), result.getContent());
        assertEquals(postDto.mood(), result.getMood());
        assertEquals(postDto.tags(), result.getTags());
        assertEquals(postDto.date(), result.getDate());
        assertEquals(robin, result.getUser());
        verify(userService, times(1)).userExistsByEmail(testEmail);
        verify(userService, times(1)).findUserByEmail(testEmail);
        verify(journalRepository, times(1)).save(any(Journal.class));
    }

    @Test
    void given_non_existing_user_when_post_journal_then_throw_exception() {
        // Given
        PostJournalDto postDto = new PostJournalDto(
            "New Journal",
            "Content",
            Mood.HAPPY,
            Arrays.asList("test"),
            LocalDateTime.now()
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> journalService.postJournal(testEmail, postDto));
        assertTrue(exception.getMessage().contains("bestaat niet"));
        assertTrue(exception.getMessage().contains(testEmail));
        verify(userService, times(1)).userExistsByEmail(testEmail);
        verify(userService, never()).findUserByEmail(anyString());
        verify(journalRepository, never()).save(any(Journal.class));
    }

    @Test
    void given_post_journal_dto_when_post_journal_then_correct_user_is_associated() {
        // Given
        PostJournalDto postDto = new PostJournalDto(
            "User Journal",
            "Content",
            Mood.PEACEFUL,
            null,
            LocalDateTime.now()
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Journal result = journalService.postJournal(testEmail, postDto);

        // Then
        assertNotNull(result.getUser());
        assertEquals(robin.getUserName(), result.getUser().getUserName());
        assertEquals(robin.getEmail(), result.getUser().getEmail());
    }

    @Test
    void given_post_journal_dto_with_null_tags_when_post_journal_then_journal_is_created() {
        // Given
        PostJournalDto postDto = new PostJournalDto(
            "No Tags Journal",
            "Content",
            Mood.CALM,
            null,
            LocalDateTime.now()
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Journal result = journalService.postJournal(testEmail, postDto);

        // Then
        assertNotNull(result);
        assertTrue(result.getTags().isEmpty());
    }

    @Test
    void given_post_journal_dto_with_empty_tags_when_post_journal_then_journal_is_created() {
        // Given
        PostJournalDto postDto = new PostJournalDto(
            "Empty Tags Journal",
            "Content",
            Mood.ANXIOUS,
            new ArrayList<>(),
            LocalDateTime.now()
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Journal result = journalService.postJournal(testEmail, postDto);

        // Then
        assertNotNull(result);
        assertTrue(result.getTags().isEmpty());
    }

    @Test
    void given_post_journal_dto_with_all_mood_types_when_post_journal_then_mood_is_set_correctly() {
        // Given
        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mood[] moods = {Mood.HAPPY, Mood.CALM, Mood.ANXIOUS, Mood.SAD,
            Mood.EXCITED, Mood.STRESSED, Mood.PEACEFUL, Mood.GRATEFUL};

        // When & Then
        for (Mood mood : moods) {
            PostJournalDto postDto = new PostJournalDto(
                "Journal",
                "Content",
                mood,
                null,
                LocalDateTime.now()
            );

            Journal result = journalService.postJournal(testEmail, postDto);
            assertEquals(mood, result.getMood());
        }
    }

    @Test
    void given_multiple_journals_when_get_journals_then_return_all_in_order() {
        // Given
        Journal journal3 = new Journal(
            "Third Journal",
            "More content",
            Mood.GRATEFUL,
            Arrays.asList("reflection"),
            LocalDateTime.now(),
            robin
        );

        List<Journal> journals = Arrays.asList(testJournal1, testJournal2, journal3);
        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(journalRepository.findAllByUserEmail(testEmail)).thenReturn(journals);

        // When
        List<GetJournalDto> result = journalService.getJournals(testEmail);

        // Then
        assertEquals(3, result.size());
        assertEquals("My First Journal", result.get(0).title());
        assertEquals("Work Reflection", result.get(1).title());
        assertEquals("Third Journal", result.get(2).title());
    }

    @Test
    void given_post_journal_dto_when_post_journal_then_repository_save_is_called() {
        // Given
        PostJournalDto postDto = new PostJournalDto(
            "Test",
            "Content",
            Mood.HAPPY,
            null,
            LocalDateTime.now()
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        journalService.postJournal(testEmail, postDto);

        // Then
        ArgumentCaptor<Journal> journalCaptor = ArgumentCaptor.forClass(Journal.class);
        verify(journalRepository).save(journalCaptor.capture());

        Journal savedJournal = journalCaptor.getValue();
        assertEquals(postDto.title(), savedJournal.getTitle());
        assertEquals(postDto.content(), savedJournal.getContent());
        assertEquals(postDto.mood(), savedJournal.getMood());
    }

    @Test
    void given_post_journal_dto_with_long_content_when_post_journal_then_journal_is_created() {
        // Given
        String longContent = "This is a very long journal entry that contains multiple sentences. " +
            "It includes detailed thoughts, reflections, and observations about the day. " +
            "The service should handle this without any issues.";

        PostJournalDto postDto = new PostJournalDto(
            "Long Entry",
            longContent,
            Mood.PEACEFUL,
            null,
            LocalDateTime.now()
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Journal result = journalService.postJournal(testEmail, postDto);

        // Then
        assertEquals(longContent, result.getContent());
    }

    @Test
    void given_post_journal_dto_with_specific_date_when_post_journal_then_date_is_set() {
        // Given
        LocalDateTime specificDate = LocalDateTime.of(2024, 12, 1, 15, 30);
        PostJournalDto postDto = new PostJournalDto(
            "Dated Journal",
            "Content",
            Mood.HAPPY,
            null,
            specificDate
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Journal result = journalService.postJournal(testEmail, postDto);

        // Then
        assertEquals(specificDate, result.getDate());
    }

    @Test
    void given_journals_with_different_dates_when_get_journals_then_dates_are_preserved() {
        // Given
        LocalDateTime date1 = LocalDateTime.of(2024, 12, 1, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2024, 12, 2, 14, 0);

        Journal journal1 = new Journal("J1", "C1", Mood.HAPPY, null, date1, robin);
        Journal journal2 = new Journal("J2", "C2", Mood.CALM, null, date2, robin);

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(journalRepository.findAllByUserEmail(testEmail)).thenReturn(Arrays.asList(journal1, journal2));

        // When
        List<GetJournalDto> result = journalService.getJournals(testEmail);

        // Then
        assertEquals(2, result.size());
        assertEquals(date1, result.get(0).date());
        assertEquals(date2, result.get(1).date());
    }

    @Test
    void given_post_journal_when_save_fails_then_exception_is_propagated() {
        // Given
        PostJournalDto postDto = new PostJournalDto(
            "Test",
            "Content",
            Mood.HAPPY,
            null,
            LocalDateTime.now()
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
            () -> journalService.postJournal(testEmail, postDto));
        verify(journalRepository, times(1)).save(any(Journal.class));
    }

    @Test
    void given_multiple_post_journals_when_called_sequentially_then_all_are_saved() {
        // Given
        PostJournalDto postDto1 = new PostJournalDto("J1", "C1", Mood.HAPPY, null, LocalDateTime.now());
        PostJournalDto postDto2 = new PostJournalDto("J2", "C2", Mood.CALM, null, LocalDateTime.now());

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        journalService.postJournal(testEmail, postDto1);
        journalService.postJournal(testEmail, postDto2);

        // Then
        verify(journalRepository, times(2)).save(any(Journal.class));
        verify(userService, times(2)).userExistsByEmail(testEmail);
        verify(userService, times(2)).findUserByEmail(testEmail);
    }

    @Test
    void given_empty_title_when_post_journal_then_journal_is_created() {
        // Given
        PostJournalDto postDto = new PostJournalDto(
            "",
            "Content",
            Mood.HAPPY,
            null,
            LocalDateTime.now()
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Journal result = journalService.postJournal(testEmail, postDto);

        // Then
        assertNotNull(result);
        assertEquals("", result.getTitle());
    }

    @Test
    void given_empty_content_when_post_journal_then_journal_is_created() {
        // Given
        PostJournalDto postDto = new PostJournalDto(
            "Title",
            "",
            Mood.HAPPY,
            null,
            LocalDateTime.now()
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Journal result = journalService.postJournal(testEmail, postDto);

        // Then
        assertNotNull(result);
        assertEquals("", result.getContent());
    }

    @Test
    void given_journal_with_multiple_tags_when_get_journals_then_all_tags_are_returned() {
        // Given
        Journal journalWithManyTags = new Journal(
            "Tagged Journal",
            "Content",
            Mood.GRATEFUL,
            Arrays.asList("tag1", "tag2", "tag3", "tag4", "tag5"),
            LocalDateTime.now(),
            robin
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(journalRepository.findAllByUserEmail(testEmail)).thenReturn(Arrays.asList(journalWithManyTags));

        // When
        List<GetJournalDto> result = journalService.getJournals(testEmail);

        // Then
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).tags().size());
        assertTrue(result.get(0).tags().contains("tag1"));
        assertTrue(result.get(0).tags().contains("tag5"));
    }

    @Test
    void given_post_journal_dto_with_multiple_tags_when_post_journal_then_all_tags_are_saved() {
        // Given
        List<String> tags = Arrays.asList("work", "personal", "reflection", "gratitude");
        PostJournalDto postDto = new PostJournalDto(
            "Multi-tag Journal",
            "Content",
            Mood.GRATEFUL,
            tags,
            LocalDateTime.now()
        );

        when(userService.userExistsByEmail(testEmail)).thenReturn(true);
        when(userService.findUserByEmail(testEmail)).thenReturn(robin);
        when(journalRepository.save(any(Journal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Journal result = journalService.postJournal(testEmail, postDto);

        // Then
        assertEquals(4, result.getTags().size());
        assertTrue(result.getTags().containsAll(tags));
    }
}
