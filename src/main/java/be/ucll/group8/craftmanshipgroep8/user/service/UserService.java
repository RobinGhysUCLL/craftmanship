package be.ucll.group8.craftmanshipgroep8.user.service;

import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.AuthenticationResponse;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.SignUpRequest;
import be.ucll.group8.craftmanshipgroep8.user.controller.Dto.SignupResponse;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import be.ucll.group8.craftmanshipgroep8.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUserName(username);
    }

    boolean userExistsByUsername(String username) {
        return findUserByUsername(username) != null;
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    boolean userExistsByEmail(String email) {
        return findUserByEmail(email) != null;
    }


    public AuthenticationResponse authenticate(String email, String password) {
        if (!userExistsByEmail(email)) {
            throw new RuntimeException("Gebruiker met email '" + email + "' bestaat niet.");
        }
        User user = findUserByEmail(email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Onjuist wachtwoord.");
        }

        return new AuthenticationResponse(jwtService.generateToken(user), user.getUserName());
    }

    public SignupResponse signup(SignUpRequest signUpInput) {
        if (userExistsByUsername(signUpInput.username())) {
            throw new RuntimeException("Gebruiker met gebruikersnaam '" + signUpInput.username() + "' bestaat al.");
        }

        final var hashedPassword = passwordEncoder.encode(signUpInput.password());
        final var user = new User(signUpInput.username(), signUpInput.email(), hashedPassword);
        final User savedUser = userRepository.save(user);

        return new SignupResponse(jwtService.generateToken(savedUser));
    }
}
