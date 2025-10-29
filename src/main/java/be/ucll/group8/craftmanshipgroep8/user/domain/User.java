package be.ucll.group8.craftmanshipgroep8.user.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity(name = "user")
public class User {

    @EmbeddedId
    private UserId id;

    @NotNull(message = "Username cannot be null.")
    private String userName;

    @NotNull(message = "Password cannot be null.")
    private String password;

    @Email(message = "Email format is invalid, example email format: user@example.com")
    private String email;


    public User(String userName, String email, String password) {
        this.id = new UserId();
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    protected User() {

    }


    public UserId getId() {
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
