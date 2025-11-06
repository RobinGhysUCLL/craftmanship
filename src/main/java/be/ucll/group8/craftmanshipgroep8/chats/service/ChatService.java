package be.ucll.group8.craftmanshipgroep8.chats.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.GetChat;
import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.PostMessage;
import be.ucll.group8.craftmanshipgroep8.chats.domain.Chat;
import be.ucll.group8.craftmanshipgroep8.chats.domain.Message;
import be.ucll.group8.craftmanshipgroep8.chats.repository.ChatRepository;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.service.UserService;

@Service
public class ChatService {

    private ChatRepository chatRepository;
    private UserService userService;

    public ChatService(ChatRepository chatRepository, UserService userService) {
        this.chatRepository = chatRepository;
        this.userService = userService;
    }

    public GetChat getMessages(String email) {
        if (!userService.userExistsByEmail(email)) {
            throw new RuntimeException("Gebruiker met email '" + email + "' bestaat niet.");
        }

        Chat chat = chatRepository.findByUserEmail(email);
        List<Message> messages = (chat == null || chat.getMessages() == null) ? new ArrayList<>() : chat.getMessages();

        List<PostMessage> dtoMessages = new ArrayList<>();
        for (Message m : messages) {
            var id = m.getId().id();
            var messageText = m.getMessage();
            var aiFlag = m.getAi();
            var timestamp = m.getTimestamp();

            dtoMessages.add(new PostMessage(id, messageText, aiFlag, timestamp));
        }

        return new GetChat(dtoMessages);
    }

    public PostMessage postMessage(String email, String message) {
        Message userMessage = new Message(message, false);

        // temporary response, this needs to be the AI message in the future
        Message aiResponse = new Message("Hello i am RAFVIOLI AI and i am here with a response", true);

        if (!userService.userExistsByEmail(email)) {
            throw new RuntimeException("Gebruiker met email '" + email + "' bestaat niet.");
        }

        Chat chat = chatRepository.findByUserEmail(email);

        chat.addMessage(userMessage);

        // temporary response, this needs to be the AI message in the future
        chat.addMessage(aiResponse);

        chatRepository.save(chat);

        // temporary response, this needs to be the AI message in the future
        return new PostMessage(aiResponse.getId().id(), aiResponse.getMessage(), aiResponse.getAi(),
                aiResponse.getTimestamp());
    }

}
