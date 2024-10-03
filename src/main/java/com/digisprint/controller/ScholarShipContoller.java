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
import org.springframework.web.bind.annotation.RestController;

import com.digisprint.bean.ScholarShip;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.requestBean.ScholarShipRequest;
import com.digisprint.service.ScholarShipService;
import com.digisprint.utils.ApplicationConstants;

@RestController
public class ScholarShipContoller {

	@Autowired
	ScholarShipService scholarShipService;

	@Autowired
	private HttpServletRequest request;

	public String getToken() {
		String requestHeaders = request.getHeader(ApplicationConstants.TOKEN);
		String token = requestHeaders.substring(7); // Remove "Bearer " prefix
		return token;
	}

	@PostMapping(value = "/saveScholarShip")
	public ResponseEntity<String> saveScholarShip(@RequestBody ScholarShipRequest scholarShip) throws UserNotFoundException {
		return scholarShipService.saveScholarShip(scholarShip, getToken());
	}

	@GetMapping(value = "/getScholarShip/{scholarShipId}")
	public ResponseEntity<String> getScholarShip(@PathVariable String scholarShipId) throws UserNotFoundException {
		return scholarShipService.getScholarShip(scholarShipId, getToken());
	}

	@GetMapping(value = "/getAllScholarShips")
	public ResponseEntity<String> getAllScholarShips() throws UserNotFoundException {
		return scholarShipService.getAllScholarShips(getToken());
	}

	@PutMapping(value = "/updateScholarShip")
	public ResponseEntity<String> updateScholarShip(@RequestBody ScholarShip scholarShip) throws UserNotFoundException {
		return scholarShipService.updateScholarShip(scholarShip, getToken());
	}

	@DeleteMapping(value = "/deleteScholarShip/{scholarShipId}")
	public ResponseEntity<String> deleteScholarShip(@PathVariable String scholarShipId) throws UserNotFoundException {
		return scholarShipService.deleteScholarShip(scholarShipId, getToken());
	}

}
