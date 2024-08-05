package com.digisprint.EmailUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Configuration
@Data
public class EmailTemplates {

	@Value("${configuration.emailBody.welcome}")
	private String welcomeMailAfterFillingFirstRegistrationFrom;
	
	@Value("${configuration.emailBody.loginCredentialsEmail}")
	private String loginCredentialsEmail;
	
	@Value("${configuration.emailBody.committeeRejectEmail}")
	private String committeeRejectEmail;
	
	@Value("${configuration.emailBody.membershipApproved}")
	private String membershipApproved;
	
	@Value("${configuration.emailBody.paymentFormalEmail}")
	private String paymentApprovalEmail;
	
	@Value("${configuration.emailBody.presidentApprovalEmail}")
	private String presidentApprovalEmail;
	
	@Value("${configuration.emailBody.presidentRejectionEmail}")
	private String presidentRejectionEmail;
	
	@Value("${configuration.emailBody.notifyCommitteeForNewRegisteration}")
	private String newUserNotifyToCommittee;
}
