package com.digisprint.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digisprint.bean.AccessBean;
import com.digisprint.bean.CulturalEvents;
import com.digisprint.bean.MarketPlaces;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.requestBean.LoginPayload;
import com.digisprint.requestBean.UploadAnnouncement;
import com.digisprint.requestBean.UploadBean;
import com.digisprint.requestBean.VerifyEmail;
import com.digisprint.service.AccessBeanService;
import com.digisprint.utils.ApplicationConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name= ApplicationConstants.ROLE_MANAGEMENT)
@CrossOrigin
public class AccessBeanController {

	private AccessBeanService accessBeanService;

	public AccessBeanController(AccessBeanService accessBeanService) {
		super();
		this.accessBeanService = accessBeanService;
	}

	@Autowired
	private HttpServletRequest request;

	@GetMapping("/hello")
    public String getMessage()
    {
        return "Hello World!";
    }
	
	public String getToken() {
		String requestHeaders= request.getHeader(ApplicationConstants.TOKEN);	        
		String token = requestHeaders.substring(7); // Remove "Bearer " prefix
		return token;
	}

	@Operation(summary = "Method Used for LogIn")
	@PostMapping("/login")
	ResponseEntity login(@RequestBody LoginPayload login) {
		return accessBeanService.login(login.getUsername(), login.getPassword());
	}

	@Operation(summary = "Method is used for LogIn with Token")
	@PostMapping("/loginWithToken")
	ResponseEntity validateAndGenerateToken() {
		return accessBeanService.validateAndGenerateToken(getToken());
	}

	@Operation(summary="This method is used to save users")
	@PostMapping("/save")
	ResponseEntity saveInternalUsers(@Valid @RequestBody AccessBean accessBean) {
		return accessBeanService.saveInternalUsers(accessBean);	
	}

	@Operation(summary=" This method is used to get all the internal users")
	@GetMapping("/getAll")
	ResponseEntity getAllInternalUsers() {
		return accessBeanService.getAllInternalUsers();
	}

	@Operation(summary ="This method is used to get internalusers")
	@GetMapping("/fetchUsersById/{id}")
	ResponseEntity fetchInternalUsersById(@PathVariable("id") String id) {
		return accessBeanService.fetchInternalUsersById(id);
	}

	@Operation(summary = "This method is used to remove internal users")
	@DeleteMapping("/removeAccess/{id}")
	ResponseEntity softDeleteInternalUsers(@PathVariable("id") String id) {
		return accessBeanService.softDeleteInternalUsers(id);
	}

	@Operation(summary = "This method is used to post announcement")
	@PostMapping(value="/postingAnnouncements")
	public ResponseEntity postingAnnouncements(@RequestBody UploadAnnouncement uploadAnnouncement)	{
		return accessBeanService.postingAnnouncements(uploadAnnouncement.getTitle(),uploadAnnouncement.getDescription());
	}

	@Operation(summary = "This method is used to get all announcement")
	@GetMapping(value="/getAllAnnouncements")
	public ResponseEntity getAllAnnouncement () {
		return accessBeanService.getAllAnnouncement();
	}

//	@Operation(summary = "This method is used to get events")
//	@GetMapping(value="/getEvents")
//	public ResponseEntity getEvents() throws MalformedURLException {
//		return accessBeanService.getEvents();
//	}
	
	@Operation(summary="This method is used for posting market places")
	@PostMapping(value="/postMarketPlace")
	public ResponseEntity postMarketPlace( @RequestBody MarketPlaces marketPlaces) {
        try {
        	accessBeanService.postMarketPlace(getToken(), marketPlaces);
            return ResponseEntity.status(HttpStatus.CREATED).body("MarketPlace added successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while saving MarketPlace: " + e.getMessage());
        }
	}
	
	@Operation(summary="This method is used for getting market images")
	@GetMapping(value="/getMarketPlaces")
	public ResponseEntity getMarketPlace() {
		return accessBeanService.getAllMarketPlaces();
	}
	
	@Operation(summary = "This method is used to get specific market place image")
	@GetMapping(value="/getMarketPlaceImage/{marketPlaceId}")
	public ResponseEntity getSelectedMarketPlace(@PathVariable("marketPlaceId") String marketPlaceId) throws MalformedURLException {
		return accessBeanService.getSelectedMarketPlace(marketPlaceId);
	}

	@GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = accessBeanService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
	
	@GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        List<String> cities = accessBeanService.getAllCities();
        return ResponseEntity.ok(cities);
    }
	
	@DeleteMapping(value="/deleteAnnouncement/{id}")
	public ResponseEntity deleteAnnouncement(@PathVariable("id") String id) {
		return accessBeanService.deleteAnnouncement(id);
	}
	
	@PostMapping("/uploadEventsAnnouncementsGalleryAwardsQRCodeImages")
    public ResponseEntity<String> uploadEventsAnnouncementsGalleryAwardsQRCodeImages(@RequestBody UploadBean uploadBean) throws MalformedURLException {
		return accessBeanService.uploadEventsAnnouncementsGalleryAwardsQRCodeImages(uploadBean);
	}
	
	
	@GetMapping("/getCulturalEvents")
	public ResponseEntity<List<CulturalEvents>> getAllCulturalEvents(){
		return accessBeanService.getAllCulturalEvents();
	}
	
//	@GetMapping("/gallery-urls")
//    public ResponseEntity<List<CulturalEvents>> getGalleryURLs() {
//        return accessBeanService.getAllGallery();
//    }
	
//	@GetMapping("/award-urls")
//    public ResponseEntity<List<CulturalEvents>> getAwardURLs() {
//        
//        return accessBeanService.getAllAwards();
//    }
	
	@GetMapping("/getQRCode/{id}")
	public ResponseEntity getQRcode(@PathVariable String id) {
		return accessBeanService.getQRCode(id);
	}
	
	@PostMapping("/verifyEmail")
	public ResponseEntity verifyEmail(@RequestBody VerifyEmail verifyEmail) throws UserNotFoundException, IOException, MessagingException {
		return accessBeanService.verifyEmail(verifyEmail.getEmail());

	}
	
	@PostMapping("/verifyOtp")
	ResponseEntity verifyOtp(@RequestBody VerifyEmail verifyEmail) throws UserNotFoundException {
		return accessBeanService.verifyOtp(verifyEmail.getEmail(), verifyEmail.getOtp());
	}
	
	@PostMapping("/resetPassword")
	ResponseEntity forgotPassword(@RequestBody VerifyEmail verifyEmail) throws UserNotFoundException{
		return accessBeanService.forgotPassword(verifyEmail.getEmail(),verifyEmail.getPassword());
	}
	
	
	
}