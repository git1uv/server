package com.simter.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title="심터 API",
        description = "예소 깃러브",
        version = "v1")
)
@Configuration
public class SwaggerConfig {
    private Dotenv dotenv = Dotenv.load();
    private String SERVER_URL = dotenv.get("SERVER_URL");
    @Bean
    public OpenAPI OpenAPI() {

        String jwtSchemeName = "JWT TOKEN";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                        .scheme("bearer")
                        .bearerFormat("JWT"));
        return new OpenAPI()
                .addServersItem(new Server().url(SERVER_URL))
                .info(new io.swagger.v3.oas.models.info.Info())
                .addSecurityItem(securityRequirement)
                .components(components);


    }
}
