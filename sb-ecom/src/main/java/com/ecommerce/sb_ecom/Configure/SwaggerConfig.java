package com.ecommerce.sb_ecom.Configure;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi(){
        SecurityScheme bearerScheme= new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT Bearer Token");

        SecurityRequirement bearerRequirment=new SecurityRequirement()
                .addList("Bearer Authentication");

        return  new OpenAPI()
                .info(new Info()
                        .title("Spring Boot eCommerce API")
                        .version("1.0")
                        .description("This is a Spring Boot Project for eCommarce")
                        .license(new License().name("Apache 2.0").url("http://himanshu.com"))
                        .contact( new Contact()
                                .name("Himanshu Kumar")
                                .email("himanshukuma93079@gmail.com")
                                .url("https://github.com/45himanshukumar")
                        )
                )
                .externalDocs( new ExternalDocumentation()
                        .description("Project Documention")
                        .url("http://himanshu.com")
                )
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",bearerScheme))
                .addSecurityItem(bearerRequirment);
    }
}
