package com.digisprint.service;

import org.springframework.http.ResponseEntity;

import com.digisprint.bean.ScholarShip;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.requestBean.ScholarShipRequest;

public interface ScholarShipService {

	ResponseEntity<String> saveScholarShip(ScholarShipRequest scholarShip, String token) throws UserNotFoundException;

	ResponseEntity<String> getScholarShip(String scholarShipId, String token) throws UserNotFoundException;

	ResponseEntity<String> updateScholarShip(ScholarShip scholarShip, String token) throws UserNotFoundException;

	ResponseEntity<String> deleteScholarShip(String scholarShipId, String token) throws UserNotFoundException;

	ResponseEntity<String> getAllScholarShips(String token) throws UserNotFoundException;

}
