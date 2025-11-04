package be.ucll.group8.craftmanshipgroep8.journal.domain;

import org.springframework.util.Assert;

import java.util.UUID;

public record JournalId(UUID id) {

    public JournalId {
        Assert.notNull(id, "JournalId cannot be null.");
    }

    public JournalId() {
        this(UUID.randomUUID());
    }

}
