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

import com.digisprint.bean.ScholarShip;
import com.digisprint.requestBean.ScholarShipRequest;
import com.digisprint.service.ScholarShipService;

@RestController
public class ScholarShipContoller {
	
	@Autowired
	ScholarShipService scholarShipService;
	
	@PostMapping(value = "/saveScholarShip")
	public ResponseEntity<String> saveScholarShip(@RequestBody ScholarShipRequest scholarShip){ 
		return  scholarShipService.saveScholarShip(scholarShip);
	}
	
	@GetMapping(value = "/getScholarShip")
	public ResponseEntity<String> getScholarShip(@PathVariable String scholarShipId){ 
		return  scholarShipService.getScholarShip(scholarShipId);
	}
	
	@PutMapping(value = "/updateScholarShip")
	public ResponseEntity<String> updateScholarShip(@RequestBody ScholarShip scholarShip){ 
		return  scholarShipService.updateScholarShip(scholarShip);
	}
	
	@DeleteMapping(value = "/deleteScholarShip")
	public ResponseEntity<String> deleteScholarShip(@PathVariable String scholarShipId){ 
		return  scholarShipService.deleteScholarShip(scholarShipId);
	}
	

}
