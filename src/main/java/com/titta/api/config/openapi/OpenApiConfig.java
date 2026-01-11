package com.titta.api.config.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI myOpenAPI() {
        // Configuración del servidor (URL base)
        String serverUrl = "http://localhost:" + serverPort + contextPath;
        if (contextPath.equals("/"))
            serverUrl = "http://localhost:" + serverPort;

        Server devServer = new Server();
        devServer.setUrl(serverUrl);
        devServer.setDescription("Servidor de Desarrollo");

        // Información de contacto
        Contact contact = new Contact();
        contact.setEmail("soporte@titta.com");
        contact.setName("Equipo Titta");
        contact.setUrl("https://www.titta.com");

        // Licencia
        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        // Información General de la API
        Info info = new Info()
                .title("Titta API")
                .version("1.0")
                .contact(contact)
                .description(
                        "Esta API expone endpoints para gestionar ventas, inventario y usuarios del sistema Titta.")
                .termsOfService("https://www.titta.com/terms")
                .license(mitLicense);

        // Configuración de Seguridad (JWT Bearer Token)
        // Esto habilita el botón "Authorize" en la UI de Swagger
        String securitySchemeName = "Bearer Authentication";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);

        Components components = new Components().addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}