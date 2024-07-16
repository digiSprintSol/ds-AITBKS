package com.digisprint;

import java.util.Base64;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;

@SpringBootApplication
public class CommunityProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityProjectApplication.class, args);
		
		   String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhY2Nlc3MiLCJ1c2VyTmFtZSI6InBycEAxMjM0IiwidXNlcklkIjoicHJwQDEyMzQiLCJ0eXBlIjoicHJwMTIzIiwiYWNjZXNzIjpbIlBSRVNJREVOVCIsIkFDQ09VTlRBTlQiLCJDT01NSVRFRSJdLCJpYXQiOjE3MjA2OTUyODgsImV4cCI6MTcyMDY5NTI4OH0.UVozq2SmQW05m5f49f9paSKSMnuhwq7AzB9KS5rG_Gw";
	        String[] chunks = token.split("\\.");
	        Base64.Decoder decoder = Base64.getUrlDecoder();

	        String header = new String(decoder.decode(chunks[0]));
	        String payload = new String(decoder.decode(chunks[1]));
	        String signature = chunks[2];

	        System.out.println("Header: " + header);
	        System.out.println("Payload: " + payload);
	        System.out.println("Signature: " + signature);
	}

	@Bean
	public OperationCustomizer customGlobalHeaders() {
		return (Operation operation, HandlerMethod handlerMethod) -> {
			Parameter token = new Parameter().in(ParameterIn.HEADER.toString()).schema(new StringSchema())
					.name("token").description("Auth token").required(true);
			operation.addParametersItem(token);
			return operation;
		};
	}
}
