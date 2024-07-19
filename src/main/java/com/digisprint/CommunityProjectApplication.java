package com.digisprint;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.HandlerMethod;

import com.digisprint.utils.ApplicationConstants;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;

@SpringBootApplication
public class CommunityProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityProjectApplication.class, args);
	}

	@Bean
	public OperationCustomizer customGlobalHeaders() {
		return (Operation operation, HandlerMethod handlerMethod) -> {
			Parameter token = new Parameter().in(ParameterIn.HEADER.toString()).schema(new StringSchema())
					.name(ApplicationConstants.TOKEN).description(ApplicationConstants.AUTH_TOKEN).required(true);
			operation.addParametersItem(token);
			return operation;
		};
	}
}
