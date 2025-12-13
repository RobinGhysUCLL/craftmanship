package be.ucll.group8.craftmanshipgroep8.journal.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoodTest {

    @Test
    void given_mood_enum_when_get_all_values_then_return_all_mood_types() {
        // Given & When
        Mood[] moods = Mood.values();

        // Then
        assertEquals(8, moods.length);
        assertTrue(containsMood(moods, Mood.HAPPY));
        assertTrue(containsMood(moods, Mood.CALM));
        assertTrue(containsMood(moods, Mood.ANXIOUS));
        assertTrue(containsMood(moods, Mood.SAD));
        assertTrue(containsMood(moods, Mood.EXCITED));
        assertTrue(containsMood(moods, Mood.STRESSED));
        assertTrue(containsMood(moods, Mood.PEACEFUL));
        assertTrue(containsMood(moods, Mood.GRATEFUL));
    }

    @Test
    void given_mood_name_when_value_of_then_return_correct_mood() {
        // Given
        String moodName = "HAPPY";

        // When
        Mood mood = Mood.valueOf(moodName);

        // Then
        assertEquals(Mood.HAPPY, mood);
    }

    @Test
    void given_all_mood_names_when_value_of_then_return_correct_moods() {
        // Given & When & Then
        assertEquals(Mood.HAPPY, Mood.valueOf("HAPPY"));
        assertEquals(Mood.CALM, Mood.valueOf("CALM"));
        assertEquals(Mood.ANXIOUS, Mood.valueOf("ANXIOUS"));
        assertEquals(Mood.SAD, Mood.valueOf("SAD"));
        assertEquals(Mood.EXCITED, Mood.valueOf("EXCITED"));
        assertEquals(Mood.STRESSED, Mood.valueOf("STRESSED"));
        assertEquals(Mood.PEACEFUL, Mood.valueOf("PEACEFUL"));
        assertEquals(Mood.GRATEFUL, Mood.valueOf("GRATEFUL"));
    }

    @Test
    void given_invalid_mood_name_when_value_of_then_throw_exception() {
        // Given
        String invalidMoodName = "INVALID_MOOD";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> Mood.valueOf(invalidMoodName));
    }

    @Test
    void given_mood_when_compare_then_equality_works() {
        // Given
        Mood mood1 = Mood.HAPPY;
        Mood mood2 = Mood.HAPPY;
        Mood mood3 = Mood.SAD;

        // When & Then
        assertEquals(mood1, mood2);
        assertNotEquals(mood1, mood3);
    }

    @Test
    void given_mood_when_to_string_then_return_mood_name() {
        // Given
        Mood mood = Mood.GRATEFUL;

        // When
        String moodString = mood.toString();

        // Then
        assertEquals("GRATEFUL", moodString);
    }

    @Test
    void given_mood_when_get_name_then_return_correct_name() {
        // Given
        Mood mood = Mood.PEACEFUL;

        // When
        String name = mood.name();

        // Then
        assertEquals("PEACEFUL", name);
    }

    private boolean containsMood(Mood[] moods, Mood targetMood) {
        for (Mood mood : moods) {
            if (mood == targetMood) {
                return true;
            }
        }
        return false;
    }
}
