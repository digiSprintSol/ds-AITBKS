package com.digisprint.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.PaymentInfo;
import com.digisprint.bean.ProgressBarReport;
import com.digisprint.bean.RegistrationFrom;
import com.digisprint.exception.UserNotFoundException;

public interface RegistrationService {

	RegistrationFrom registerUser(RegistrationFrom registrationForm);
	
	ResponseEntity upload(String userId, MultipartFile aadharCard, MultipartFile voterIdCard, MultipartFile profilePic, MultipartFile casteCertificate)throws Exception;
		
	Page<RegistrationFrom> getAllRegisteredUsers(int page, int size);
	
	void committeePresidentAccountantApproval(String token, String phoneNumber, String statusOfApproval, String remarks, String membership) throws UserNotFoundException, Exception;
	
	ProgressBarReport progressBarForAUser(String id);
	
	List<RegistrationFrom> committeePresidentAccountantViewListOfApplicants(String token);
	
	RegistrationFrom presidentFillingRegistrationThreeForm(String token, String appicantId, String categoryOfMemberShipRecomendedByPresident, String remarks);
	
	RegistrationFrom userFillingRegistrationThreeForm(String applicantId, boolean isMemberOfOtherCommunity, boolean isDecleration, String nativePlace);
	
	Page<PaymentInfo> accountFirstView(int page, int size);
	
}
