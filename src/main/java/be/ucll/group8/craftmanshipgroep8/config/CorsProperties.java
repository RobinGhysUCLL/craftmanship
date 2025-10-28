package be.ucll.group8.craftmanshipgroep8.config;

import java.net.URL;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
        @DefaultValue("http://localhost:8080") List<URL> allowedOrigins) {
    // Idk what port frontend runs on
}
