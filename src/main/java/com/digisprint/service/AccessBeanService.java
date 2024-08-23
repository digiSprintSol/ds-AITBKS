package com.digisprint.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.AccessBean;
import com.digisprint.bean.EventsImagesAnnouncements;
import com.digisprint.bean.MarketPlaces;
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
	ResponseEntity postMarketPlace(String token,String nameOfShop, String contactPerson, String mobileNumber, String location, String category, String city, String photoUrl)throws IOException;
	ResponseEntity getAllMarketPlaces();
	ResponseEntity getSelectedMarketPlace(String marketPlaceId);
	List<String> getAllCategories();
	List<String> getAllCities();
	ResponseEntity deleteAnnouncement(String id);
	ResponseEntity uploadEventsAnnouncementsGalleryAwardsQRCodeImages(String title, String description, String imageUrl)throws MalformedURLException;
	List<EventsImagesAnnouncements> getAllGallery();
	List<EventsImagesAnnouncements> getAllAwards();
}
