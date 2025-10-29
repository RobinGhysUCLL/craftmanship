package be.ucll.group8.craftmanshipgroep8.user.service;

import be.ucll.group8.craftmanshipgroep8.config.JwtProperties;
import be.ucll.group8.craftmanshipgroep8.user.domain.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;
    private final JwtEncoder jwtEncoder;

    public JwtService(JwtProperties jwtProperties,
                      JwtEncoder jwtEncoder) {
        this.jwtProperties = jwtProperties;
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(String username, String email) {
        final var now = Instant.now();
        final var expiresAt = now.plus(jwtProperties.token().lifetime());
        final var header = JwsHeader.with(MacAlgorithm.HS256).build();
        final var claims = JwtClaimsSet.builder()
            .issuer(jwtProperties.token().issuer())
            .issuedAt(now)
            .expiresAt(expiresAt)
            .subject(email)
            .claim("username", username)
            .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String generateToken(User user) {
        return generateToken(user.getUserName(), user.getEmail());
    }
}
