package be.ucll.group8.craftmanshipgroep8.journal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import be.ucll.group8.craftmanshipgroep8.journal.controller.Dto.PostJournalDto;
import be.ucll.group8.craftmanshipgroep8.journal.domain.Journal;
import be.ucll.group8.craftmanshipgroep8.journal.repository.JournalRepository;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.service.UserService;

@Service
public class JournalService {
    private JournalRepository journalRepository;
    private UserService userService;

    public JournalService(JournalRepository journalRepository, UserService userService) {
        this.journalRepository = journalRepository;
        this.userService = userService;
    }

    public List<Journal> getJournals(String email) {
        if (!userService.userExistsByEmail(email)) {
            throw new RuntimeException("Gebruiker met email '" + email + "' bestaat niet.");
        }

        return journalRepository.findAllByUserEmail(email);
    }

    public Journal postJournal(String email, PostJournalDto postJournalDto) {
        if (!userService.userExistsByEmail(email)) {
            throw new RuntimeException("Gebruiker met email '" + email + "' bestaat niet.");
        }

        User user = userService.findUserByEmail(email);
        Journal newJournal = new Journal(postJournalDto.title(), postJournalDto.content(), postJournalDto.mood(),
                postJournalDto.tags(), postJournalDto.date(), user);

        return journalRepository.save(newJournal);

    }

}
