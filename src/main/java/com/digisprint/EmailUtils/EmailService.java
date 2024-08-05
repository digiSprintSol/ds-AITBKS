package com.digisprint.EmailUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

	private JavaMailSender javaMailSender;

	public EmailService(JavaMailSender javaMailSender) {
		super();
		this.javaMailSender = javaMailSender;
	}
	
	@Autowired
    private ResourceLoader resourceLoader;
	
	@Async
	public String MailSendingService(String fromEmail, String[] toEmail, String bodyOfMail, String subjectOfMail) throws IOException, MessagingException {
		
		
	     MimeMessage message = javaMailSender.createMimeMessage();
	     MimeMessageHelper helper = new MimeMessageHelper(message, true);
	     helper.setFrom(fromEmail);	
	     helper.setTo(toEmail);
	     helper.setSubject(subjectOfMail);
	     helper.setText(bodyOfMail, true);
		javaMailSender.send(message);
		
		return "Mail Sent";
		
	}
	
}
