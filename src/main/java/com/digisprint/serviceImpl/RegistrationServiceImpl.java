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

import com.digisprint.bean.AccessBean;
import com.digisprint.bean.RegistrationFrom;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.RegistrationFromRepository;
import com.digisprint.service.RegistrationService;

@Service
public class RegistrationServiceImpl  implements RegistrationService{

	private RegistrationFromRepository registrationFromRepository;

	public RegistrationServiceImpl(RegistrationFromRepository registrationFromRepository) {
		super();
		this.registrationFromRepository = registrationFromRepository;
	}

	@Value("${spring.wrapper.uploadFiles}")
	public String UPLOAD_DIR;
	
	@Override
	public RegistrationFrom registerUser(RegistrationFrom from) {
		File vendorsFolder = new File(UPLOAD_DIR);

		if (!vendorsFolder.exists()) {
			vendorsFolder.mkdir();
		}

		String vendorFolderPath = UPLOAD_DIR + "\\" + from.getUserId();
		File vendorFolder = new File(vendorFolderPath);

		if (!vendorFolder.exists()) {
			vendorFolder.mkdir();
		}
		 registrationFromRepository.save(from);
		 return from;
	}

	@Override
	public ResponseEntity getAllRegisteredUsers() {
		 List<RegistrationFrom> registrationFromList= registrationFromRepository.findAll();
		 if(registrationFromList ==null) {
			 return new ResponseEntity("No users registered",HttpStatus.INTERNAL_SERVER_ERROR);
		 }
		 else {
			 return new ResponseEntity(registrationFromList,HttpStatus.OK);
		 }
	}
	


	private String saveFileIfValid(MultipartFile file, String folderPath, RegistrationFrom user_from, String fileType,
	        String formattedDate) throws IOException {
	    if (!file.isEmpty()) {
	        String originalFileName = file.getOriginalFilename();
	        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

//	        if (!extension.equalsIgnoreCase(WorkFlowConstants.PDF)) {
//	            return originalFileName + " This file type is invalid, Only PDF files are allowed.";
//	        }

	        String newFileName = user_from.getFullName() + "_" + fileType
	                + "_"
	                + formattedDate.replace(",", "-") + extension;

	        String filePath = folderPath + "\\" + newFileName;
	        Path path = Paths.get(filePath);
	        Files.write(path, file.getBytes());

	        switch (fileType) {
	        case "AADHARCARD":
	        	user_from.setAadharCard(newFileName);
	            break;
	        case "VOTERIDCARD":
	        	user_from.setVoterIdCard(newFileName);
	            break;
	        case "PROFILEPIC":
	        	user_from.setProfilePic(newFileName);
	            break;
	        default:
	            return "Unsupported file type: " + fileType;
	        }

	        registrationFromRepository.save(user_from);
	    }
	    return null;
	}

	@Override
	public String upload(String userId, MultipartFile aadharCard, MultipartFile voterIdCard, MultipartFile profilePic,MultipartFile casteCertificate) throws Exception {
		 RegistrationFrom userRegister = registrationFromRepository.findById(userId).orElseThrow(()-> new Exception("User id not found"));
		
		    if (userRegister != null) {
		        String folderPath = UPLOAD_DIR +"/" + userId;
		        File folder = new File(folderPath);

		        if (folder.exists()) {
		            try {
		                LocalDate localDate = LocalDate.now();
		                String strinFormateLocalDate = localDate.toString();

		                String result;

		                if ((result = saveFileIfValid(aadharCard, folderPath, userRegister, "AADHARCARD",strinFormateLocalDate)) != null)
		                    return result;
		                if ((result = saveFileIfValid(voterIdCard, folderPath, userRegister,"VOTERIDCARD",strinFormateLocalDate)) != null)
		                    return result;
		                if ((result = saveFileIfValid(casteCertificate, folderPath, userRegister, "CASTECERTIFICATE",strinFormateLocalDate)) != null)
		                    return result;
		                if ((result = saveFileIfValid(profilePic, folderPath, userRegister, "PROFILEPIC", strinFormateLocalDate)) != null)
		                    return result;
		                return "File uploaded successfully!";
		            } catch (IOException e) {
		                System.err.println("Failed to upload file: " + e.getMessage());
		                return "Failed to upload file!";
		            }
		        } else {
		            System.err.println("Vendor folder not found with ID: " + userRegister);
		            return "Vendor folder not found with ID: " + userRegister;
		        }
		    }

		    return "Vendor not found with ID: " + userRegister;
	}


	

}
