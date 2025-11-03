package be.ucll.group8.craftmanshipgroep8.chats.domain;

import java.util.UUID;

import org.springframework.util.Assert;

public record MessageId(UUID id) {

    public MessageId {
        Assert.notNull(id, "MessageId cannot be null.");
    }

    public MessageId() {
        this(UUID.randomUUID());
    }

}
