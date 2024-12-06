package com.digisprint.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digisprint.bean.AccessBean;
import com.digisprint.bean.Donation;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.DonationRepository;
import com.digisprint.requestBean.DonationRequest;
import com.digisprint.responseBody.DonationResponse;
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
	public ResponseEntity<String> createDonation(DonationRequest donation) {
		Donation donationEntity = new Donation();
		BeanUtils.copyProperties(donation, donationEntity);
		donationEntity.setAcknowledge(false);
		donationEntity.setCreatedDate(LocalDateTime.now());
		Donation response = this.donationRepository.save(donationEntity);
		DonationResponse donationResponse = new DonationResponse();
		BeanUtils.copyProperties(response, donationResponse);
		return new ResponseEntity(donationResponse, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> getDonation(String donationId) {
		Optional<Donation> optionalDonation = donationRepository.findById(donationId);
		if (optionalDonation.isPresent()) {
			DonationResponse donationResponse = new DonationResponse();
			BeanUtils.copyProperties(optionalDonation.get(), donationResponse);
			return new ResponseEntity(donationResponse, HttpStatus.OK);
		}
		return new ResponseEntity("Donation not found", HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<String> getAllDonation(String token) throws UserNotFoundException {
		if (token != null) {
			AccessBean accountantUser = getTokenVerified(token);
			List<Donation> donations = donationRepository.findAll();
			List<DonationResponse> donationResponses = new ArrayList<>();
			if (donations.size() > 0) {
				donations.stream().forEach((donationEntity)->{
					DonationResponse response = new DonationResponse();
					BeanUtils.copyProperties(donationEntity, response);
					donationResponses.add(response);
				});
				return new ResponseEntity(donationResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity("Data not found", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			return new ResponseEntity(ErrorResponseConstants.ERROR_RESPONSE_FOR_WRONG_TOKEN, HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> updateDonation(Donation donation) {
		if (donationRepository.findById(donation.getId()).isPresent()) {
			donation.setModifiedDate(LocalDateTime.now());
			Donation donationEntity= donationRepository.save(donation);
			DonationResponse donationResponse= new DonationResponse();
			BeanUtils.copyProperties(donationEntity, donationResponse);
			return new ResponseEntity(donationResponse, HttpStatus.OK);
		}
		return new ResponseEntity("Donation not found", HttpStatus.NOT_FOUND);
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
		if (accessList.contains(ApplicationConstants.ACCOUNTANT)) {
			accessBeanUser = accessBeanRepository.findById(identityNumber).get();
			return accessBeanUser;
					
		}
	    throw new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND);
	}

	public JSONObject decodeToken(String jwtToken) {
		return JwtTokenUtil.decodeUserToken(jwtToken);
	}

	@Override
	public ResponseEntity donationAcknowledge(String donationId) {
		
	    Optional<Donation> optionalDonation=donationRepository.findById(donationId);
	    if(optionalDonation.isPresent()) {
	    Donation donation=	optionalDonation.get();
	    donation.setAcknowledge(true);
	    donationRepository.save(donation);
	    return new ResponseEntity("Donation is Acknowledged",HttpStatus.OK);
	    }
	    return new ResponseEntity("Donation is not found",HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<List<Donation>> getDonationsAcknowledged() {
		List<Donation> acknowledgeDonations = donationRepository.findByAcknowledgeTrue();
		if(acknowledgeDonations.size() != 0) {
			return new ResponseEntity(acknowledgeDonations,HttpStatus.OK);
		}
		else {
		return new ResponseEntity("No Acknowledged Donations found",HttpStatus.NOT_FOUND);
		}
	}
}
