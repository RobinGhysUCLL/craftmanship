package be.ucll.group8.craftmanshipgroep8.user.controller.Dto;

public record AuthenticationResponse(
    String token,
    String username
) {
}
