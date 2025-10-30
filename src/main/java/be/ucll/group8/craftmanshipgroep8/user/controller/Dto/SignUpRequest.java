package be.ucll.group8.craftmanshipgroep8.user.controller.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
    @NotBlank
    String username,
    @NotBlank
    String password,
    @Email
    String email
) {
}
