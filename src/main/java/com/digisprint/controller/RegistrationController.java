package com.digisprint.controller;

import org.springframework.data.domain.Page;
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

import com.digisprint.bean.ProgressBarReport;
import com.digisprint.bean.RegistrationFrom;
import com.digisprint.service.RegistrationService;
import com.digisprint.utils.ApplicationConstants;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(ApplicationConstants.USER_API)
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
	public ResponseEntity uploadFile(@PathVariable String userId, @RequestParam(name="aadharCard",required =false) MultipartFile aadharCard,
			@RequestParam(name="voterIdCard",required =false) MultipartFile voterIdCard,
			@RequestParam(name="profilePic",required =false) MultipartFile profilePic,
			@RequestParam(name="casteCertificate",required =false) MultipartFile casteCertificate ) throws Exception {
		return registrationService.upload(userId, aadharCard, voterIdCard, profilePic , casteCertificate);
	} 
	
	@Operation(summary= "This method is used to get all users ")
	@GetMapping(value="/getAllUsers")
	public Page<RegistrationFrom> getAllRegisteredUsers( @RequestParam(defaultValue = "0") int page,
			 @RequestParam(defaultValue = "10") int size) {
		return registrationService.getAllRegisteredUsers(page,size);
	}
	
	@Operation(summary="")
	@PostMapping(value="/approval/{token}/{phoneNumber}/{statusOfApproval}")
	public void committeePresidentAccountantApproval(@RequestParam(value="token") String token,
			@RequestParam(value="phoneNumber") String phoneNumber,@RequestParam(value="statusOfApproval") String statusOfApproval) throws Exception {
		 registrationService.committeePresidentAccountantApproval(token, phoneNumber, statusOfApproval);
	}
	
	@Operation(summary="")
	@PostMapping(value="/progressBar/{id}")
	public ProgressBarReport progressBarForAUser(@RequestParam(value="id") String id) {
		return registrationService.progressBarForAUser(id);
	}
	
	@Operation(summary="")
	@PostMapping(value="/registrationThreeForm/{applicantId}/{isMemberOfOtherCommunity}/{isDecleration}/{nativePlace}")
	public RegistrationFrom userFillingRegistrationThreeForm(@RequestParam(value="applicantId") String applicantId,
			@RequestParam(value="isMemberOfOtherCommunity") boolean isMemberOfOtherCommunity
			,@RequestParam(value="isDecleration") boolean isDecleration,@RequestParam(value="nativePlace") String nativePlace) {
		return registrationService.userFillingRegistrationThreeForm(applicantId, isMemberOfOtherCommunity, isDecleration, nativePlace);
	}
	
}
