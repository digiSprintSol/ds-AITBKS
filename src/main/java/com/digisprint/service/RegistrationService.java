package com.digisprint.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.PaymentInfo;
import com.digisprint.bean.ProgressBarReport;
import com.digisprint.bean.RegistrationFrom;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.requestBean.ApprovalFrom;
import com.digisprint.requestBean.RegistrationFrom2;

public interface RegistrationService {

	RegistrationFrom registerUser(RegistrationFrom registrationForm,String imageUrl) throws IOException, MessagingException;
	
	ResponseEntity getAllRegisteredUsers(String token);
	
	ResponseEntity committeePresidentAccountantApproval(String token, String userId,ApprovalFrom approvalFrom) throws UserNotFoundException, Exception;
	
	ProgressBarReport progressBarForAUser(String id);
	
	List<RegistrationFrom> committeePresidentAccountantViewListOfApplicants(String token);
	
	List<RegistrationFrom> accountFirstView();

	ResponseEntity getIDOfUser(String token) throws MalformedURLException, UserNotFoundException;

	ResponseEntity uploadTranscationRecepit(String token, String imageUrl, String transcationId) throws IOException, MessagingException;

	RegistrationFrom userFillingRegistrationThreeForm(String token, RegistrationFrom2 registrationFrom2);

	ResponseEntity getPaymentReceipt(String userId) throws MalformedURLException;

	ResponseEntity getUserDetails(String token);
	
	List<String> referenceOneDropdown();
	
	ResponseEntity bulkEmailUpload(String toEmail, String subject, String body)throws IOException, MessagingException;
	
	ResponseEntity getAllFilteredMembers(String categoryOfMember);

}
