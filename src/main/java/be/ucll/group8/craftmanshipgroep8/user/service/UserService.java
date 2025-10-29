package be.ucll.group8.craftmanshipgroep8.user.service;

import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.AuthenticationResponse;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUserName(username);
    }

    boolean userExistsByUsername(String username) {
        return findUserByUsername(username) != null;
    }


    public AuthenticationResponse authenticate(String username, String password) {
        if (!userExistsByUsername(username)) {
            throw new RuntimeException("Gebruiker met gebruikersnaam '" + username + "' bestaat niet.");
        }
        User user = userRepository.findByUserName(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Onjuist wachtwoord.");
        }

        return new AuthenticationResponse(jwtService.generateToken(user));
    }
}
