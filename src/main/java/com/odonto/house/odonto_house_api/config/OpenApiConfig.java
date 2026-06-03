package com.odonto.house.odonto_house_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI odontoHouseOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Odonto House API")
                        .description("OpenAPI documentation for the Odonto House clinic application")
                        .version("v1.0"));
    }
}
