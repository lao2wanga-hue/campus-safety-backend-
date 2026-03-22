package com.campus.safety.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校园安全隐患整改管理系统 API")
                        .version("1.0")
                        .description("校园安全隐患上报、整改、跟踪管理系统")
                        .contact(new Contact()
                                .name("系统管理员")
                                .email("admin@campus.edu")));
    }

    @Bean
    public GroupedOpenApi hazardApi() {
        return GroupedOpenApi.builder()
                .group("隐患管理")
                .pathsToMatch("/api/hazard/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户管理")
                .pathsToMatch("/api/user/**")
                .build();
    }
}