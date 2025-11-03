package be.ucll.group8.craftmanshipgroep8.chats.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.ucll.group8.craftmanshipgroep8.chats.controller.Dto.MessageResponse;
import be.ucll.group8.craftmanshipgroep8.chats.service.MessageService;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<MessageResponse> getMessages(Principal principal) {
        String email = principal.getName();
        return messageService.getMessages(email);
    }

    @PostMapping
    public String postMessage(@RequestBody String message, Principal principal) {
        String email = principal.getName();
        return messageService.postMessage(email, message);
    }

}
