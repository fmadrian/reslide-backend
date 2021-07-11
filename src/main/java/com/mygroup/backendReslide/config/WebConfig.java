package com.mygroup.backendReslide.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
// @EnableWebMvc // Don't include in Spring boot applications.
public class WebConfig implements WebMvcConfigurer {
    // This method overrides the cors mappings and
    // includes all the cors configurations required for the backend.

    // In this example, we are accepting requests from all origins.
    // In a production environment WE CAN'T DO THIS.
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                .allowedOriginPatterns("*") // Allow any origin
                .allowedMethods("*") // Allow any (GET, DELETE, PUT, ...) method
                .maxAge(3600L)
                .allowedHeaders("*") // Allow any header
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }
    // spring mvc doesn't know how to handle the webjars which are coming as part of swagger configuration
    // That's why we have to add the resources handlers.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }
}
