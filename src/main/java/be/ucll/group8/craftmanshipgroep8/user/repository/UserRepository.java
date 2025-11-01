package be.ucll.group8.craftmanshipgroep8.user.repository;

import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.domain.UserId;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UserId> {
    User findByUserName(String userName);

    User findUserByUserName(String userName);

    User findUserByEmail(
            @Email(message = "Email format is invalid, example email format: user@example.com") String email);
}
