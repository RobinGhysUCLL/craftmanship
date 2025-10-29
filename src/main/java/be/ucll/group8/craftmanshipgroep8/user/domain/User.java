package be.ucll.group8.craftmanshipgroep8.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity(name = "user")
public class User {
    @Id
    private UUID id;
    private String userName;
    private String password;
    private String email;


    public User(UUID id, String userName, String email, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    protected User() {

    }


    public UUID getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean equals(User user) {
        return this.id.equals(user.getId()) && this.userName.equals(user.getUserName()) && this.email.equals(user.getEmail()) && this.password.equals(user.getPassword());
    }
}
