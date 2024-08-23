package com.digisprint.service;

import org.springframework.http.ResponseEntity;

import com.digisprint.bean.Donation;

public interface DonationService {

	ResponseEntity<String> createDonation(Donation donation);

	ResponseEntity<String> getDonation(String donationId);

	ResponseEntity<String> getAllDonation();

	ResponseEntity<String> updateDonation(Donation donation);

}
