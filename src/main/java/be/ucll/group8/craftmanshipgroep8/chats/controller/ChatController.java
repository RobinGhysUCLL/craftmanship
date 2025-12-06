package be.ucll.group8.craftmanshipgroep8.chats.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.GetChat;
import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.PostMessage;
import be.ucll.group8.craftmanshipgroep8.chats.service.ChatService;

@RestController
@RequestMapping("/messages")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public GetChat getChat(Principal principal) {
        String email = principal.getName();
        return chatService.getMessages(email);
    }

    @PostMapping
    public PostMessage postMessage(@RequestBody String message, Principal principal) {
        String email = principal.getName();
        return chatService.postMessage(email, message);
    }
}
