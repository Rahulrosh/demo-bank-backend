package com.rahul.demo_bank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info=@Info(
				title="Demo Bank App",
				description="Backend Rest APIs for Demo Bank App",
				version="v1.0",
				contact = @Contact(
						name="Rahul Roshan",
						email="rahulroshan324@gmail.com"
				),
				license = @License(
						name="Rahul Roshan"
				)

		),
		externalDocs = @ExternalDocumentation(
				description = "Demo Bank Application"

		)
)
public class DemoBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoBankApplication.class, args);
	}

}
