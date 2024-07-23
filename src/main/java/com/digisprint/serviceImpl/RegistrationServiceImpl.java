package com.digisprint.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.digisprint.bean.AccessBean;
import com.digisprint.bean.ProgressBarReport;
import com.digisprint.bean.RegistrationFrom;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.ProgressBarRepository;
import com.digisprint.repository.RegistrationFromRepository;
import com.digisprint.service.RegistrationService;
import com.digisprint.utils.ApplicationConstants;
import com.digisprint.utils.EmailConstants;
import com.digisprint.utils.ErrorResponseConstants;
import com.digisprint.utils.RegistrationFormConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RegistrationServiceImpl  implements RegistrationService{

	private RegistrationFromRepository registrationFromRepository;
	
	private ProgressBarRepository progressBarRepository;
	
	private EmailService email;
	
	private EmailTemplates emailTemplates;

	public RegistrationServiceImpl(RegistrationFromRepository registrationFromRepository,EmailService email,
			EmailTemplates emailTemplates, ProgressBarRepository progressBarRepository) {
		super();
		this.registrationFromRepository = registrationFromRepository;
		this.email = email;
		this.emailTemplates = emailTemplates;
		this.progressBarRepository = progressBarRepository;
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
	public void committeePresidentAccountantApproval(String token, String phoneNumber, String statusOfApproval) throws Exception {
	    try {
	        JSONObject jsonObject = decodeToken(token);
	        String identityNumber = jsonObject.getString("id");
	        String userType = jsonObject.getString("type");

	        RegistrationFrom specificUserDetails = registrationFromRepository.findByPhoneNumber(phoneNumber);
	        if (specificUserDetails == null) {
	            throw new UserNotFoundException("User with phone number " + phoneNumber + " not found.");
	        }

	        Optional<ProgressBarReport> optionalProgressBarReport = progressBarRepository.findById(specificUserDetails.getUserId());
	        if (!optionalProgressBarReport.isPresent()) {
	            throw new Exception("Progress bar report for user id " + specificUserDetails.getUserId() + " not found.");
	        }

	        ProgressBarReport progressBarReport = optionalProgressBarReport.get();

	        if (userType.equalsIgnoreCase(ApplicationConstants.COMMITEE)) {
	            if (progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE) {
	                if (statusOfApproval.equals("APPROVAL")) {
	                    progressBarReport.setCommitteeApproval(true);
	                    // send approval mail
	                } else {
	                    progressBarReport.setCommitteeApproval(false);
	                    // send disapproval mail
	                }
	            } else {
	                throw new Exception("Registration form one is not completed.");
	            }
	        } else if (userType.equalsIgnoreCase(ApplicationConstants.PRESIDENT)) {
	            if (progressBarReport.isCommitteeApproval() && progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE) {
	                progressBarReport.setPresidentApproval(RegistrationFormConstants.TRUE);
	                // send approval mail
	            } else {
	                progressBarReport.setPresidentApproval(RegistrationFormConstants.FALSE);
	                // send disapproval mail
	            }
	        } else if (userType.equalsIgnoreCase(ApplicationConstants.ACCOUNTANT)) {
	            if (progressBarReport.isCommitteeApproval() && progressBarReport.isRegistrationOneFormCompleted() == RegistrationFormConstants.TRUE
	                && progressBarReport.isPayment() && progressBarReport.isPresidentApproval()
	                && progressBarReport.isRegistrationThreeFormCompleted() == RegistrationFormConstants.TRUE) {
	                progressBarReport.setAccountantAcknowledgement(RegistrationFormConstants.TRUE);
	                // generate member id --> utils.generate member id
	                // send congratulations mail
	            } else {
	                throw new Exception("All required conditions are not met for accountant acknowledgment.");
	            }
	        } else {
	            throw new Exception("User type " + userType + " is not authorized for this operation.");
	        }
	        
	        progressBarRepository.save(progressBarReport);

	    } catch (JSONException e) {
	        e.printStackTrace();
	        throw new Exception("Error parsing token: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new Exception("Error in approval process: " + e.getMessage());
	    }
	}

	

}
