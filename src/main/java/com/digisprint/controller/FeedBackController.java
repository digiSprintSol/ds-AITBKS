package com.digisprint.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digisprint.bean.Feedback;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.requestBean.FeedbackRequest;
import com.digisprint.service.FeedBackService;
import com.digisprint.utils.ApplicationConstants;

@RestController
@RequestMapping("/feedback")
public class FeedBackController {

	@Autowired
	FeedBackService feedBackService;

	@Autowired
	private HttpServletRequest request;

	public String getToken() {
		String requestHeaders= request.getHeader(ApplicationConstants.TOKEN);	        
		String token = requestHeaders.substring(7); // Remove "Bearer " prefix
		return token;
	}
	
	@PostMapping(value = "/createFeedBack")
	public ResponseEntity<String> createFeedBack(@RequestBody FeedbackRequest feedback) throws UserNotFoundException {
		return feedBackService.createFeedBack(getToken(),feedback);
	}

	@PutMapping(value = "/updateFeedBack/{feedBackId}")
	public ResponseEntity<String> updateFeedBack(@RequestBody Feedback feedback) throws UserNotFoundException {
		return feedBackService.updateFeedBack(getToken(),feedback);
	}

	@GetMapping(value = "/getFeedBack/{feedBackId}")
	public ResponseEntity<String> getFeedBacks(@PathVariable String feedBackId) {
		return feedBackService.getFeedBack(feedBackId);
	}

	@GetMapping(value = "/getAllFeedBacks")
	public ResponseEntity<String> getAllFeedBacks() {
		return feedBackService.getAllFeedBacks();
	}

	@DeleteMapping(value = "/deleteFeedBack/{feedBackId}")
	public ResponseEntity<String> deleteFeedBack(@PathVariable String feedBackId) throws UserNotFoundException {
		return feedBackService.deleteFeedBack(getToken(),feedBackId);
	}

}
