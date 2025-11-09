package be.ucll.group8.craftmanshipgroep8.journal.controller.Dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import be.ucll.group8.craftmanshipgroep8.journal.domain.Mood;

public record PostJournalDto(
        String title,
        String content,
        Mood mood,
        List<String> tags,
        LocalDateTime date) {

}
