package be.ucll.group8.craftmanshipgroep8.user.repository;

import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUserName(String userName);

    User findUserByUserName(String userName);
}
