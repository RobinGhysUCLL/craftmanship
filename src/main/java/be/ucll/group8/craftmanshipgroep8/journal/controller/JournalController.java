package be.ucll.group8.craftmanshipgroep8.journal.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.group8.craftmanshipgroep8.journal.controller.Dto.PostJournalDto;
import be.ucll.group8.craftmanshipgroep8.journal.domain.Journal;
import be.ucll.group8.craftmanshipgroep8.journal.service.JournalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/journals")
public class JournalController {

    private JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @GetMapping
    public List<Journal> getJournals(Principal principal) {
        String email = principal.getName();
        return journalService.getJournals(email);
    }

    @PostMapping
    public Journal postMethodName(@RequestBody PostJournalDto postJournalDto, Principal principal) {
        String email = principal.getName();
        return journalService.postJournal(email, postJournalDto);
    }

}
