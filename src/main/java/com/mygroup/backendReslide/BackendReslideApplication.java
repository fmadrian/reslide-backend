package com.mygroup.backendReslide;

import com.mygroup.backendReslide.config.SwaggerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // Allow async processing in the app.
@Import(SwaggerConfiguration.class) // Imports the swagger configuration class.
public class BackendReslideApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendReslideApplication.class, args);
	}
}
