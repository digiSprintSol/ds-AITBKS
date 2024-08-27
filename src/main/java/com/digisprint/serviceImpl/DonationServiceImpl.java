package com.digisprint.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digisprint.bean.AccessBean;
import com.digisprint.bean.Donation;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.DonationRepository;
import com.digisprint.service.DonationService;
import com.digisprint.utils.ApplicationConstants;
import com.digisprint.utils.ErrorResponseConstants;

@Service
public class DonationServiceImpl implements DonationService {


	private DonationRepository donationRepository;

	private AccessBeanRepository accessBeanRepository;

	private JwtTokenUtil jwtTokenUtil;

	public DonationServiceImpl(DonationRepository donationRepository, AccessBeanRepository accessBeanRepository,
			JwtTokenUtil jwtTokenUtil) {
		super();
		this.donationRepository = donationRepository;
		this.accessBeanRepository = accessBeanRepository;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@Override
	public ResponseEntity<String> createDonation(Donation donation) {
		//this method not working check
		return new ResponseEntity(this.donationRepository.save(donation), HttpStatus.OK);
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
	public ResponseEntity<String> getAllDonation(String token) throws UserNotFoundException {
		if(token!=null) {
			AccessBean accountantUser = getTokenVerified(token);
			List<Donation> donations = donationRepository.findAll();
			if (donations.size() > 0) {
				return new ResponseEntity(donations, HttpStatus.OK);
			}
			else {
				return new ResponseEntity("Token cannot be null", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		else {
			return new ResponseEntity(ErrorResponseConstants.ERROR_RESPONSE_FOR_WRONG_TOKEN,HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> updateDonation(Donation donation) {
		return new ResponseEntity(donationRepository.save(donation), HttpStatus.OK);
	}

	private AccessBean getTokenVerified(String token) throws UserNotFoundException {
		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}

		JSONObject jsonObject = decodeToken(token);
		if (!jsonObject.has("userId") || !jsonObject.has("access")) {
			throw new IllegalArgumentException("Token must contain userId and access fields");
		}
		String identityNumber = jsonObject.getString("userId");
		List accessList = jwtTokenUtil.getAccessList(token);
		AccessBean accessBeanUser = new AccessBean();
		if(accessList.contains(ApplicationConstants.ACCOUNTANT)) {
			accessBeanUser= accessBeanRepository.findById(identityNumber)
					.orElseThrow(()->new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND));
		}
		return accessBeanUser;
	}

	public JSONObject decodeToken(String jwtToken) {
		return JwtTokenUtil.decodeUserToken(jwtToken);
	}
}
