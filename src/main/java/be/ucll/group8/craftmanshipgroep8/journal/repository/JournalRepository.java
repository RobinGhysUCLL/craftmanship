package be.ucll.group8.craftmanshipgroep8.journal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import be.ucll.group8.craftmanshipgroep8.journal.domain.Journal;
import be.ucll.group8.craftmanshipgroep8.journal.domain.JournalId;

public interface JournalRepository extends JpaRepository<Journal, JournalId> {
    List<Journal> findAllByUserEmail(String email);
}
