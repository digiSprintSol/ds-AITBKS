package com.digisprint.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.RegistrationFrom;
import com.digisprint.service.RegistrationService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class RegistrationController {

     private RegistrationService registrationService;
	
	public RegistrationController(RegistrationService registrationService) {
		super();
		this.registrationService = registrationService;
	}

	@Operation(summary="This method is used for 1st level of Registration")
	@PostMapping("/register")
	public RegistrationFrom registerUser(@Validated @RequestBody RegistrationFrom from) {
		
		return this.registrationService.registerUser(from) ;
	}

	@Operation(summary = "This method is used to upload files for user, aadhar, voter id and caste certificate and profile")
	@PostMapping(value = "/uploadByUserId/{userId}", consumes = { "multipart/form-data" })
	public String uploadFile(@PathVariable String userId, @RequestParam("aadharCard") MultipartFile aadharCard,
			@RequestParam("voterIdCard") MultipartFile voterIdCard,
			@RequestParam("profilePic") MultipartFile profilePic,
			@RequestParam("casteCertificate") MultipartFile casteCertificate ) throws Exception {
		return registrationService.upload(userId, aadharCard, voterIdCard, profilePic , casteCertificate);
	} 
	
	@Operation(summary= "This method is used to get all users ")
	@GetMapping(value="/getAllUsers")
	public ResponseEntity getAllRegisteredUsers() {
		return registrationService.getAllRegisteredUsers();
	}
}
