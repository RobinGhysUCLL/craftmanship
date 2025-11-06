package be.ucll.group8.craftmanshipgroep8.chats.domain;

import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
