package be.ucll.group8.craftmanshipgroep8.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.domain.UserId;
import jakarta.validation.constraints.Email;

@Repository
public interface UserRepository extends JpaRepository<User, UserId> {
    Optional<User> findUserByUserName(String userName);

    Optional<User> findUserByEmail(
            @Email(message = "Email format is invalid, example email format: user@example.com") String email);
}
