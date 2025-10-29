package be.ucll.group8.craftmanshipgroep8.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@OpenAPIDefinition(security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${SERVER_PORT:8080}") String serverPort,
            @Value("${spring.profiles.active:}") String activeProfile) {
        OpenAPI openAPI = new OpenAPI();

        // Localhost:{SERVER_PORT} for dev and https://craftmanship.robinghys.com/ in
        // production for swagger API calls
        if (activeProfile != null && activeProfile.toLowerCase().contains("prod")) {
            openAPI.addServersItem(new Server().url("https://craftmanship.robinghys.com/"));
        } else {
            openAPI.addServersItem(new Server().url("http://localhost:" + serverPort));
        }

        return openAPI;
    }
}
