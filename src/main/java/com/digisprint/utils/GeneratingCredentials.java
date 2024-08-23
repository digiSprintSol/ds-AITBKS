package com.digisprint.utils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digisprint.bean.RegistrationFrom;
import com.digisprint.repository.RegistrationFromRepository;

@Component
public class GeneratingCredentials {

	private static final String PREFIX = "AITBKS";
	
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	
	@Autowired
	private static RegistrationFromRepository registrationFromRepository;

	/**
	 * Generates MEMBER ID for the applicant
	 */
	public static String generateMemberId(String userId) {
		
		RegistrationFrom registrationFrom = registrationFromRepository.findById(userId).get();
		String typeOfMembership = registrationFrom.getPresidentChoosenMembershipForApplicant();
		
		String currentYear = String.valueOf(LocalDate.now().getYear()).substring(2);
		String prefix = new String();
		
		if(typeOfMembership.equalsIgnoreCase(RegistrationFormConstants.TRUSTEE))
			prefix = "TM-" + currentYear + "-";
		
		if(typeOfMembership.equalsIgnoreCase(RegistrationFormConstants.PATRON))
			prefix = "PM-" + currentYear + "-";
		
		if(typeOfMembership.equalsIgnoreCase(RegistrationFormConstants.LIFE_MEMBER))
			prefix = "LM-" + currentYear + "-";
		
		RegistrationFrom latestMember = registrationFromRepository.findTopByMembershipIdStartingWithOrderByMembershipIdDesc(prefix);
		
		int nextSequenceMember = 1;
		
		if(latestMember != null) {
			String lastMembershipId = latestMember.getMembershipId();
			String lastSequence = lastMembershipId.substring(lastMembershipId.lastIndexOf('-')+1);
			nextSequenceMember = Integer.parseInt(lastSequence)+1;
		}
		
		String nextSequencePadded = String.format("%04d", nextSequenceMember);
		
		
		return prefix + nextSequencePadded;
		
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
