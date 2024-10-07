	package com.digisprint.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digisprint.bean.AccessBean;
import com.digisprint.bean.ScholarShip;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.ScholarShipRepository;
import com.digisprint.requestBean.ScholarShipRequest;
import com.digisprint.responseBody.ScholarShipResponse;
import com.digisprint.service.ScholarShipService;
import com.digisprint.utils.ApplicationConstants;
import com.digisprint.utils.ErrorResponseConstants;

@Service
public class ScholarShipServiceImpl implements ScholarShipService {

	@Autowired
	ScholarShipRepository scholarShipRepository;

	@Autowired
	AccessBeanRepository accessBeanRepository;

	@Autowired
	JwtTokenUtil jwtTokenUtil;

	public JSONObject decodeToken(String jwtToken) {
		return JwtTokenUtil.decodeUserToken(jwtToken);
	}

	@Override
	public ResponseEntity<String> saveScholarShip(ScholarShipRequest scholarShip, String token)
			throws UserNotFoundException {
		AccessBean accessBean = getTokenVerified(token);
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
	public ResponseEntity<String> getScholarShip(String scholarShipId, String token) throws UserNotFoundException {
		Optional<ScholarShip> optionalScholarShip = scholarShipRepository.findById(scholarShipId);
		if (optionalScholarShip.isPresent()) {
			ScholarShip scholarShipBean = optionalScholarShip.get();
			ScholarShipResponse scholarShipResponse = new ScholarShipResponse();
			BeanUtils.copyProperties(scholarShipBean, scholarShipResponse);
			return new ResponseEntity(scholarShipResponse, HttpStatus.OK);
		}
		return new ResponseEntity("No Data Found", HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<String> updateScholarShip(ScholarShip scholarShip, String token)
			throws UserNotFoundException {
		AccessBean accessBean = getTokenVerified(token);
		if (scholarShipRepository.findById(scholarShip.getId()).isPresent()) {
			ScholarShip scholarShipBean = scholarShipRepository.save(scholarShip);
			ScholarShipResponse scholarShipResponse = new ScholarShipResponse();
			BeanUtils.copyProperties(scholarShipBean, scholarShipResponse);
			return new ResponseEntity(scholarShipResponse, HttpStatus.OK);
		}
		return new ResponseEntity("No Data Found", HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<String> deleteScholarShip(String scholarShipId, String token) throws UserNotFoundException {
		AccessBean accessBean = getTokenVerified(token);
		Optional<ScholarShip> optionalScholarShip = scholarShipRepository.findById(scholarShipId);
		if (optionalScholarShip.isPresent()) {
			ScholarShip scholarShipBean = optionalScholarShip.get();
			scholarShipBean.setDeleted(true);
			scholarShipRepository.save(scholarShipBean);
		}
		return new ResponseEntity("Data Deleted successfully", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> getAllScholarShips(String token) throws UserNotFoundException {
		List<ScholarShip> scholarShipLists = scholarShipRepository.findAll();
		List<ScholarShipResponse> scholarShipResponses = new ArrayList<>();
		if (scholarShipLists.size() > 0) {
			scholarShipLists.forEach(scholarShip -> {
				ScholarShipResponse scholarShipResponse = new ScholarShipResponse();
				BeanUtils.copyProperties(scholarShip, scholarShipResponse);
				scholarShipResponses.add(scholarShipResponse);
			});
			return new ResponseEntity(scholarShipResponses, HttpStatus.OK);
		}
		return new ResponseEntity("No Data Found", HttpStatus.NOT_FOUND);
	}

	private AccessBean getTokenVerified(String token) throws UserNotFoundException {
		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}
		JSONObject jsonObject = decodeToken(token);
		if (!jsonObject.has("userId") || !jsonObject.has("access")) {
			throw new IllegalArgumentException("Token must contain userId and access fields");
		}
		String identityNumber = jsonObject.getString("userId");
		List accessList = jwtTokenUtil.getAccessList(token);
		AccessBean accessBeanUser = new AccessBean();
		if (accessList.contains(ApplicationConstants.PRESIDENT) || accessList.contains(ApplicationConstants.COMMITTEE_EXECUTIVE) || accessList.contains(ApplicationConstants.ADMIN)) {
			accessBeanUser = accessBeanRepository.findById(identityNumber).get();
			return accessBeanUser;			
		}
		throw new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND);
	}
}
