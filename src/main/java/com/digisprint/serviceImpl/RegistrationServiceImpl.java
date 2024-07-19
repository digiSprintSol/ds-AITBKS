package com.digisprint.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.EmailUtils.EmailService;
import com.digisprint.EmailUtils.EmailTemplates;
import com.digisprint.bean.AccessBean;
import com.digisprint.bean.RegistrationFrom;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.RegistrationFromRepository;
import com.digisprint.service.RegistrationService;
import com.digisprint.utils.ApplicationConstants;
import com.digisprint.utils.EmailConstants;
import com.digisprint.utils.ErrorResponseConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RegistrationServiceImpl  implements RegistrationService{

	private RegistrationFromRepository registrationFromRepository;
	
	private EmailService email;
	
	private EmailTemplates emailTemplates;

	public RegistrationServiceImpl(RegistrationFromRepository registrationFromRepository,EmailService email,
			EmailTemplates emailTemplates) {
		super();
		this.registrationFromRepository = registrationFromRepository;
		this.email = email;
		this.emailTemplates = emailTemplates;
	}

	@Value("${spring.wrapper.uploadFiles}")
	public String UPLOAD_DIR;

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
		
		email.MailSendingService("allindiatelagabalijakapusangam@gmail.com", "sriramsphere@gmail.com", body, EmailConstants.REGISTRATOIN_1_EMAIL_SUBJECT);
		
		registrationFromRepository.save(registrationForm);
		return registrationForm;
	}

	@Override
	public ResponseEntity getAllRegisteredUsers() {
		List<RegistrationFrom> registrationFromList= registrationFromRepository.findAll();
		if(registrationFromList ==null) {
			return new ResponseEntity(ErrorResponseConstants.NO_USERS_FOUND,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			return new ResponseEntity(registrationFromList,HttpStatus.OK);
		}
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




}
