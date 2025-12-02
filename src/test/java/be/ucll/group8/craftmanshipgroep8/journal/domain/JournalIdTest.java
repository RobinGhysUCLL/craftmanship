package be.ucll.group8.craftmanshipgroep8.journal.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JournalIdTest {

    @Test
    void given_valid_uuid_when_create_journal_id_then_journal_id_is_created_successfully() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        JournalId journalId = new JournalId(uuid);

        // Then
        assertNotNull(journalId);
        assertEquals(uuid, journalId.id());
    }

    @Test
    void given_no_argument_when_create_journal_id_then_generate_random_uuid() {
        // Given & When
        JournalId journalId = new JournalId();

        // Then
        assertNotNull(journalId);
        assertNotNull(journalId.id());
    }

    @Test
    void given_null_uuid_when_create_journal_id_then_throw_exception() {
        // Given
        UUID nullUuid = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new JournalId(nullUuid)
        );
        assertTrue(exception.getMessage().contains("JournalId cannot be null"));
    }

    @Test
    void given_same_uuid_when_create_journal_ids_then_journal_ids_are_equal() {
        // Given
        UUID uuid = UUID.randomUUID();

        // When
        JournalId journalId1 = new JournalId(uuid);
        JournalId journalId2 = new JournalId(uuid);

        // Then
        assertEquals(journalId1, journalId2);
    }

    @Test
    void given_different_uuids_when_create_journal_ids_then_journal_ids_are_not_equal() {
        // Given
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        // When
        JournalId journalId1 = new JournalId(uuid1);
        JournalId journalId2 = new JournalId(uuid2);

        // Then
        assertNotEquals(journalId1, journalId2);
    }

    @Test
    void given_multiple_journal_ids_when_created_without_argument_then_all_are_unique() {
        // Given & When
        JournalId journalId1 = new JournalId();
        JournalId journalId2 = new JournalId();
        JournalId journalId3 = new JournalId();

        // Then
        assertNotEquals(journalId1, journalId2);
        assertNotEquals(journalId2, journalId3);
        assertNotEquals(journalId1, journalId3);
    }

    @Test
    void given_journal_id_when_get_id_then_return_correct_uuid() {
        // Given
        UUID expectedUuid = UUID.randomUUID();
        JournalId journalId = new JournalId(expectedUuid);

        // When
        UUID actualUuid = journalId.id();

        // Then
        assertEquals(expectedUuid, actualUuid);
    }

    @Test
    void given_journal_id_when_use_as_record_then_equals_and_hashcode_work_correctly() {
        // Given
        UUID uuid = UUID.randomUUID();
        JournalId journalId1 = new JournalId(uuid);
        JournalId journalId2 = new JournalId(uuid);
        JournalId journalId3 = new JournalId(UUID.randomUUID());

        // When & Then
        assertEquals(journalId1, journalId2);
        assertEquals(journalId1.hashCode(), journalId2.hashCode());
        assertNotEquals(journalId1, journalId3);
        assertNotEquals(journalId1.hashCode(), journalId3.hashCode());
    }
}
