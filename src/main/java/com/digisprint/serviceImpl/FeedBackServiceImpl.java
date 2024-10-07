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
import com.digisprint.bean.Feedback;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.FeedbackRepository;
import com.digisprint.requestBean.FeedbackRequest;
import com.digisprint.responseBody.FeedbackResponse;
import com.digisprint.service.FeedBackService;
import com.digisprint.utils.ApplicationConstants;
import com.digisprint.utils.ErrorResponseConstants;

@Service
public class FeedBackServiceImpl implements FeedBackService {

	private FeedbackRepository feedbackRepository;

	private JwtTokenUtil jwtTokenUtil;

	private AccessBeanRepository accessBeanRepository;

	public FeedBackServiceImpl(FeedbackRepository feedbackRepository, JwtTokenUtil jwtTokenUtil,
			AccessBeanRepository accessBeanRepository) {
		super();
		this.feedbackRepository = feedbackRepository;
		this.jwtTokenUtil = jwtTokenUtil;
		this.accessBeanRepository = accessBeanRepository;
	}

	@Override
	public ResponseEntity<String> createFeedBack(String token, FeedbackRequest feedback) throws UserNotFoundException {
		if (token == null || token.isEmpty()) {
			return new ResponseEntity(ErrorResponseConstants.ERROR_RESPONSE_FOR_WRONG_TOKEN, HttpStatus.BAD_REQUEST);

		} else {
			AccessBean presidentUser = getTokenVerified(token);
			Feedback feedBackEntity = new Feedback();
			BeanUtils.copyProperties(feedback, feedBackEntity);
			feedBackEntity.setCreatedDate(LocalDateTime.now());
			feedBackEntity.setCreatedBy(presidentUser.getName());
			Feedback response= feedbackRepository.save(feedBackEntity);
			FeedbackResponse feedbackResponse = new FeedbackResponse();
			BeanUtils.copyProperties(response,feedbackResponse);
			return new ResponseEntity(feedbackResponse, HttpStatus.OK);
		}

	}

	@Override
	public ResponseEntity<String> updateFeedBack(String token, Feedback feedback) throws UserNotFoundException {

		if (token != null) {
			AccessBean presidentUser = getTokenVerified(token);
			// change the code for update add id in path and do findby and then update
			Optional<Feedback> optionalFeedBck = feedbackRepository.findById(feedback.getId());
			if (optionalFeedBck.isPresent()) {
				feedback.setModifiedDate(LocalDateTime.now());
				feedback.setModifiedBy(presidentUser.getName());
				Feedback response = feedbackRepository.save(feedback);
				FeedbackResponse feedbackResponse = new FeedbackResponse();
				BeanUtils.copyProperties(response, feedbackResponse);
				return new ResponseEntity(feedbackResponse, HttpStatus.OK);
			}
			return new ResponseEntity("Feedback not found", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(ErrorResponseConstants.ERROR_RESPONSE_FOR_WRONG_TOKEN, HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<String> getFeedBack(String feedBackId) {
		Optional<Feedback> optionalFeedBack = feedbackRepository.findById(feedBackId);
		if (optionalFeedBack.isPresent()) {
			FeedbackResponse feedbackResponse = new FeedbackResponse();
			BeanUtils.copyProperties(optionalFeedBack.get(), feedbackResponse);
			return new ResponseEntity(feedbackResponse, HttpStatus.OK);
		}
		return new ResponseEntity("FeedBack not found", HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<String> deleteFeedBack(String token, String feedBackId) throws UserNotFoundException {

		if (token != null) {
			AccessBean presidentUser = getTokenVerified(token);
			// change the code for delete here do either soft or hard delete
			Optional<Feedback> optionalFeedBack = feedbackRepository.findById(feedBackId);
			if (optionalFeedBack.isPresent()) {
				Feedback feedback = optionalFeedBack.get();
				feedback.setDeleted(true);
				feedback.setModifiedDate(LocalDateTime.now());
				feedback.setModifiedBy(presidentUser.getName());
				feedbackRepository.save(feedback);
				return new ResponseEntity("Feedback is deleted", HttpStatus.OK);
			} else {
				return new ResponseEntity("FeedBack not found", HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity(ErrorResponseConstants.ERROR_RESPONSE_FOR_WRONG_TOKEN, HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getAllFeedBacks() {
		List<Feedback> feedBacks = feedbackRepository.findAll();
		List<FeedbackResponse> feedbackResponses = new ArrayList<>();
		if (feedBacks.size() > 0) {
			feedBacks.stream().forEach((feedbackEntity) -> {
				FeedbackResponse feedbackResponse = new FeedbackResponse();
				BeanUtils.copyProperties(feedbackEntity, feedbackResponse);
				feedbackResponses.add(feedbackResponse);
			});
			return new ResponseEntity(feedbackResponses, HttpStatus.OK);
		}
		return new ResponseEntity("No Data Found", HttpStatus.NOT_FOUND);
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
		if (accessList.contains(ApplicationConstants.PRESIDENT) || accessList.contains(ApplicationConstants.COMMITTEE_EXECUTIVE) || accessList.contains(ApplicationConstants.ADMIN)) {
			accessBeanUser = accessBeanRepository.findById(identityNumber).get();
			return accessBeanUser;		
		}
		throw new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND);	
	}

}
