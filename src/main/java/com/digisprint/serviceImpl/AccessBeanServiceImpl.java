package com.digisprint.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digisprint.bean.AccessBean;
import com.digisprint.bean.UserResponse;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.service.AccessBeanService;
import com.digisprint.utils.ApplicationConstants;
import com.digisprint.utils.ErrorResponseConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccessBeanServiceImpl implements AccessBeanService{

	private AccessBeanRepository accessBeanRepository;

	public AccessBeanServiceImpl(AccessBeanRepository accessBeanRepository) {
		super();
		this.accessBeanRepository = accessBeanRepository;
	}

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${config.secretKey}")
	private  String secretKey;

	@Override
	public ResponseEntity saveInternalUsers(AccessBean accessBean) {

		if(accessBeanRepository.findByEmail(accessBean.getEmail()).isPresent()) {
			return new ResponseEntity(ErrorResponseConstants.EMAIL_ALREADY_EXISTS,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			accessBean.setDeleted(false);
			accessBeanRepository.save(accessBean);
			return new ResponseEntity(accessBean,HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity getAllInternalUsers() {

		List<AccessBean> getAllUsers = accessBeanRepository.findAll();
		if(getAllUsers.size()==0) {
			return new ResponseEntity(ErrorResponseConstants.NO_USERS_FOUND,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			getAllUsers	= getAllUsers.stream().filter(user -> user.isDeleted()==false).collect(Collectors.toList());
			return new ResponseEntity(getAllUsers,HttpStatus.OK);
		}

	}

	@Override
	public ResponseEntity fetchInternalUsersById(String id) {
		AccessBean internalUsers = new AccessBean();
		try {
			internalUsers = accessBeanRepository.findById(id).orElseThrow(()->new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND));
		} catch (Exception e) {
			return new ResponseEntity(ErrorResponseConstants.USER_NOT_FOUND,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity(internalUsers,HttpStatus.OK);

	}

	@Override
	public ResponseEntity softDeleteInternalUsers(String id) {
		AccessBean internalUsers = new AccessBean();

		try {
			internalUsers = accessBeanRepository.findById(id)
					.orElseThrow(()->new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND));
			internalUsers.setDeleted(true);
			accessBeanRepository.save(internalUsers);
		} catch (Exception e) {
			return new ResponseEntity(ErrorResponseConstants.USER_NOT_FOUND,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity(internalUsers,HttpStatus.OK);
	}		

	
	private List<String> getAccessList(AccessBean accessBean){
		
		List<String> accessList = new ArrayList();
		if(accessBean.isPresident()){
			accessList.add(ApplicationConstants.PRESIDENT);
		}
		if(accessBean.isAccountant()){
			accessList.add(ApplicationConstants.ACCOUNTANT);
		}
		if(accessBean.isCommitee()){
			accessList.add(ApplicationConstants.COMMITEE);
		}
		if(accessBean.isUser()){
			accessList.clear();
			accessList.add(ApplicationConstants.USER);
		}
		
		return accessList;

	} 

	@Override
	public String  login(String userName, String password) {
		AccessBean accessBean = accessBeanRepository.findByEmailAndPassword(userName, password);
	
		return jwtTokenUtil.generateToken(userName, userName, getAccessList(accessBean), password);
	}

	public  Claims decodeAndValidateToken(String token) {
		try {
			return Jwts.parser()
					.setSigningKey(secretKey)
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			log.error(ErrorResponseConstants.INVALID_TOKEN + e.getMessage());
			return null;
		}
	}

	@Override
	public ResponseEntity validateAndGenerateToken(String token) {

		AccessBean internalUsers = new AccessBean();
		UserResponse userresponse = new UserResponse();
		try {
			Claims claims = decodeAndValidateToken(token);

			if (claims == null) {
				throw new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND);
			}

			String userName = String.valueOf(claims.get(ApplicationConstants.USERNAME)).replace(ApplicationConstants.REPLACE_WITH_FORWARDSLASH, ApplicationConstants.EMPTY_QUOTATION_MARK).trim().toLowerCase();
			internalUsers = accessBeanRepository.findByEmail(userName).orElseThrow(()-> new  UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND));
			userresponse.setAccessId(internalUsers.getAccessId());
			userresponse.setName(internalUsers.getName());
			userresponse.setPresident(internalUsers.isPresident());
			userresponse.setCommitee(internalUsers.isCommitee());
			userresponse.setAccountant(internalUsers.isAccountant());
			userresponse.setUser(internalUsers.isUser());

			userresponse.setToken(jwtTokenUtil.generateToken(internalUsers.getName(), internalUsers.getAccessId(), getAccessList(internalUsers),
					String.valueOf(claims.get(ApplicationConstants.OID)).replace(ApplicationConstants.REPLACE_WITH_FORWARDSLASH, ApplicationConstants.EMPTY_QUOTATION_MARK).trim().toLowerCase()));
		} catch (Exception e) {
			return new ResponseEntity(ErrorResponseConstants.USER_NOT_FOUND,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (userresponse != null) {
			return new ResponseEntity(userresponse,HttpStatus.OK);
		}
		else {
			return new ResponseEntity(ErrorResponseConstants.USER_NOT_FOUND,HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}

