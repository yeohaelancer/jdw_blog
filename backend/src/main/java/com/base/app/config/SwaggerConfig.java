package com.base.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Base Project API")
                        .description("Spring Boot + MyBatis 기반 API 문서")
                        .version("v1.0.0")
                        .contact(new Contact().name("개발팀")))
                .servers(List.of(
                        new Server().url("/").description("Default Server")
                ));
    }

}
