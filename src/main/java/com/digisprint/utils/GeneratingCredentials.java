package com.digisprint.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digisprint.repository.RegistrationFromRepository;

@Component
public class GeneratingCredentials {

	private static final String PREFIX = "AITBKS";
	
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	/**
	 * Generates MEMBER ID for the applicant
	 */
	public static String generateMemberId() {
		
		return PREFIX + 1000000000 + SECURE_RANDOM.nextInt(900000000);
		
	}
	
	/**
	 * Generates Passcode for login
	 * @param applicantId
	 * @return
	 */
	public String generatePasscode(String applicantCategory, String mobileNumber) {
		
		String concatenatedString = applicantCategory + mobileNumber;
		
		List<Character> individualCharactersInConcatenatedString = new ArrayList<>();
		for(char c: concatenatedString.toCharArray()) {
			individualCharactersInConcatenatedString.add(c);
		}
		
		Collections.shuffle(individualCharactersInConcatenatedString);
		
		StringBuilder result = new StringBuilder();
        for (char c : individualCharactersInConcatenatedString) {
            result.append(c);
        }
		
		return EmailConstants.PASSCODE_PATTERNS+result.toString();
	}
	
}
