package com.wesplit.main;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition
public class WeSplitApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeSplitApplication.class, args);
    }
    @Bean
    ModelMapper getModelMapper(){
        return new ModelMapper();
    }
    //Swagger Config
    @Bean
    OpenAPI configSwagger(){
        return new OpenAPI().info(
                new Info()
                        .title("WeSplit APIs")
                        .description("by shaurya")
        );
    }
}
