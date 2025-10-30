package be.ucll.group8.craftmanshipgroep8.user.controller.Dto;

import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(
    @NotNull(message = "Email cannot be null.")
    String email,
    @NotNull(message = "Password cannot be null.")
    String password
) {
}
