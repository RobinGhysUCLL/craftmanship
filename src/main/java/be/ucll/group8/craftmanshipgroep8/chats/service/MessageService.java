package be.ucll.group8.craftmanshipgroep8.chats.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.MessageResponse;
import be.ucll.group8.craftmanshipgroep8.chats.domain.Message;
import be.ucll.group8.craftmanshipgroep8.chats.repository.MessageRepository;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.service.UserService;

@Service
public class MessageService {

    private MessageRepository messageRepository;
    private UserService userService;

    public MessageService(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    public List<MessageResponse> getMessages(String email) {
        final var messages = messageRepository.findAllByUserEmail(email);
        final var responses = new ArrayList<MessageResponse>();

        if (!userService.userExistsByEmail(email)) {
            throw new RuntimeException("Gebruiker met email '" + email + "' bestaat niet.");
        }

        for (Message message : messages) {
            final var response = new MessageResponse(
                message.getId().id().toString(),
                message.getUserMessage(),
                message.getAiResponse(),
                message.getTimestamp());
            responses.add(response);
        }

        return responses;
    }

    public String postMessage(String email, String message) {
        String tempResponse = "Hello i am RAFVIOLI AI and i am here with a response";

        if (!userService.userExistsByEmail(email)) {
            throw new RuntimeException("Gebruiker met email '" + email + "' bestaat niet.");
        }

        User user = userService.findUserByEmail(email);

        Message newMessage = new Message(user, message, tempResponse);

        messageRepository.save(newMessage);

        return tempResponse;
    }

}
