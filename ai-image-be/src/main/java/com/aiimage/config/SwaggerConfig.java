package com.aiimage.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
            .group("v1-definition")
            .pathsToMatch("/api/**")
            .build();
  }

  @Bean
  public OpenAPI springShopOpenAPI() {
    Server serverLocal = new Server();
    serverLocal.setUrl("http://localhost:8080");
    serverLocal.setDescription("for local usages");
    Server serverDev = new Server();
    serverDev.setUrl("https://aa.newtype.design");
    serverDev.setDescription("for dev usages");
    Server serverReal = new Server();
    serverReal.setUrl("https://about-innc.ncsoft.com");
    serverReal.setDescription("for real usages");
    return new OpenAPI()
            .components(new Components().addSecuritySchemes("xpto",
                    new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)))
            .info(new Info()
                    .title("NC BLOG API")
                    .description("This is a Spring Boot RESTful service using springdoc-openapi and OpenAPI 3.")
                    .version("1.0")
            ).servers(Arrays.asList(serverLocal, serverDev));
  }
}
