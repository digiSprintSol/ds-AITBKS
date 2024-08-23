package com.digisprint.requestBean;

import lombok.Data;

@Data
public class VerifyEmail {

	private String email;
	
	private String otp;
	
	private String password;
}
