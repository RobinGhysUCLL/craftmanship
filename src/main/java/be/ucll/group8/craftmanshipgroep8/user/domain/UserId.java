package be.ucll.group8.craftmanshipgroep8.user.domain;

import org.springframework.util.Assert;

import java.util.UUID;


public record UserId(UUID id) {

    public UserId {
        Assert.notNull(id, "UserId cannot be null.");
    }

    public UserId() {
        this(UUID.randomUUID());
    }

}
