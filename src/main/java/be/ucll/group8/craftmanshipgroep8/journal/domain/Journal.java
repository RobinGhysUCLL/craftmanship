package be.ucll.group8.craftmanshipgroep8.journal.domain;

import java.time.LocalDate;
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
    private List<String> tags;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    public Journal(String title, String content, Mood mood, List<String> tags, LocalDate date, User user) {
        this.id = new JournalId();
        setTitle(title);
        setContent(content);
        setMood(mood);
        setTags(tags);
        setDate(date);
        setUser(user);
    }

    protected Journal() {
    }

    private JournalId getId() {
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
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
