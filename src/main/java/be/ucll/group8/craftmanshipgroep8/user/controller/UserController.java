package be.ucll.group8.craftmanshipgroep8.user.controller;

import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.AuthenticationRequest;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.AuthenticationResponse;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.SignUpInput;
import be.ucll.group8.craftmanshipgroep8.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return userService.authenticate(authenticationRequest.email(), authenticationRequest.password());
    }

    @PostMapping("/signup")
    public AuthenticationResponse signup(@Valid @RequestBody SignUpInput signUpInput) {
        return userService.signup(signUpInput);
    }

}
