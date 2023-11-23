package br.com.jmarcos.assessment_task.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
@SecurityScheme(type = SecuritySchemeType.HTTP, bearerFormat = "JWT", name = "Authorization", scheme = "Bearer")
public class SwaggerConfiguration {

        @Bean
        public OpenAPI basOpenAPI() {
                return new OpenAPI()
                                .info(new Info().title("AssessmentTask API Documentation").version("1.0")
                                                .description("a simple assiessment task"));
        }

}
