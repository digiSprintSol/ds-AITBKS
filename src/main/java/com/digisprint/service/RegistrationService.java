package com.digisprint.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.RegistrationFrom;
import com.digisprint.exception.UserNotFoundException;

public interface RegistrationService {

	RegistrationFrom registerUser(RegistrationFrom registrationForm);
	
	ResponseEntity upload(String userId, MultipartFile aadharCard, MultipartFile voterIdCard, MultipartFile profilePic, MultipartFile casteCertificate)throws Exception;
		
	Page<RegistrationFrom> getAllRegisteredUsers(int page, int size);
	
	void committeePresidentAccountantApproval(String token, String phoneNumber, String statusOfApproval) throws UserNotFoundException, Exception;
	
}
