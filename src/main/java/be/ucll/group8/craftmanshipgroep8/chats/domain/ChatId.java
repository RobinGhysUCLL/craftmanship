package be.ucll.group8.craftmanshipgroep8.chats.domain;

import java.util.UUID;

import org.springframework.util.Assert;

public record ChatId(UUID id) {

    public ChatId {
        Assert.notNull(id, "MessageId cannot be null.");
    }

    public ChatId() {
        this(UUID.randomUUID());
    }

}
