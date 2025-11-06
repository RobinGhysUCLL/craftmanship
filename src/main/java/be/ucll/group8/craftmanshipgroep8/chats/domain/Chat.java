package be.ucll.group8.craftmanshipgroep8.chats.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "chat")
public class Chat {

    @EmbeddedId
    private ChatId id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private List<Message> messages = new ArrayList<>();

    public Chat(User user) {
        this.id = new ChatId();
        this.user = user;
    }

    protected Chat() {
    }

    public User getUser() {
        return user;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }
}
