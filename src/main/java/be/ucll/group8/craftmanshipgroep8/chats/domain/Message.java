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

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String userMessage;

    private String aiResponse;

    private LocalDateTime timestamp;

    public Message(User user, String userMessage, String aiResponse) {
        this.id = new MessageId();
        this.user = user;
        this.userMessage = userMessage;
        this.aiResponse = aiResponse;
        this.timestamp = LocalDateTime.now();
    }

    protected Message() {
    }

    public MessageId getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getAiResponse() {
        return aiResponse;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
