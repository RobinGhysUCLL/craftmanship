package be.ucll.group8.craftmanshipgroep8.journal.domain;

import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JournalTest {

    private User robin;
    private String testTitle;
    private String testContent;
    private Mood testMood;
    private List<String> testTags;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        robin = new User("ghys_pad", "ghys.pad@example.com", "Password123!");
        testTitle = "My First Journal Entry";
        testContent = "Today was a great day!";
        testMood = Mood.HAPPY;
        testTags = Arrays.asList("personal", "reflection", "gratitude");
        testDate = LocalDateTime.now();
    }

    @Test
    void given_valid_parameters_when_create_journal_then_journal_is_created_successfully() {
        // Given

        // When
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);

        // Then
        assertNotNull(journal);
        assertNotNull(journal.getId());
        assertEquals(testTitle, journal.getTitle());
        assertEquals(testContent, journal.getContent());
        assertEquals(testMood, journal.getMood());
        assertEquals(testTags, journal.getTags());
        assertEquals(testDate, journal.getDate());
        assertEquals(robin, journal.getUser());
    }

    @Test
    void given_journal_when_get_title_then_return_correct_title() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);

        // When
        String title = journal.getTitle();

        // Then
        assertEquals(testTitle, title);
    }

    @Test
    void given_journal_when_set_title_then_title_is_updated() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);
        String newTitle = "Updated Journal Entry";

        // When
        journal.setTitle(newTitle);

        // Then
        assertEquals(newTitle, journal.getTitle());
    }

    @Test
    void given_journal_when_get_content_then_return_correct_content() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);

        // When
        String content = journal.getContent();

        // Then
        assertEquals(testContent, content);
    }

    @Test
    void given_journal_when_set_content_then_content_is_updated() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);
        String newContent = "Updated content with more details.";

        // When
        journal.setContent(newContent);

        // Then
        assertEquals(newContent, journal.getContent());
    }

    @Test
    void given_journal_when_get_mood_then_return_correct_mood() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);

        // When
        Mood mood = journal.getMood();

        // Then
        assertEquals(Mood.HAPPY, mood);
    }

    @Test
    void given_journal_when_set_mood_then_mood_is_updated() {
        // Given
        Journal journal = new Journal(testTitle, testContent, Mood.HAPPY, testTags, testDate, robin);

        // When
        journal.setMood(Mood.CALM);

        // Then
        assertEquals(Mood.CALM, journal.getMood());
    }

    @Test
    void given_journal_with_tags_when_get_tags_then_return_tags_list() {
        // Given
        List<String> tags = Arrays.asList("work", "stress", "productivity");
        Journal journal = new Journal(testTitle, testContent, testMood, tags, testDate, robin);

        // When
        List<String> retrievedTags = journal.getTags();

        // Then
        assertNotNull(retrievedTags);
        assertEquals(3, retrievedTags.size());
        assertTrue(retrievedTags.contains("work"));
        assertTrue(retrievedTags.contains("stress"));
        assertTrue(retrievedTags.contains("productivity"));
    }

    @Test
    void given_journal_with_null_tags_when_get_tags_then_return_empty_list() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, null, testDate, robin);

        // When
        List<String> tags = journal.getTags();

        // Then
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }

    @Test
    void given_journal_with_empty_tags_when_get_tags_then_return_empty_list() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, new ArrayList<>(), testDate, robin);

        // When
        List<String> tags = journal.getTags();

        // Then
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }

    @Test
    void given_journal_when_set_tags_list_then_tags_are_updated() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);
        List<String> newTags = Arrays.asList("updated", "new");

        // When
        journal.setTagsList(newTags);

        // Then
        assertEquals(2, journal.getTags().size());
        assertTrue(journal.getTags().contains("updated"));
        assertTrue(journal.getTags().contains("new"));
    }

    @Test
    void given_journal_when_set_null_tags_list_then_tags_are_cleared() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);

        // When
        journal.setTagsList(null);

        // Then
        List<String> tags = journal.getTags();
        assertTrue(tags.isEmpty());
    }

    @Test
    void given_journal_when_set_empty_tags_list_then_tags_are_cleared() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);

        // When
        journal.setTagsList(new ArrayList<>());

        // Then
        List<String> tags = journal.getTags();
        assertTrue(tags.isEmpty());
    }

    @Test
    void given_journal_with_single_tag_when_get_tags_then_return_single_tag() {
        // Given
        List<String> singleTag = Arrays.asList("important");
        Journal journal = new Journal(testTitle, testContent, testMood, singleTag, testDate, robin);

        // When
        List<String> tags = journal.getTags();

        // Then
        assertEquals(1, tags.size());
        assertEquals("important", tags.get(0));
    }

    @Test
    void given_journal_with_multiple_tags_when_set_tags_then_format_is_correct() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, null, testDate, robin);
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");

        // When
        journal.setTagsList(tags);

        // Then
        assertEquals(3, journal.getTags().size());
        assertEquals("tag1", journal.getTags().get(0));
        assertEquals("tag2", journal.getTags().get(1));
        assertEquals("tag3", journal.getTags().get(2));
    }

    @Test
    void given_journal_when_get_date_then_return_correct_date() {
        // Given
        LocalDateTime specificDate = LocalDateTime.of(2024, 12, 1, 15, 30);
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, specificDate, robin);

        // When
        LocalDateTime date = journal.getDate();

        // Then
        assertEquals(specificDate, date);
    }

    @Test
    void given_journal_when_set_date_then_date_is_updated() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);
        LocalDateTime newDate = LocalDateTime.of(2024, 12, 15, 10, 0);

        // When
        journal.setDate(newDate);

        // Then
        assertEquals(newDate, journal.getDate());
    }

    @Test
    void given_journal_when_get_user_then_return_associated_user() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);

        // When
        User user = journal.getUser();

        // Then
        assertNotNull(user);
        assertEquals(robin.getUserName(), user.getUserName());
        assertEquals(robin.getEmail(), user.getEmail());
    }

    @Test
    void given_journal_when_set_user_then_user_is_updated() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);
        User newUser = new User("roel_crabbe", "roel@example.com", "Pass123!");

        // When
        journal.setUser(newUser);

        // Then
        assertEquals(newUser, journal.getUser());
        assertEquals("roel_crabbe", journal.getUser().getUserName());
    }

    @Test
    void given_different_moods_when_create_journals_then_all_moods_are_supported() {
        // Given & When
        Journal happyJournal = new Journal("Happy Entry", "Content", Mood.HAPPY, testTags, testDate, robin);
        Journal calmJournal = new Journal("Calm Entry", "Content", Mood.CALM, testTags, testDate, robin);
        Journal anxiousJournal = new Journal("Anxious Entry", "Content", Mood.ANXIOUS, testTags, testDate, robin);
        Journal sadJournal = new Journal("Sad Entry", "Content", Mood.SAD, testTags, testDate, robin);

        // Then
        assertEquals(Mood.HAPPY, happyJournal.getMood());
        assertEquals(Mood.CALM, calmJournal.getMood());
        assertEquals(Mood.ANXIOUS, anxiousJournal.getMood());
        assertEquals(Mood.SAD, sadJournal.getMood());
    }

    @Test
    void given_multiple_journals_when_created_then_each_has_unique_id() {
        // Given & When
        Journal journal1 = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);
        Journal journal2 = new Journal("Another Title", "Other content", Mood.CALM, testTags, testDate, robin);
        Journal journal3 = new Journal("Third Title", "More content", Mood.SAD, testTags, testDate, robin);

        // Then
        assertNotEquals(journal1.getId(), journal2.getId());
        assertNotEquals(journal2.getId(), journal3.getId());
        assertNotEquals(journal1.getId(), journal3.getId());
    }

    @Test
    void given_long_content_when_create_journal_then_content_is_stored_completely() {
        // Given
        String longContent = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
            "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" +
            "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc";

        // When
        Journal journal = new Journal(testTitle, longContent, testMood, testTags, testDate, robin);

        // Then
        assertEquals(longContent, journal.getContent());
        assertEquals(longContent.length(), journal.getContent().length());
    }

    @Test
    void given_tags_with_special_characters_when_set_tags_then_tags_are_stored() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, null, testDate, robin);
        List<String> specialTags = Arrays.asList("work-related", "self_improvement", "daily.routine");

        // When
        journal.setTagsList(specialTags);

        // Then
        List<String> retrievedTags = journal.getTags();
        assertEquals(3, retrievedTags.size());
        assertTrue(retrievedTags.contains("work-related"));
        assertTrue(retrievedTags.contains("self_improvement"));
        assertTrue(retrievedTags.contains("daily.routine"));
    }

    @Test
    void given_journal_when_update_multiple_fields_then_all_fields_are_updated() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);
        String newTitle = "Updated Title";
        String newContent = "Updated Content";
        Mood newMood = Mood.GRATEFUL;
        List<String> newTags = Arrays.asList("updated");
        LocalDateTime newDate = LocalDateTime.now().plusDays(1);

        // When
        journal.setTitle(newTitle);
        journal.setContent(newContent);
        journal.setMood(newMood);
        journal.setTagsList(newTags);
        journal.setDate(newDate);

        // Then
        assertEquals(newTitle, journal.getTitle());
        assertEquals(newContent, journal.getContent());
        assertEquals(newMood, journal.getMood());
        assertEquals(newTags, journal.getTags());
        assertEquals(newDate, journal.getDate());
    }

    @Test
    void given_empty_title_when_create_journal_then_journal_is_created() {
        // Given
        String emptyTitle = "";

        // When
        Journal journal = new Journal(emptyTitle, testContent, testMood, testTags, testDate, robin);

        // Then
        assertNotNull(journal);
        assertEquals("", journal.getTitle());
    }

    @Test
    void given_empty_content_when_create_journal_then_journal_is_created() {
        // Given
        String emptyContent = "";

        // When
        Journal journal = new Journal(testTitle, emptyContent, testMood, testTags, testDate, robin);

        // Then
        assertNotNull(journal);
        assertEquals("", journal.getContent());
    }

    @Test
    void given_journal_with_all_mood_types_when_set_mood_then_all_moods_work() {
        // Given
        Journal journal = new Journal(testTitle, testContent, Mood.HAPPY, testTags, testDate, robin);

        // When & Then
        journal.setMood(Mood.EXCITED);
        assertEquals(Mood.EXCITED, journal.getMood());

        journal.setMood(Mood.STRESSED);
        assertEquals(Mood.STRESSED, journal.getMood());

        journal.setMood(Mood.PEACEFUL);
        assertEquals(Mood.PEACEFUL, journal.getMood());

        journal.setMood(Mood.GRATEFUL);
        assertEquals(Mood.GRATEFUL, journal.getMood());
    }

    @Test
    void given_journal_with_tags_when_replace_tags_then_old_tags_are_removed() {
        // Given
        List<String> originalTags = Arrays.asList("old1", "old2", "old3");
        Journal journal = new Journal(testTitle, testContent, testMood, originalTags, testDate, robin);
        List<String> newTags = Arrays.asList("new1", "new2");

        // When
        journal.setTagsList(newTags);

        // Then
        List<String> retrievedTags = journal.getTags();
        assertEquals(2, retrievedTags.size());
        assertFalse(retrievedTags.contains("old1"));
        assertFalse(retrievedTags.contains("old2"));
        assertFalse(retrievedTags.contains("old3"));
        assertTrue(retrievedTags.contains("new1"));
        assertTrue(retrievedTags.contains("new2"));
    }

    @Test
    void given_journal_when_get_id_then_return_non_null_id() {
        // Given
        Journal journal = new Journal(testTitle, testContent, testMood, testTags, testDate, robin);

        // When
        JournalId id = journal.getId();

        // Then
        assertNotNull(id);
        assertNotNull(id.id());
    }
}
