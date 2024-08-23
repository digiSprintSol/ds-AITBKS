package com.digisprint.service;

import org.springframework.http.ResponseEntity;

import com.digisprint.bean.Feedback;
import com.digisprint.exception.UserNotFoundException;

public interface FeedBackService {

	ResponseEntity<String> createFeedBack(String token,Feedback feedback) throws UserNotFoundException;

	ResponseEntity<String> updateFeedBack(String token,Feedback feedback)throws UserNotFoundException;

	ResponseEntity<String> getFeedBack(String feedBackId);

	ResponseEntity<String> deleteFeedBack(String token,String feedBackId) throws UserNotFoundException;

	ResponseEntity<String> getAllFeedBacks();

}
