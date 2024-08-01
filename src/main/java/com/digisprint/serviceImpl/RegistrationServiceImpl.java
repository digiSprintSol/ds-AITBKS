package com.digisprint.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.EmailUtils.EmailService;
import com.digisprint.EmailUtils.EmailTemplates;
import com.digisprint.bean.PaymentInfo;
import com.digisprint.bean.ProgressBarReport;
import com.digisprint.bean.RegistrationFrom;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.PaymentRepository;
import com.digisprint.repository.ProgressBarRepository;
import com.digisprint.repository.RegistrationFromRepository;
import com.digisprint.service.RegistrationService;
import com.digisprint.utils.ApplicationConstants;
import com.digisprint.utils.EmailConstants;
import com.digisprint.utils.ErrorResponseConstants;
import com.digisprint.utils.GeneratingCredentials;
import com.digisprint.utils.RegistrationFormConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RegistrationServiceImpl  implements RegistrationService{

	private RegistrationFromRepository registrationFromRepository;

	private ProgressBarRepository progressBarRepository;

	private EmailService email;

	private EmailTemplates emailTemplates;

	private GeneratingCredentials generatingCredentials;
	
	private PaymentRepository paymentRepository;

	public RegistrationServiceImpl(RegistrationFromRepository registrationFromRepository,EmailService email,
			EmailTemplates emailTemplates, ProgressBarRepository progressBarRepository,
			GeneratingCredentials generatingCredentials,PaymentRepository paymentRepository) {
		super();
		this.registrationFromRepository = registrationFromRepository;
		this.email = email;
		this.emailTemplates = emailTemplates;
		this.progressBarRepository = progressBarRepository;
		this.generatingCredentials = generatingCredentials;
		this.paymentRepository = paymentRepository;
	}

	@Value("${spring.wrapper.uploadFiles}")
	public String UPLOAD_DIR;

	@Value("${spring.mail.username}")
	public String ADMIN_USERNAME;

	@Override
	public RegistrationFrom registerUser(RegistrationFrom registrationForm) {
		File vendorsFolder = new File(UPLOAD_DIR);

		if (!vendorsFolder.exists()) {
			vendorsFolder.mkdir();
		}

		String vendorFolderPath = UPLOAD_DIR + ApplicationConstants.DOUBLE_SLASH + registrationForm.getUserId();
		File vendorFolder = new File(vendorFolderPath);

		if (!vendorFolder.exists()) {
			vendorFolder.mkdir();
		}

		String body = emailTemplates.getWelcomeMailAfterFillingFirstRegistrationFrom()
				.replaceAll("[NAME]", registrationForm.getFullName());

		email.MailSendingService(ADMIN_USERNAME, registrationForm.getEmailAddress() , body, EmailConstants.REGISTRATOIN_1_EMAIL_SUBJECT);

		ProgressBarReport progressBarReport = new ProgressBarReport();
		progressBarReport.setUserId(registrationForm.getUserId());
		progressBarReport.setRegistrationOneFormCompleted(RegistrationFormConstants.TRUE);
		progressBarRepository.save(progressBarReport);

		registrationFromRepository.save(registrationForm);
		return registrationForm;
	}

	@Override
	public Page<RegistrationFrom> getAllRegisteredUsers(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return registrationFromRepository.findAll(pageable);
	}

	private String saveFileIfValid(MultipartFile file, String folderPath, RegistrationFrom user_from, String fileType,
			String formattedDate) throws IOException {
		if (!file.isEmpty()) {
			String originalFileName = file.getOriginalFilename();
			String extension = originalFileName.substring(originalFileName.lastIndexOf(ApplicationConstants.FULL_STOP));

			if (!extension.equalsIgnoreCase(ApplicationConstants.PDF)) {
				return originalFileName + ErrorResponseConstants.INVALID_FILE_TYPE;
			}

			String newFileName = user_from.getFullName() + ApplicationConstants.UNDERSCORE + fileType
					+ ApplicationConstants.UNDERSCORE
					+ formattedDate.replace(ApplicationConstants.COMMA, ApplicationConstants.HYPHEN) + extension;

			String filePath = folderPath + ApplicationConstants.DOUBLE_SLASH + newFileName;
			Path path = Paths.get(filePath);
			Files.write(path, file.getBytes());

			switch (fileType) {
			case ApplicationConstants.AADHAR_CARD:
				user_from.setAadharCard(newFileName);
				break;
			case ApplicationConstants.VOTERID_CARD:
				user_from.setVoterIdCard(newFileName);
				break;
			case ApplicationConstants.PROFILE_PIC:
				user_from.setProfilePic(newFileName);
				break;
			default:
				return ErrorResponseConstants.UNSUPPORTED_FILE_TYPE + fileType;
			}

			registrationFromRepository.save(user_from);
		}
		return null;
	}

	public JSONObject decodeToken(String jwtToken) {
		return JwtTokenUtil.decodeUserToken(jwtToken);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ResponseEntity upload(String userId, MultipartFile aadharCard, MultipartFile voterIdCard, MultipartFile profilePic,MultipartFile casteCertificate) throws Exception {
		RegistrationFrom userRegister = registrationFromRepository.findById(userId).orElseThrow(()-> new Exception(ErrorResponseConstants.USER_NOT_FOUND));

		if (userRegister != null) {
			String folderPath = UPLOAD_DIR + ApplicationConstants.REPLACE_WITH_BACKSLASH + userId;
			File folder = new File(folderPath);

			if (folder.exists()) {
				try {
					String strinFormateLocalDate = LocalDate.now().toString();

					String result;

					if ((result = saveFileIfValid(aadharCard, folderPath, userRegister, ApplicationConstants.AADHAR_CARD,strinFormateLocalDate)) != null)
						return new ResponseEntity(result,HttpStatus.OK);
					if ((result = saveFileIfValid(voterIdCard, folderPath, userRegister, ApplicationConstants.VOTERID_CARD,strinFormateLocalDate)) != null)
						return new ResponseEntity(result,HttpStatus.OK);
					if ((result = saveFileIfValid(profilePic, folderPath, userRegister, ApplicationConstants.PROFILE_PIC, strinFormateLocalDate)) != null)
						return new ResponseEntity(result,HttpStatus.OK);
					return new ResponseEntity(ApplicationConstants.FILE_UPLOADED_SUCCESSFULLY,HttpStatus.OK);
				} catch (IOException e) {
					log.error(ErrorResponseConstants.FAILED_TO_UPLOAD_FILE + e.getMessage());
					return new ResponseEntity(ErrorResponseConstants.FAILED_TO_UPLOAD_FILE,HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				log.error(ErrorResponseConstants.FOLDER_NOT_FOUND + userRegister);
				return new ResponseEntity(ErrorResponseConstants.FOLDER_NOT_FOUND + userRegister,HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity(ErrorResponseConstants.USER_NOT_FOUND + userRegister,HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public void committeePresidentAccountantApproval(String token, String phoneNumber, String statusOfApproval, String remarks, String membership) throws Exception {

		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			throw new IllegalArgumentException("Phone number cannot be null or empty");
		}
		if (statusOfApproval == null || statusOfApproval.isEmpty()) {
			throw new IllegalArgumentException("Status of approval cannot be null or empty");
		}

		JSONObject jsonObject = decodeToken(token);
		if (!jsonObject.has("id") || !jsonObject.has("type")) {
			throw new IllegalArgumentException("Token must contain 'id' and 'type' fields");
		}

		String identityNumber = jsonObject.getString("id");
		String userType = jsonObject.getString("type");

		RegistrationFrom specificUserDetails = registrationFromRepository.findByPhoneNumber(phoneNumber);
		if (specificUserDetails == null) {
			throw new IllegalArgumentException("No user found with the provided phone number");
		}

		Optional<ProgressBarReport> optionalProgressBarReport = progressBarRepository.findById(specificUserDetails.getUserId());
		if (!optionalProgressBarReport.isPresent()) {
			throw new IllegalArgumentException("No progress bar report found for the user");
		}

		ProgressBarReport progressBarReport = optionalProgressBarReport.get();

		if (userType.equalsIgnoreCase(ApplicationConstants.COMMITEE)) {
			if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE &&
					statusOfApproval.equals(RegistrationFormConstants.APPROVAL)) {
				progressBarReport.setCommitteeApproval(true);
				specificUserDetails.setCommitteeChoosenMembershipForApplicant(membership);
				specificUserDetails.setCommitteeRemarksForApplicant(remarks);

				// Sending credentials to the Applicant as Committee approved.
				String username = specificUserDetails.getEmailAddress();
				String passcode = generatingCredentials.generatePasscode(specificUserDetails.getCategory(), specificUserDetails.getMobileNumber());

				// paste these username and passcode in the Email to be sent.

			} else if (statusOfApproval.equalsIgnoreCase(RegistrationFormConstants.REJECTED)) {
				progressBarReport.setCommitteeApproval(false);

				// rejection email, and inform that can get lucky when president approves

			} else {
				progressBarReport.setCommitteeApproval(false);

				// waiting email
			}
		} else if (userType.equalsIgnoreCase(ApplicationConstants.PRESIDENT)) {
			if (specificUserDetails != null && progressBarReport != null && !progressBarReport.isCommitteeApproval()
					&& !progressBarReport.isPresidentApproval()
					&& progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
					&& statusOfApproval.equals(RegistrationFormConstants.APPROVAL)
					&& progressBarReport.isPresidentFillingRegistrationTwoForm() == true) {

				progressBarReport.setPresidentApproval(RegistrationFormConstants.TRUE);
				// R2 --> set membership for user , remarks
				specificUserDetails.setPresidentRemarksForApplicant(remarks);
				specificUserDetails.setPresidentChoosenMembershipForApplicant(membership);
				// mail regarding success

			} else if (statusOfApproval.equals(RegistrationFormConstants.REJECTED)) {
				progressBarReport.setPresidentApproval(RegistrationFormConstants.FALSE);
				// rejection mail from president

			} else {
				progressBarReport.setPresidentApproval(RegistrationFormConstants.FALSE);
				// waiting mail from president
			}

		} else if (userType.equalsIgnoreCase(ApplicationConstants.ACCOUNTANT)) {
			if (progressBarReport.isCommitteeApproval() && progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
					&& progressBarReport.isPayment() && progressBarReport.isPresidentApproval()
					&& progressBarReport.isRegistrationThreeFormCompleted() == RegistrationFormConstants.TRUE) {

				progressBarReport.setAccountantAcknowledgement(RegistrationFormConstants.TRUE);

				String memberIdentityNumber = generatingCredentials.generateMemberId();
				// send congratulations mail with generated memberID 

			} else {
				throw new IllegalArgumentException("All conditions for accountant approval are not met");
			}

		} else {
			throw new Exception("You don't have access !!");
		}

		registrationFromRepository.save(specificUserDetails);
		progressBarRepository.save(progressBarReport);

	}

	@Override
	public ProgressBarReport progressBarForAUser(String id) {
	
		return progressBarRepository.findById(id).get();
	}

	@Override
	public List<RegistrationFrom> committeePresidentAccountantViewListOfApplicants(String token) {
		
		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}
		
		JSONObject jsonObject = decodeToken(token);
		if (!jsonObject.has("id") || !jsonObject.has("type")) {
			throw new IllegalArgumentException("Token must contain 'id' and 'type' fields");
		}
		
		String userType = jsonObject.getString("type");
		
		if(userType.equalsIgnoreCase(ApplicationConstants.COMMITEE) || userType.equalsIgnoreCase(ApplicationConstants.PRESIDENT)) {
			return registrationFromRepository.findAll();
		}else if(userType.equalsIgnoreCase(ApplicationConstants.ACCOUNTANT)) {
			return registrationFromRepository.findAll().stream()
		            .filter(registrationForm -> registrationForm.getPaymentInfo() != null)
		            .collect(Collectors.toList());
		}else {
			throw new IllegalArgumentException("Invalid user type");
		}
		
	}

	@Override
	public RegistrationFrom presidentFillingRegistrationThreeForm(String token, String appicantId, String categoryOfMemberShipRecomendedByPresident, String remarks) {
		
		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}
		
		JSONObject jsonObject = decodeToken(token);
		if (!jsonObject.has("id") || !jsonObject.has("type")) {
			throw new IllegalArgumentException("Token must contain 'id' and 'type' fields");
		}
		
		String userType = jsonObject.getString("type");
		
		if(userType.equalsIgnoreCase(ApplicationConstants.PRESIDENT)) {
			RegistrationFrom form = registrationFromRepository.findById(appicantId).get();
			
			form.setPresidentChoosenMembershipForApplicant(categoryOfMemberShipRecomendedByPresident);
			form.setPresidentRemarksForApplicant(remarks);
			
			registrationFromRepository.save(form);
			
		}else {
			throw new IllegalArgumentException("Invalid access");
		}
		
		return null;
	}

	@Override
	public RegistrationFrom userFillingRegistrationThreeForm(String applicantId, boolean isMemberOfOtherCommunity
			, boolean isDecleration, String nativePlace) {
		
		RegistrationFrom specificUserDetails = registrationFromRepository.findById(applicantId).get();
		if (specificUserDetails == null) {
			throw new IllegalArgumentException("No user found with the provided phone number");
		}

		Optional<ProgressBarReport> optionalProgressBarReport = progressBarRepository.findById(applicantId);
		if (!optionalProgressBarReport.isPresent()) {
			throw new IllegalArgumentException("No progress bar report found for the user");
		}

		ProgressBarReport progressBarReport = optionalProgressBarReport.get();
		
		specificUserDetails.setNativePlace(nativePlace);
		specificUserDetails.setMemberOfOtherCommunity(isMemberOfOtherCommunity);
		specificUserDetails.setApplicationForMembershipDeclaration(isDecleration);
		
		registrationFromRepository.save(specificUserDetails);
		
		progressBarReport.setRegistrationThreeFormCompleted(RegistrationFormConstants.TRUE);
		progressBarRepository.save(progressBarReport);
		
		return specificUserDetails;
	}

	@Override
	public Page<PaymentInfo> accountFirstView(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return paymentRepository.findAll(pageable);
	}

	

}
