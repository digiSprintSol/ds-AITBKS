package com.digisprint.service;

import org.springframework.http.ResponseEntity;

import com.digisprint.bean.Donation;
import com.digisprint.exception.UserNotFoundException;

public interface DonationService {

	ResponseEntity<String> createDonation(Donation donation);

	ResponseEntity<String> getDonation(String donationId);

	ResponseEntity<String> getAllDonation(String token) throws UserNotFoundException;

	ResponseEntity<String> updateDonation(Donation donation);

}
