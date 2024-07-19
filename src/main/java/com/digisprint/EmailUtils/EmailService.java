package com.digisprint.EmailUtils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

	private JavaMailSender javaMailSender;

	public EmailService(JavaMailSender javaMailSender) {
		super();
		this.javaMailSender = javaMailSender;
	}
	
	@Async
	public String MailSendingService(String fromEmail, String toEmail, String bodyOfMail, String subjectOfMail) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setCc(fromEmail);
		message.setText(bodyOfMail);
		message.setSubject(subjectOfMail);
		
		javaMailSender.send(message);
		
		return "Mail Sent";
		
	}
	
}
