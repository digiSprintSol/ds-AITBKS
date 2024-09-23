package com.digisprint.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.digisprint.bean.EmailUpload;
import com.digisprint.bean.ProgressBarReport;
import com.digisprint.bean.RegistrationForm;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.repository.ProgressBarRepository;
import com.digisprint.requestBean.ApprovalFrom;
import com.digisprint.requestBean.RegistrationFrom2;
import com.digisprint.requestBean.UploadPaymentReceipt;
import com.digisprint.requestBean.UserRequest;
import com.digisprint.service.RegistrationService;
import com.digisprint.utils.ApplicationConstants;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(ApplicationConstants.USER_API)
@CrossOrigin(origins = "*")
public class RegistrationController {

	private RegistrationService registrationService;

	public RegistrationController(RegistrationService registrationService) {
		super();
		this.registrationService = registrationService;
	}

	@Autowired
	ProgressBarRepository progressBarRepository;

	@Autowired
	private HttpServletRequest request;

	public String getToken() {
		String requestHeaders = request.getHeader(ApplicationConstants.TOKEN);
		String token = requestHeaders.substring(7); // Remove "Bearer " prefix
		return token;
	}

	@Operation(summary = "This method is used for 1st level of Registration")
	@PostMapping("/register")
	public RegistrationForm registerUser(@Validated @RequestBody RegistrationForm from)
			throws IOException, MessagingException {

		return this.registrationService.registerUser(from);
	}

	@Operation(summary = "This method is used to edit the existing user information")
	@PostMapping("/update")
	public ResponseEntity updateUser(@RequestBody UserRequest user, @PathVariable String userId) throws IOException, MessagingException {

		return this.registrationService.updateUser(user, userId);
	}

	@Operation(summary = "This method is used to get all users ")
	@GetMapping(value = "/getAllUsers")
	public ResponseEntity getAllRegisteredUsers() {
		return registrationService.getAllRegisteredUsers(getToken());
	}

	@Operation(summary = "This method is used for approval from various roles")
	@PostMapping(value = "/approval/{userId}")
	public ResponseEntity committeePresidentAccountantApproval(@PathVariable(value = "userId") String userId,
			@RequestBody ApprovalFrom approvalFrom) throws Exception {
		return registrationService.committeePresidentAccountantApproval(getToken(), userId, approvalFrom);
	}

	@Operation(summary = "This is used to see the progress bar")
	@GetMapping(value = "/progressBar")
	public ProgressBarReport progressBarForAUser() {
		return registrationService.progressBarForAUser(getToken());
	}

	@Operation(summary = "This method is used to save Registration from 3")
	@PostMapping(value = "/registrationThreeForm")
	public RegistrationForm userFillingRegistrationThreeForm(@RequestBody RegistrationFrom2 registrationFrom2) {
		return registrationService.userFillingRegistrationThreeForm(getToken(), registrationFrom2);
	}

	@Operation(summary = "This method is used to see account view for users")
	@GetMapping(value = "/accountantFirstView")
	public List<RegistrationForm> accountFirstView() {
		return registrationService.accountFirstView();
	}

	@Operation(summary = "This method is used to download documents of the user")
	@GetMapping(value = "/getIDOfUser")
	public ResponseEntity getDocuments() throws UserNotFoundException, MalformedURLException {
		return registrationService.getIDOfUser(getToken());

	}

	@Operation(summary = "This method is used to upload the payment receipt")
	@PostMapping(value = "/uploadTranscationReceipt")
	public ResponseEntity uploadTranscationRecepit(@RequestBody UploadPaymentReceipt uploadPaymentReceipt)
			throws IOException, MessagingException {
		return registrationService.uploadTranscationRecepit(getToken(), uploadPaymentReceipt);
	}

	@Operation(summary = "This method is used to get payment receipt")
	@GetMapping(value = "/downloadPaymentReceipt/{userId}")
	public ResponseEntity getPaymentReceipt(@PathVariable String userId) throws MalformedURLException {
		return registrationService.getPaymentReceipt(userId);
	}

	@Operation(summary = "This method is used to get payment receipt")
	@GetMapping(value = "/getSpecificUserDetails")
	public ResponseEntity getUserDetails() {
		return registrationService.getUserDetails(getToken());

	}

	@GetMapping("/membersForDropDown")
	public List<String> getMembers() {
		return registrationService.referenceOneDropdown();
	}

	@GetMapping("/BarReport")
	public ProgressBarReport barReport(@RequestParam String UserId) {
		return progressBarRepository.findById(UserId).get();
	}

	@GetMapping("/getFiltereedMembers")
	public ResponseEntity getTheMembers(@RequestParam String categoryOfMember) {
		return registrationService.getAllFilteredMembers(categoryOfMember);
	}

	@PostMapping("/bulkEmailUpload")
	public ResponseEntity bulkEmailUpload(@RequestBody EmailUpload emailUpload) throws IOException, MessagingException {
		return registrationService.bulkEmailUpload(emailUpload);
	}
}
