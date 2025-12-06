package be.ucll.group8.craftmanshipgroep8.chats.service;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.GetChat;
import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.PostMessage;
import be.ucll.group8.craftmanshipgroep8.chats.domain.Chat;
import be.ucll.group8.craftmanshipgroep8.chats.domain.Message;
import be.ucll.group8.craftmanshipgroep8.chats.repository.ChatRepository;
import be.ucll.group8.craftmanshipgroep8.user.service.UserService;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserService userService;
    private final AiService aiService;

    public ChatService(ChatRepository chatRepository,
            UserService userService,
            AiService aiService) {
        this.chatRepository = chatRepository;
        this.userService = userService;
        this.aiService = aiService;
    }

    public GetChat getMessages(String email) {
        final var user = userService.findUserByEmail(email);
        final var messages = chatRepository.findByUserEmail(user.getEmail())
                .map(Chat::getMessages)
                .orElseGet(ArrayList::new);

        final var dtoMessages = new ArrayList<PostMessage>();
        for (var m : messages)
            dtoMessages.add(new PostMessage(
                    m.getId().id(),
                    m.getMessage(),
                    m.getAi(),
                    m.getTimestamp()));

        return new GetChat(dtoMessages);
    }

    public PostMessage postMessage(String email, String messageText) {
        final var user = userService.findUserByEmail(email);
        final var chat = chatRepository.findByUserEmail(email)
                .orElseGet(() -> new Chat(user));

        final var history = chat.getMessages();
        final var userMessage = new Message(messageText, false);
        chat.addMessage(userMessage);

        final var aiText = aiService.generateReply(history, messageText);
        final var aiResponse = new Message(aiText, true);
        chat.addMessage(aiResponse);

        chatRepository.save(chat);

        return new PostMessage(
                aiResponse.getId().id(),
                aiResponse.getMessage(),
                aiResponse.getAi(),
                aiResponse.getTimestamp());
    }
}
