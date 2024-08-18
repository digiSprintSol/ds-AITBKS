package com.digisprint.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.AccessBean;
import com.digisprint.bean.MarketPlaces;

public interface AccessBeanService {
	
	ResponseEntity login(String userName, String password);
	ResponseEntity saveInternalUsers(AccessBean accessBean);
	ResponseEntity getAllInternalUsers();
	ResponseEntity fetchInternalUsersById(String id);
	ResponseEntity softDeleteInternalUsers(String id);
	ResponseEntity validateAndGenerateToken(String token);
	ResponseEntity uploadEventsAnnocementsImages(MultipartFile events, MultipartFile imagesForHomePage,String title,String description) throws IOException;
	ResponseEntity postingAnnouncements(String title, String description);
	ResponseEntity getAllAnnouncement();
	ResponseEntity getEvents() throws MalformedURLException;
	ResponseEntity getImages() throws MalformedURLException;
	ResponseEntity postMarketPlace(String token,String nameOfShop, String contactPerson, String mobileNumber, String location, String category, String city, MultipartFile image)throws IOException;
	ResponseEntity getAllMarketPlaces();
	ResponseEntity getSelectedMarketPlace(String marketPlaceId)throws MalformedURLException;
	List<String> getAllCategories();
	List<String> getAllCities();
}
