package com.digisprint.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.AccessBean;
import com.digisprint.bean.CulturalEvents;
import com.digisprint.bean.MarketPlaces;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.requestBean.UploadBean;
import com.digisprint.responseBody.GetDocumentURL;

public interface AccessBeanService {
	
	ResponseEntity login(String userName, String password);
	ResponseEntity saveInternalUsers(AccessBean accessBean);
	ResponseEntity getAllInternalUsers();
	ResponseEntity fetchInternalUsersById(String id);
	ResponseEntity softDeleteInternalUsers(String id);
	ResponseEntity validateAndGenerateToken(String token);
	ResponseEntity postingAnnouncements(String title, String description);
	ResponseEntity getAllAnnouncement();
	ResponseEntity getEvents() throws MalformedURLException;
	ResponseEntity postMarketPlace(String token, MarketPlaces marketPlaces)throws IOException;
	ResponseEntity getAllMarketPlaces();
	ResponseEntity getSelectedMarketPlace(String marketPlaceId);
	List<String> getAllCategories();
	List<String> getAllCities();
	ResponseEntity deleteAnnouncement(String id);
	ResponseEntity uploadEventsAnnouncementsGalleryAwardsQRCodeImages(UploadBean uploadBean)throws MalformedURLException;
	ResponseEntity getAllGallery();
	ResponseEntity getAllAwards();
	ResponseEntity verifyEmail(String email) throws UserNotFoundException, IOException, MessagingException;
	ResponseEntity verifyOtp(String email,String otp) throws UserNotFoundException ;
	ResponseEntity forgotPassword(String email,String newPassword) throws UserNotFoundException;
	ResponseEntity getQRCode(String id);
	ResponseEntity<List<CulturalEvents>> getAllCulturalEvents();
}
