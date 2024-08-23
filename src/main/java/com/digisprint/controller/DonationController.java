package com.digisprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digisprint.bean.Donation;
import com.digisprint.service.DonationService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class DonationController {

	@Autowired
	DonationService donationService;

	@PostMapping(value = "/createDonation")
	public ResponseEntity<String> DonationController(@RequestBody Donation donation) {
		return donationService.createDonation(donation);
	}

	@PutMapping(value = "/updateDonation")
	public ResponseEntity<String> updateDonation(@RequestBody Donation donation) {
		return donationService.updateDonation(donation);
	}

	@GetMapping(value = "/getDonation/{donationId}")
	public ResponseEntity<String> getDonation(@PathVariable String donationId) {
		return donationService.getDonation(donationId);
	}

	@GetMapping(value = "/getAllDonation")
	public ResponseEntity<String> getAllDonation() {
		return donationService.getAllDonation();
	}

}
