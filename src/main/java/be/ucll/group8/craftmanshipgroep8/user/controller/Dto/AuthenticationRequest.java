package be.ucll.group8.craftmanshipgroep8.user.controller.Dto;

public record AuthenticationRequest(
    String username,
    String password
) {
}
