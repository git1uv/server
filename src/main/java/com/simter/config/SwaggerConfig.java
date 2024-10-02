package com.simter.config;

import static java.lang.System.getenv;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Arrays;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title="SIMTER API",
        description = "예소 깃러브",
        version = "v1")
)
@Configuration
public class SwaggerConfig {
    Map<String, String> env = getenv();
    private String SERVER_URL = env.get("SERVER_URL");

    @Bean
    public OpenAPI OpenAPI() {
        Server localServer = new Server();
        localServer.setDescription("local");
        localServer.setUrl("http://localhost:8080");

        Server deployServer = new Server();
        deployServer.setDescription("dev");
        deployServer.setUrl(SERVER_URL);

        String jwtSchemeName = "JWT TOKEN";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
        return new OpenAPI()
                .servers(Arrays.asList(localServer, deployServer))
                .info(new io.swagger.v3.oas.models.info.Info())
                .addSecurityItem(securityRequirement)
                .components(components);


    }
}
