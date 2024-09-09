package com.digisprint.service;

import org.springframework.http.ResponseEntity;

import com.digisprint.bean.ScholarShip;
import com.digisprint.requestBean.ScholarShipRequest;

public interface ScholarShipService {

	ResponseEntity<String> saveScholarShip(ScholarShipRequest scholarShip);

	ResponseEntity<String> getScholarShip(String scholarShipId);

	ResponseEntity<String> updateScholarShip(ScholarShip scholarShip);

	ResponseEntity<String> deleteScholarShip(String scholarShipId);

}
