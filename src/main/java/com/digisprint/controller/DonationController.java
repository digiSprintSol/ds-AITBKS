package com.digisprint.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digisprint.bean.Donation;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.requestBean.DonationRequest;
import com.digisprint.service.DonationService;
import com.digisprint.utils.ApplicationConstants;

@RestController
@RequestMapping("/donations")
public class DonationController {

	@Autowired
	DonationService donationService;

	@Autowired
	private HttpServletRequest request;

	public String getToken() {
		String requestHeaders = request.getHeader(ApplicationConstants.TOKEN);
		String token = requestHeaders.substring(7); // Remove "Bearer " prefix
		return token;
	}

	@PostMapping(value = "/createDonation")
	public ResponseEntity<String> donationController(@RequestBody DonationRequest donation) {
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
	public ResponseEntity<String> getAllDonation() throws UserNotFoundException {
		return donationService.getAllDonation(getToken());
	}
	
	@PutMapping(value = "/donationAcknowledge/{donationId}")
	public ResponseEntity donationAcknowledge(@PathVariable String donationId) {
		return donationService.donationAcknowledge(donationId);
	}

	@GetMapping(value = "/getDonationsAcknowledged")
	public ResponseEntity<List<Donation>> getDonationsAcknowledged(){
		return donationService.getDonationsAcknowledged();
	}
}
