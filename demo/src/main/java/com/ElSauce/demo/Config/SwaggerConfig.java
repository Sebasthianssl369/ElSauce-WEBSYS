package com.ElSauce.demo.Config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI elSauceOpenAPI() {

        return new OpenAPI()

                .info(

                        new Info()

                                .title("API Restaurante El Sauce")

                                .description("API REST desarrollada en Spring Boot para la gestión de reservas, platos y usuarios.")

                                .version("1.0")

                                .contact(

                                        new Contact()

                                                .name("Equipo El Sauce")

                                                .email("equipo@elsauce.com"))

                                .license(

                                        new License()

                                                .name("MIT")))

                .externalDocs(

                        new ExternalDocumentation()

                                .description("Proyecto WebSys"));

    }

}
