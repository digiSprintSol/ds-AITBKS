package com.digisprint.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digisprint.bean.Feedback;
import com.digisprint.repository.FeedbackRepository;
import com.digisprint.service.FeedBackService;

@Service
public class FeedBackServiceImpl implements FeedBackService {

	@Autowired
	FeedbackRepository feedbackRepository;

	@Override
	public ResponseEntity<String> createFeedBack(Feedback feedback) {
		return new ResponseEntity(feedbackRepository.save(feedback), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> updateFeedBack(Feedback feedback) {
		return new ResponseEntity(feedbackRepository.save(feedback), HttpStatus.OK);
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
	public ResponseEntity<String> deleteFeedBack(String feedBackId) {
		Optional<Feedback> optionalFeedBack = feedbackRepository.findById(feedBackId);
		if (optionalFeedBack.isPresent()) {
			return new ResponseEntity(optionalFeedBack.get(), HttpStatus.OK);
		}
		return new ResponseEntity("FeedBack not found", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> getAllFeedBacks() {
		List<Feedback> feedBacks = feedbackRepository.findAll();
		if (feedBacks.size() > 0) {
			return new ResponseEntity(feedBacks, HttpStatus.OK);
		}
		return new ResponseEntity("No Data Found", HttpStatus.OK);
	}

}
