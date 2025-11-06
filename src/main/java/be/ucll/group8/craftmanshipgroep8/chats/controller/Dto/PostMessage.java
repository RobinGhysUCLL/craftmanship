package be.ucll.group8.craftmanshipgroep8.chats.controller.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostMessage(
        UUID id,
        String message,
        Boolean ai,
        LocalDateTime timestamp) {

}
