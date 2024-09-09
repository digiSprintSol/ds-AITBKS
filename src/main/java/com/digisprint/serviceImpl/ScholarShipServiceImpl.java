package com.digisprint.serviceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digisprint.bean.ScholarShip;
import com.digisprint.repository.ScholarShipRepository;
import com.digisprint.requestBean.ScholarShipRequest;
import com.digisprint.responseBody.ScholarShipResponse;
import com.digisprint.service.ScholarShipService;

@Service
public class ScholarShipServiceImpl implements ScholarShipService {

	@Autowired
	ScholarShipRepository scholarShipRepository;

	@Override
	public ResponseEntity<String> saveScholarShip(ScholarShipRequest scholarShip) {
		ScholarShip scholarShipEntity = new ScholarShip();
		BeanUtils.copyProperties(scholarShip, scholarShipEntity);
		scholarShipEntity.setCreatedDate(LocalDateTime.now());
		scholarShipEntity.setModifiedDate(LocalDateTime.now());
		ScholarShip scholarShipBean = scholarShipRepository.save(scholarShipEntity);
		ScholarShipResponse scholarShipResponse = new ScholarShipResponse();
		BeanUtils.copyProperties(scholarShipBean, scholarShipResponse);
		return new ResponseEntity(scholarShipResponse, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> getScholarShip(String scholarShipId) {
       Optional<ScholarShip> optionalScholarShip = scholarShipRepository.findById(scholarShipId);
		if (optionalScholarShip.isPresent()) {
			ScholarShip scholarShipBean = optionalScholarShip.get();
			ScholarShipResponse scholarShipResponse = new ScholarShipResponse();
			BeanUtils.copyProperties(scholarShipBean, scholarShipResponse);
			return new ResponseEntity(scholarShipResponse, HttpStatus.OK);
		}
		return new ResponseEntity("No Data Found", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> updateScholarShip(ScholarShip scholarShip) {
		if (scholarShipRepository.findById(scholarShip.getId()).isPresent()) {
			ScholarShip scholarShipBean = scholarShipRepository.save(scholarShip);
			ScholarShipResponse scholarShipResponse = new ScholarShipResponse();
			BeanUtils.copyProperties(scholarShipBean, scholarShipResponse);
			return new ResponseEntity(scholarShipResponse, HttpStatus.OK);
		}
		return new ResponseEntity("No Data Found", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> deleteScholarShip(String scholarShipId) {
		Optional<ScholarShip> optionalScholarShip = scholarShipRepository.findById(scholarShipId);
		if (optionalScholarShip.isPresent()) {
			ScholarShip scholarShipBean = optionalScholarShip.get();
			scholarShipBean.setDeleted(true);
			scholarShipRepository.save(scholarShipBean);
		}
		return new ResponseEntity("Data Saved successfully", HttpStatus.OK);
	}
}
