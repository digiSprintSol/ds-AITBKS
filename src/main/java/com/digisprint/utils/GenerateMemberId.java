package com.digisprint.utils;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class GenerateMemberId {

	private static final String PREFIX = "AITBKS";
	
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	
	public static String generateMemberId() {
		
		return PREFIX + 1000000000 + SECURE_RANDOM.nextInt(900000000);
		
	}
	
}
