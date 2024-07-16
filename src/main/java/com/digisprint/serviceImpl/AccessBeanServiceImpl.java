package com.digisprint.serviceImpl;

import java.net.MalformedURLException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.digisprint.ApplicationConstants;
import com.digisprint.bean.AccessBean;
import com.digisprint.bean.UserResponse;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.service.AccessBeanService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
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
			return new ResponseEntity("Email Already exsists",HttpStatus.INTERNAL_SERVER_ERROR);
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
			return new ResponseEntity("No users found",HttpStatus.INTERNAL_SERVER_ERROR);
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
			internalUsers = accessBeanRepository.findById(id).orElseThrow(()->new Exception("User Not found"));
		} catch (Exception e) {
			return new ResponseEntity("User not found",HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity(internalUsers,HttpStatus.OK);

	}

	@Override
	public ResponseEntity softDeleteInternalUsers(String id) {
		AccessBean internalUsers = new AccessBean();

		try {
			internalUsers = accessBeanRepository.findById(id).orElseThrow(()->new Exception("User Not found"));
			internalUsers.setDeleted(true);
			accessBeanRepository.save(internalUsers);
		} catch (Exception e) {
			return new ResponseEntity("User not found",HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity(internalUsers,HttpStatus.OK);
	}		


	@Override
	public String  login(String userName, String password) {
		AccessBean accessBean = accessBeanRepository.findByEmailAndPassword(userName, password);
System.out.println(userName+password);
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
System.out.println("dfjsdfds"+accessList);
		return jwtTokenUtil.generateToken(userName, userName, accessList, password);
	}

	public  Claims decodeAndValidateToken(String token) {
		try {
			return Jwts.parser()
					.setSigningKey(secretKey)
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			System.err.println("Invalid token: " + e.getMessage());
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
				throw new UserNotFoundException("User not found");
			}

			String userName = String.valueOf(claims.get("userName")).replace('\"', ' ').trim().toLowerCase();
			internalUsers = accessBeanRepository.findByEmail(userName).orElseThrow(()-> new  Exception("user not found"));
			userresponse.setAccessId(internalUsers.getAccessId());
			userresponse.setName(internalUsers.getName());
			userresponse.setPresident(internalUsers.isPresident());
			userresponse.setCommitee(internalUsers.isCommitee());
			userresponse.setAccountant(internalUsers.isAccountant());
			userresponse.setUser(internalUsers.isUser());

			List<String> accessList = new ArrayList();
			if(internalUsers.isPresident()){
				accessList.add(ApplicationConstants.PRESIDENT);
			}
			if(internalUsers.isAccountant()){
				accessList.add(ApplicationConstants.ACCOUNTANT);
			}
			if(internalUsers.isCommitee()){
				accessList.add(ApplicationConstants.COMMITEE);
			}
			if(internalUsers.isUser()){
				accessList.clear();
				accessList.add(ApplicationConstants.USER);
			}

			userresponse.setToken(jwtTokenUtil.generateToken(internalUsers.getName(), internalUsers.getAccessId(), accessList,
					String.valueOf(claims.get("oid")).replace('\"', ' ').trim().toLowerCase()));
		} catch (Exception e) {
			return new ResponseEntity("User not found",HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (userresponse != null) {
			return new ResponseEntity(userresponse,HttpStatus.OK);
		}
		else {
			return new ResponseEntity("User not found",HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}

