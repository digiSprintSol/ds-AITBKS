package com.digisprint.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.RegistrationFrom;

public interface RegistrationService {

	RegistrationFrom registerUser(RegistrationFrom from);
	ResponseEntity upload(String userId, MultipartFile aadharCard, MultipartFile voterIdCard, MultipartFile profilePic, MultipartFile casteCertificate)throws Exception;
	ResponseEntity getAllRegisteredUsers();
}
