package com.digisprint.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.http.ResponseEntity;

import com.digisprint.bean.EmailUpload;
import com.digisprint.bean.ProgressBarReport;
import com.digisprint.bean.RegistrationForm;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.requestBean.ApprovalFrom;
import com.digisprint.requestBean.RegistrationFrom2;
import com.digisprint.requestBean.UploadPaymentReceipt;
import com.digisprint.requestBean.UserRequest;

public interface RegistrationService {

	RegistrationForm registerUser(RegistrationForm registrationForm) throws IOException, MessagingException;

	ResponseEntity getAllRegisteredUsers(String token);

	ResponseEntity committeePresidentAccountantApproval(String token, String userId, ApprovalFrom approvalFrom)
			throws UserNotFoundException, Exception;

	ProgressBarReport progressBarForAUser(String id);

	List<RegistrationForm> committeePresidentAccountantViewListOfApplicants(String token);

	List<RegistrationForm> accountFirstView();

	ResponseEntity getIDOfUser(String token) throws MalformedURLException, UserNotFoundException;

	RegistrationForm userFillingRegistrationThreeForm(String token, RegistrationFrom2 registrationFrom2);

	ResponseEntity getPaymentReceipt(String userId) throws MalformedURLException;

	ResponseEntity getUserDetails(String token);

	List<String> referenceOneDropdown();

	ResponseEntity bulkEmailUpload(EmailUpload emailUpload) throws IOException, MessagingException;

	ResponseEntity getAllFilteredMembers(String categoryOfMember);

	ResponseEntity uploadTranscationRecepit(String token, UploadPaymentReceipt uploadPaymentReceipt)
			throws IOException, MessagingException;

	ResponseEntity updateUser(UserRequest user, String token);

	String deleteUser(String userId);

}
