package com.digisprint.service;

import org.springframework.http.ResponseEntity;

import com.digisprint.bean.Feedback;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.requestBean.FeedbackRequest;

public interface FeedBackService {

	ResponseEntity<String> createFeedBack(String token,FeedbackRequest feedback) throws UserNotFoundException;

	ResponseEntity<String> updateFeedBack(String token,Feedback feedback)throws UserNotFoundException;

	ResponseEntity<String> getFeedBack(String feedBackId);

	ResponseEntity<String> deleteFeedBack(String token,String feedBackId) throws UserNotFoundException;

	ResponseEntity<String> getAllFeedBacks();

}
