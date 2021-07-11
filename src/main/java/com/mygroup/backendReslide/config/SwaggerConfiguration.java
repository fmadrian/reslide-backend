package com.mygroup.backendReslide.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration // indicates this is a configuration file
public class SwaggerConfiguration {
    @Bean // We can change the name of the bean (redditCloneApi) to whatever we like.
    public Docket reslideApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo());
        // According to Springfox documentation a Docket is a brief statement of the contents of the document
    }

    // Returns an object with the details of the API information.
    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("Reslide Clone API")
                .version("1.0")
                .description("API for Reslide Clone Application")
                .contact(new Contact("Adrián", "www.github.com/fsv2828", "adrian@email.com"))
                .license("Apache License Version 2.0")
                .build();
    }
}