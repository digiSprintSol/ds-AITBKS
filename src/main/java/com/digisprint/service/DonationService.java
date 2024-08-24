package com.digisprint.service;

import org.springframework.http.ResponseEntity;

import com.digisprint.bean.Donation;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.requestBean.DonationRequest;

public interface DonationService {

	ResponseEntity<String> createDonation(DonationRequest donation);

	ResponseEntity<String> getDonation(String donationId);

	ResponseEntity<String> getAllDonation(String token) throws UserNotFoundException;

	ResponseEntity<String> updateDonation(Donation donation);

}
