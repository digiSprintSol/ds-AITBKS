package com.digisprint.bean;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class EmailUpload {
	
	private String toEmail;
	private String subject;
	private String body;

}
