package be.ucll.group8.craftmanshipgroep8.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(String secretKey,
        @DefaultValue Token token) {
    public record Token(@DefaultValue("craftmanship") String issuer,
            @DefaultValue("7d") Duration lifetime) {
    }
}
