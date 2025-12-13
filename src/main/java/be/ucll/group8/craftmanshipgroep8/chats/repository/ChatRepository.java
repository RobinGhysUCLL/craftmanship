package be.ucll.group8.craftmanshipgroep8.chats.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import be.ucll.group8.craftmanshipgroep8.chats.domain.Chat;
import be.ucll.group8.craftmanshipgroep8.chats.domain.ChatId;

@Repository
public interface ChatRepository extends JpaRepository<Chat, ChatId> {
    Optional<Chat> findByUserEmail(String email);
}
