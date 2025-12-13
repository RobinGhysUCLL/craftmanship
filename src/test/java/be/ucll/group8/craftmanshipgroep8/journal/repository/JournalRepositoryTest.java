package be.ucll.group8.craftmanshipgroep8.journal.repository;

import be.ucll.group8.craftmanshipgroep8.journal.domain.Journal;
import be.ucll.group8.craftmanshipgroep8.journal.domain.JournalId;
import be.ucll.group8.craftmanshipgroep8.journal.domain.Mood;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class JournalRepositoryTest {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private UserRepository userRepository;

    private User robin;
    private User roel;
    private Journal testJournal1;
    private Journal testJournal2;

    @BeforeEach
    void setUp() {
        journalRepository.deleteAll();
        userRepository.deleteAll();

        robin = new User("ghys_pad", "ghys.pad@example.com", "Password123!");
        roel = new User("roel_crabbe", "roel.crabbe@example.com", "SecurePass456!");

        robin = userRepository.save(robin);
        roel = userRepository.save(roel);

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
    void given_valid_journal_when_save_then_journal_is_saved_successfully() {
        // Given

        // When
        Journal savedJournal = journalRepository.save(testJournal1);

        // Then
        assertNotNull(savedJournal);
        assertNotNull(savedJournal.getId());
        assertEquals(testJournal1.getTitle(), savedJournal.getTitle());
        assertEquals(testJournal1.getContent(), savedJournal.getContent());
        assertEquals(testJournal1.getMood(), savedJournal.getMood());
        assertEquals(robin.getId(), savedJournal.getUser().getId());
    }

    @Test
    void given_saved_journal_when_find_by_id_then_return_journal() {
        // Given
        Journal savedJournal = journalRepository.save(testJournal1);
        JournalId journalId = savedJournal.getId();

        // When
        Optional<Journal> foundJournal = journalRepository.findById(journalId);

        // Then
        assertTrue(foundJournal.isPresent());
        assertEquals(savedJournal.getId(), foundJournal.get().getId());
        assertEquals(savedJournal.getTitle(), foundJournal.get().getTitle());
        assertEquals(savedJournal.getContent(), foundJournal.get().getContent());
    }

    @Test
    void given_non_existent_id_when_find_by_id_then_return_empty() {
        // Given
        JournalId nonExistentId = new JournalId();

        // When
        Optional<Journal> foundJournal = journalRepository.findById(nonExistentId);

        // Then
        assertFalse(foundJournal.isPresent());
    }

    @Test
    void given_saved_journals_when_find_all_by_user_email_then_return_user_journals() {
        // Given
        journalRepository.save(testJournal1);
        journalRepository.save(testJournal2);

        // When
        List<Journal> journals = journalRepository.findAllByUserEmail("ghys.pad@example.com");

        // Then
        assertNotNull(journals);
        assertEquals(2, journals.size());
        assertTrue(journals.stream().allMatch(j -> j.getUser().getEmail().equals("ghys.pad@example.com")));
    }

    @Test
    void given_multiple_users_with_journals_when_find_all_by_user_email_then_return_only_user_journals() {
        // Given
        journalRepository.save(testJournal1);
        journalRepository.save(testJournal2);

        Journal user2Journal = new Journal(
            "User 2 Journal",
            "Content for user 2",
            Mood.EXCITED,
            Arrays.asList("test"),
            LocalDateTime.now(),
            roel
        );
        journalRepository.save(user2Journal);

        // When
        List<Journal> user1Journals = journalRepository.findAllByUserEmail("ghys.pad@example.com");
        List<Journal> user2Journals = journalRepository.findAllByUserEmail("roel.crabbe@example.com");

        // Then
        assertEquals(2, user1Journals.size());
        assertEquals(1, user2Journals.size());
        assertTrue(user1Journals.stream().allMatch(j -> j.getUser().getEmail().equals("ghys.pad@example.com")));
        assertTrue(user2Journals.stream().allMatch(j -> j.getUser().getEmail().equals("roel.crabbe@example.com")));
    }

    @Test
    void given_non_existent_email_when_find_all_by_user_email_then_return_empty_list() {
        // Given
        journalRepository.save(testJournal1);

        // When
        List<Journal> journals = journalRepository.findAllByUserEmail("nonexistent@example.com");

        // Then
        assertNotNull(journals);
        assertTrue(journals.isEmpty());
    }

    @Test
    void given_user_with_no_journals_when_find_all_by_user_email_then_return_empty_list() {
        // Given
        journalRepository.save(testJournal1);

        // When
        List<Journal> journals = journalRepository.findAllByUserEmail("roel.crabbe@example.com");

        // Then
        assertNotNull(journals);
        assertTrue(journals.isEmpty());
    }

    @Test
    void given_journal_with_tags_when_save_then_tags_are_persisted() {
        // Given
        List<String> tags = Arrays.asList("work", "stress", "reflection");
        Journal journal = new Journal(
            "Tagged Journal",
            "Content with tags",
            Mood.ANXIOUS,
            tags,
            LocalDateTime.now(),
            robin
        );

        // When
        Journal savedJournal = journalRepository.save(journal);
        journalRepository.flush();
        Optional<Journal> retrievedJournal = journalRepository.findById(savedJournal.getId());

        // Then
        assertTrue(retrievedJournal.isPresent());
        assertEquals(3, retrievedJournal.get().getTags().size());
        assertTrue(retrievedJournal.get().getTags().contains("work"));
        assertTrue(retrievedJournal.get().getTags().contains("stress"));
        assertTrue(retrievedJournal.get().getTags().contains("reflection"));
    }

    @Test
    void given_journal_with_null_tags_when_save_then_journal_is_saved() {
        // Given
        Journal journal = new Journal(
            "No Tags Journal",
            "Content without tags",
            Mood.PEACEFUL,
            null,
            LocalDateTime.now(),
            robin
        );

        // When
        Journal savedJournal = journalRepository.save(journal);
        journalRepository.flush();
        Optional<Journal> retrievedJournal = journalRepository.findById(savedJournal.getId());

        // Then
        assertTrue(retrievedJournal.isPresent());
        assertTrue(retrievedJournal.get().getTags().isEmpty());
    }

    @Test
    void given_journals_with_different_moods_when_save_then_all_moods_are_persisted() {
        // Given
        Journal happyJournal = new Journal("Happy", "Happy content", Mood.HAPPY, null, LocalDateTime.now(), robin);
        Journal sadJournal = new Journal("Sad", "Sad content", Mood.SAD, null, LocalDateTime.now(), robin);
        Journal anxiousJournal = new Journal("Anxious", "Anxious content", Mood.ANXIOUS, null, LocalDateTime.now(), robin);

        // When
        journalRepository.save(happyJournal);
        journalRepository.save(sadJournal);
        journalRepository.save(anxiousJournal);

        List<Journal> journals = journalRepository.findAllByUserEmail("ghys.pad@example.com");

        // Then
        assertEquals(3, journals.size());
        assertTrue(journals.stream().anyMatch(j -> j.getMood() == Mood.HAPPY));
        assertTrue(journals.stream().anyMatch(j -> j.getMood() == Mood.SAD));
        assertTrue(journals.stream().anyMatch(j -> j.getMood() == Mood.ANXIOUS));
    }

    @Test
    void given_multiple_journals_when_find_all_then_return_all_journals() {
        // Given
        journalRepository.save(testJournal1);
        journalRepository.save(testJournal2);

        Journal user2Journal = new Journal(
            "User 2 Journal",
            "Content",
            Mood.GRATEFUL,
            null,
            LocalDateTime.now(),
            roel
        );
        journalRepository.save(user2Journal);

        // When
        List<Journal> allJournals = journalRepository.findAll();

        // Then
        assertNotNull(allJournals);
        assertEquals(3, allJournals.size());
    }

    @Test
    void given_saved_journal_when_update_then_changes_are_persisted() {
        // Given
        Journal savedJournal = journalRepository.save(testJournal1);
        String newTitle = "Updated Title";
        String newContent = "Updated content";
        Mood newMood = Mood.GRATEFUL;

        // When
        savedJournal.setTitle(newTitle);
        savedJournal.setContent(newContent);
        savedJournal.setMood(newMood);
        Journal updatedJournal = journalRepository.save(savedJournal);
        journalRepository.flush();

        // Then
        Optional<Journal> retrievedJournal = journalRepository.findById(updatedJournal.getId());
        assertTrue(retrievedJournal.isPresent());
        assertEquals(newTitle, retrievedJournal.get().getTitle());
        assertEquals(newContent, retrievedJournal.get().getContent());
        assertEquals(newMood, retrievedJournal.get().getMood());
    }

    @Test
    void given_saved_journal_when_update_tags_then_tags_are_updated() {
        // Given
        Journal savedJournal = journalRepository.save(testJournal1);
        List<String> newTags = Arrays.asList("updated", "new", "tags");

        // When
        savedJournal.setTagsList(newTags);
        journalRepository.save(savedJournal);
        journalRepository.flush();

        // Then
        Optional<Journal> retrievedJournal = journalRepository.findById(savedJournal.getId());
        assertTrue(retrievedJournal.isPresent());
        assertEquals(3, retrievedJournal.get().getTags().size());
        assertTrue(retrievedJournal.get().getTags().containsAll(newTags));
    }

    @Test
    void given_saved_journal_when_delete_then_journal_is_removed() {
        // Given
        Journal savedJournal = journalRepository.save(testJournal1);
        JournalId journalId = savedJournal.getId();

        // When
        journalRepository.deleteById(journalId);
        Optional<Journal> deletedJournal = journalRepository.findById(journalId);

        // Then
        assertFalse(deletedJournal.isPresent());
    }

    @Test
    void given_saved_journal_when_delete_by_entity_then_journal_is_removed() {
        // Given
        Journal savedJournal = journalRepository.save(testJournal1);
        JournalId journalId = savedJournal.getId();

        // When
        journalRepository.delete(savedJournal);
        Optional<Journal> deletedJournal = journalRepository.findById(journalId);

        // Then
        assertFalse(deletedJournal.isPresent());
    }

    @Test
    void given_user_with_multiple_journals_when_delete_one_then_others_remain() {
        // Given
        Journal journal1 = journalRepository.save(testJournal1);
        journalRepository.save(testJournal2);

        // When
        journalRepository.delete(journal1);
        List<Journal> remainingJournals = journalRepository.findAllByUserEmail("ghys.pad@example.com");

        // Then
        assertEquals(1, remainingJournals.size());
        assertEquals(testJournal2.getTitle(), remainingJournals.get(0).getTitle());
    }

    @Test
    void given_empty_repository_when_find_all_then_return_empty_list() {
        // Given
        // Repository is empty

        // When
        List<Journal> allJournals = journalRepository.findAll();

        // Then
        assertNotNull(allJournals);
        assertTrue(allJournals.isEmpty());
    }

    @Test
    void given_multiple_journals_when_count_then_return_correct_count() {
        // Given
        journalRepository.save(testJournal1);
        journalRepository.save(testJournal2);

        // When
        long count = journalRepository.count();

        // Then
        assertEquals(2, count);
    }

    @Test
    void given_saved_journal_when_exists_by_id_then_return_true() {
        // Given
        Journal savedJournal = journalRepository.save(testJournal1);
        JournalId journalId = savedJournal.getId();

        // When
        boolean exists = journalRepository.existsById(journalId);

        // Then
        assertTrue(exists);
    }

    @Test
    void given_non_existent_journal_when_exists_by_id_then_return_false() {
        // Given
        JournalId nonExistentId = new JournalId();

        // When
        boolean exists = journalRepository.existsById(nonExistentId);

        // Then
        assertFalse(exists);
    }

    @Test
    void given_journals_with_dates_when_save_then_dates_are_persisted() {
        // Given
        LocalDateTime date1 = LocalDateTime.of(2024, 12, 1, 10, 30);
        LocalDateTime date2 = LocalDateTime.of(2024, 12, 2, 14, 45);

        Journal journal1 = new Journal("Journal 1", "Content 1", Mood.HAPPY, null, date1, robin);
        Journal journal2 = new Journal("Journal 2", "Content 2", Mood.CALM, null, date2, robin);

        // When
        journalRepository.save(journal1);
        journalRepository.save(journal2);

        List<Journal> journals = journalRepository.findAllByUserEmail("ghys.pad@example.com");

        // Then
        assertEquals(2, journals.size());
        assertTrue(journals.stream().anyMatch(j -> j.getDate().equals(date1)));
        assertTrue(journals.stream().anyMatch(j -> j.getDate().equals(date2)));
    }

    @Test
    void given_journal_with_long_content_when_save_then_content_is_stored_completely() {
        // Given
        String longContent = "This is a very long journal entry that contains multiple sentences. " +
            "It spans several lines and includes detailed thoughts and reflections. " +
            "The repository should be able to handle storing this entire content without truncation.";

        Journal journal = new Journal(
            "Long Entry",
            longContent,
            Mood.PEACEFUL,
            null,
            LocalDateTime.now(),
            robin
        );

        // When
        Journal savedJournal = journalRepository.save(journal);
        journalRepository.flush();
        Optional<Journal> retrievedJournal = journalRepository.findById(savedJournal.getId());

        // Then
        assertTrue(retrievedJournal.isPresent());
        assertEquals(longContent, retrievedJournal.get().getContent());
    }

    @Test
    void given_multiple_users_when_find_all_by_user_email_then_results_are_isolated() {
        // Given
        journalRepository.save(testJournal1);

        Journal user2Journal1 = new Journal("Journal 1", "Content 1", Mood.HAPPY, null, LocalDateTime.now(), roel);
        Journal user2Journal2 = new Journal("Journal 2", "Content 2", Mood.SAD, null, LocalDateTime.now(), roel);
        journalRepository.save(user2Journal1);
        journalRepository.save(user2Journal2);

        // When
        List<Journal> user1Journals = journalRepository.findAllByUserEmail("ghys.pad@example.com");
        List<Journal> user2Journals = journalRepository.findAllByUserEmail("roel.crabbe@example.com");

        // Then
        assertEquals(1, user1Journals.size());
        assertEquals(2, user2Journals.size());
        assertNotEquals(user1Journals.get(0).getUser().getId(), user2Journals.get(0).getUser().getId());
    }

    @Test
    void given_journal_when_save_multiple_times_then_updates_existing_entity() {
        // Given
        Journal savedJournal = journalRepository.save(testJournal1);
        JournalId originalId = savedJournal.getId();

        // When
        savedJournal.setTitle("Updated Title 1");
        Journal updated1 = journalRepository.save(savedJournal);

        updated1.setTitle("Updated Title 2");
        Journal updated2 = journalRepository.save(updated1);

        // Then
        assertEquals(originalId, updated2.getId());
        assertEquals("Updated Title 2", updated2.getTitle());
        assertEquals(1, journalRepository.count());
    }

    @Test
    void given_saved_journals_when_delete_all_then_all_journals_are_removed() {
        // Given
        journalRepository.save(testJournal1);
        journalRepository.save(testJournal2);
        assertEquals(2, journalRepository.count());

        // When
        journalRepository.deleteAll();

        // Then
        assertEquals(0, journalRepository.count());
        assertTrue(journalRepository.findAllByUserEmail("ghys.pad@example.com").isEmpty());
    }

    @Test
    void given_journal_with_all_mood_types_when_save_then_all_are_persisted_correctly() {
        // Given & When
        journalRepository.save(new Journal("1", "C", Mood.HAPPY, null, LocalDateTime.now(), robin));
        journalRepository.save(new Journal("2", "C", Mood.CALM, null, LocalDateTime.now(), robin));
        journalRepository.save(new Journal("3", "C", Mood.ANXIOUS, null, LocalDateTime.now(), robin));
        journalRepository.save(new Journal("4", "C", Mood.SAD, null, LocalDateTime.now(), robin));
        journalRepository.save(new Journal("5", "C", Mood.EXCITED, null, LocalDateTime.now(), robin));
        journalRepository.save(new Journal("6", "C", Mood.STRESSED, null, LocalDateTime.now(), robin));
        journalRepository.save(new Journal("7", "C", Mood.PEACEFUL, null, LocalDateTime.now(), robin));
        journalRepository.save(new Journal("8", "C", Mood.GRATEFUL, null, LocalDateTime.now(), robin));

        // Then
        List<Journal> journals = journalRepository.findAllByUserEmail("ghys.pad@example.com");
        assertEquals(8, journals.size());
        assertEquals(8, journals.stream().map(Journal::getMood).distinct().count());
    }

    @Test
    void given_journal_with_empty_title_when_save_then_journal_is_saved() {
        // Given
        Journal journal = new Journal("", "Content", Mood.HAPPY, null, LocalDateTime.now(), robin);

        // When
        Journal savedJournal = journalRepository.save(journal);
        Optional<Journal> retrievedJournal = journalRepository.findById(savedJournal.getId());

        // Then
        assertTrue(retrievedJournal.isPresent());
        assertEquals("", retrievedJournal.get().getTitle());
    }

    @Test
    void given_journal_with_special_characters_when_save_then_characters_are_preserved() {
        // Given
        String specialTitle = "Journal @#$% with special chars!";
        String specialContent = "Content with \"quotes\" and 'apostrophes' & symbols";

        Journal journal = new Journal(
            specialTitle,
            specialContent,
            Mood.HAPPY,
            Arrays.asList("tag-1", "tag_2"),
            LocalDateTime.now(),
            robin
        );

        // When
        Journal savedJournal = journalRepository.save(journal);
        journalRepository.flush();
        Optional<Journal> retrievedJournal = journalRepository.findById(savedJournal.getId());

        // Then
        assertTrue(retrievedJournal.isPresent());
        assertEquals(specialTitle, retrievedJournal.get().getTitle());
        assertEquals(specialContent, retrievedJournal.get().getContent());
    }
}
