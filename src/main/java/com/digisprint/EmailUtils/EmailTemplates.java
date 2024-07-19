package com.digisprint.EmailUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class EmailTemplates {

	@Value("${portal.configuration.emailBody.welcome}")
	private String welcomeMailAfterFillingFirstRegistrationFrom;
	
}
