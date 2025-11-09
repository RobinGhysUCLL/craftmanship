package be.ucll.group8.craftmanshipgroep8.journal.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Journal")
public class Journal {

    @EmbeddedId
    private JournalId id;

    private String title;
    private String content;
    private Mood mood;
    private String tags;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Journal(String title, String content, Mood mood, List<String> tags, LocalDateTime date, User user) {
        this.id = new JournalId();
        setTitle(title);
        setContent(content);
        setMood(mood);
        setTagsList(tags);
        setDate(date);
        setUser(user);
    }

    protected Journal() {
    }

    public JournalId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public List<String> getTags() {
        if (tags == null || tags.isEmpty() || tags.equals("{a}")) {
            return List.of();
        }
        String cleaned = tags.replace("{", "").replace("}", "");
        if (cleaned.isEmpty())
            return List.of();
        return Arrays.asList(cleaned.split(","));
    }

    public void setTagsList(List<String> tagsList) {
        if (tagsList == null || tagsList.isEmpty()) {
            this.tags = null;
        } else {
            this.tags = "{" + String.join(",", tagsList) + "}";
        }
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
