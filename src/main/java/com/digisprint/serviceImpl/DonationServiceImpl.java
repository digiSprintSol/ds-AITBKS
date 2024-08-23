package com.digisprint.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digisprint.bean.Donation;
import com.digisprint.repository.DonationRepository;
import com.digisprint.service.DonationService;

@Service
public class DonationServiceImpl implements DonationService {

	@Autowired
	DonationRepository donationRepository;

	@Override
	public ResponseEntity<String> createDonation(Donation donation) {
		return new ResponseEntity(donationRepository.save(donation), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> getDonation(String donationId) {
		Optional<Donation> optionalDonation = donationRepository.findById(donationId);
		if (optionalDonation.isPresent()) {
			return new ResponseEntity(optionalDonation.get(), HttpStatus.OK);
		}
		return new ResponseEntity("Donation not found", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> getAllDonation() {
		List<Donation> donations = donationRepository.findAll();
		if (donations.size() > 0) {
			return new ResponseEntity(donations, HttpStatus.OK);
		}
		return new ResponseEntity("Data not found", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> updateDonation(Donation donation) {
		return new ResponseEntity(donationRepository.save(donation), HttpStatus.OK);
	}

}
