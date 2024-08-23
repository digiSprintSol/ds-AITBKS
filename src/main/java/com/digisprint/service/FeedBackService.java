package com.digisprint.service;

import org.springframework.http.ResponseEntity;

import com.digisprint.bean.Feedback;

public interface FeedBackService {

	ResponseEntity<String> createFeedBack(Feedback feedback);

	ResponseEntity<String> updateFeedBack(Feedback feedback);

	ResponseEntity<String> getFeedBack(String feedBackId);

	ResponseEntity<String> deleteFeedBack(String feedBackId);

	ResponseEntity<String> getAllFeedBacks();

}
