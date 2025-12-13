package be.ucll.group8.craftmanshipgroep8.chats.domain;

import java.time.LocalDateTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {
    @EmbeddedId
    private MessageId id;

    private String message;

    private Boolean ai;

    private LocalDateTime timestamp;

    public Message(String message, Boolean ai) {
        this.id = new MessageId();
        this.message = message;
        this.ai = ai;
        this.timestamp = LocalDateTime.now();
    }

    protected Message() {
    }

    public MessageId getId() {
        return this.id;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getAi() {
        return ai;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
