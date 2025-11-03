package be.ucll.group8.craftmanshipgroep8.chats.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import be.ucll.group8.craftmanshipgroep8.chats.domain.MessageId;
import be.ucll.group8.craftmanshipgroep8.chats.domain.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, MessageId> {
    List<Message> findAllByUserEmail(String email);
}
