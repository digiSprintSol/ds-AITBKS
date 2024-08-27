package com.digisprint.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digisprint.bean.AccessBean;
import com.digisprint.bean.Feedback;
import com.digisprint.bean.RegistrationFrom;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.FeedbackRepository;
import com.digisprint.service.FeedBackService;
import com.digisprint.utils.ApplicationConstants;
import com.digisprint.utils.ErrorResponseConstants;

@Service
public class FeedBackServiceImpl implements FeedBackService {

	private FeedbackRepository feedbackRepository;

	private JwtTokenUtil jwtTokenUtil;

	private AccessBeanRepository accessBeanRepository;

	public FeedBackServiceImpl(FeedbackRepository feedbackRepository, JwtTokenUtil jwtTokenUtil) {
		super();
		this.feedbackRepository = feedbackRepository;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@Override
	public ResponseEntity<String> createFeedBack(String token,Feedback feedback) throws UserNotFoundException {

		if (token == null || token.isEmpty()) {
			AccessBean presidentUser = getTokenVerified(token); 
			feedback.setCreatedDate(LocalDateTime.now());
			feedback.setCreatedBy(presidentUser.getName());
			return new ResponseEntity(feedbackRepository.save(feedback), HttpStatus.OK);

		}
		else {
			return new ResponseEntity(ErrorResponseConstants.ERROR_RESPONSE_FOR_WRONG_TOKEN,HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> updateFeedBack(String token,Feedback feedback) throws UserNotFoundException {

		if(token!=null) {
			AccessBean presidentUser = getTokenVerified(token);
			//change the code for update add id in path and do findby and then update

			return new ResponseEntity(feedbackRepository.save(feedback), HttpStatus.OK);
		}
		else {
			return new ResponseEntity(ErrorResponseConstants.ERROR_RESPONSE_FOR_WRONG_TOKEN,HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getFeedBack(String feedBackId) {
		Optional<Feedback> optionalFeedBack = feedbackRepository.findById(feedBackId);
		if (optionalFeedBack.isPresent()) {
			return new ResponseEntity(optionalFeedBack.get(), HttpStatus.OK);
		}
		return new ResponseEntity("FeedBack not found", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> deleteFeedBack(String token,String feedBackId) throws UserNotFoundException {

		if(token!=null) {
			AccessBean presidentUser = getTokenVerified(token);
			//change the code for delete here do either soft or hard delete
			Optional<Feedback> optionalFeedBack = feedbackRepository.findById(feedBackId);
			if (optionalFeedBack.isPresent()) {
				return new ResponseEntity(optionalFeedBack.get(), HttpStatus.OK);
			}
			else {
				return new ResponseEntity("FeedBack not found", HttpStatus.OK);
			}
		}
		else {
			return new ResponseEntity(ErrorResponseConstants.ERROR_RESPONSE_FOR_WRONG_TOKEN,HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getAllFeedBacks() {
		List<Feedback> feedBacks = feedbackRepository.findAll();
		if (feedBacks.size() > 0) {
			return new ResponseEntity(feedBacks, HttpStatus.OK);
		}
		return new ResponseEntity("No Data Found", HttpStatus.OK);
	}

	public JSONObject decodeToken(String jwtToken) {
		return JwtTokenUtil.decodeUserToken(jwtToken);
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
		if(accessList.contains(ApplicationConstants.PRESIDENT)) {
			accessBeanUser= accessBeanRepository.findById(identityNumber)
					.orElseThrow(()->new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND));
		}
		return accessBeanUser;
	}

}
