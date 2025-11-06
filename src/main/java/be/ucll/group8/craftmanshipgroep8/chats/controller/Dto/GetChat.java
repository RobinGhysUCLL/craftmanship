package be.ucll.group8.craftmanshipgroep8.chats.controller.Dto;

import java.util.List;

public record GetChat(
        List<PostMessage> messages) {
}
