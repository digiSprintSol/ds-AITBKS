package com.digisprint.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.digisprint.bean.Feedback;
import com.digisprint.service.FeedBackService;

@RestController
public class FeedBackController {

	@Autowired
	FeedBackService feedBackService;

	@PostMapping(value = "/createFeedBack")
	public ResponseEntity<String> createFeedBack(@RequestBody Feedback feedback) {
		return feedBackService.createFeedBack(feedback);
	}

	@PutMapping(value = "/updateFeedBack")
	public ResponseEntity<String> updateFeedBack(@RequestBody Feedback feedback) {
		return feedBackService.updateFeedBack(feedback);
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
	public ResponseEntity<String> deleteFeedBack(@PathVariable String feedBackId) {
		return feedBackService.deleteFeedBack(feedBackId);
	}

}
