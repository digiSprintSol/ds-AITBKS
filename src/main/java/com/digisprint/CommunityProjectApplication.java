package com.digisprint;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.digisprint.utils.ApplicationConstants;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;

@SpringBootApplication
@EnableScheduling
public class CommunityProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityProjectApplication.class, args);
	}
	
	@Bean
	public OperationCustomizer customGlobalHeaders() {
	    return (Operation operation, HandlerMethod handlerMethod) -> {
	        Parameter authorization = new Parameter()
	                .in(ParameterIn.HEADER.toString())
	                .schema(new StringSchema()._default("Bearer <token>")) // Specify Bearer type
	                .name(ApplicationConstants.TOKEN)
	                .description(ApplicationConstants.AUTH_TOKEN)
	                .required(true);
	        operation.addParametersItem(authorization);
	        return operation;
	    };
	}
}
