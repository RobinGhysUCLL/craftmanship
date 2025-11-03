package be.ucll.group8.craftmanshipgroep8.chats.controller.Dto;

import java.time.LocalDateTime;

public record MessageResponse(
    String id,
    String userMessage,
    String aiResponse,
    LocalDateTime timestamp) {
}
