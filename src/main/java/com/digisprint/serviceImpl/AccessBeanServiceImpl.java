package com.digisprint.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.AccessBean;
import com.digisprint.bean.EventsImagesAnnouncements;
import com.digisprint.bean.Image;
import com.digisprint.bean.MarketPlaces;
import com.digisprint.bean.UserResponse;
import com.digisprint.exception.UserNotFoundException;
import com.digisprint.filter.JwtTokenUtil;
import com.digisprint.repository.AccessBeanRepository;
import com.digisprint.repository.EventsImagesAnnouncementsRepo;
import com.digisprint.repository.MarketPlaceRepository;
import com.digisprint.responseBody.GetDocumentURL;
import com.digisprint.responseBody.LoginResponse;
import com.digisprint.service.AccessBeanService;
import com.digisprint.utils.ApplicationConstants;
import com.digisprint.utils.ErrorResponseConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccessBeanServiceImpl implements AccessBeanService{

	private AccessBeanRepository accessBeanRepository;

	private	EventsImagesAnnouncementsRepo eventsImagesAnnouncementsRepo; 

	private JwtTokenUtil jwtTokenUtil;

	private MarketPlaceRepository marketPlaceRepository;

	public AccessBeanServiceImpl(AccessBeanRepository accessBeanRepository,
			EventsImagesAnnouncementsRepo eventsImagesAnnouncementsRepo, JwtTokenUtil jwtTokenUtil,
			MarketPlaceRepository marketPlaceRepository) {
		super();
		this.accessBeanRepository = accessBeanRepository;
		this.eventsImagesAnnouncementsRepo = eventsImagesAnnouncementsRepo;
		this.jwtTokenUtil = jwtTokenUtil;
		this.marketPlaceRepository = marketPlaceRepository;
	}

	@Value("${config.secretKey}")
	private  String secretKey;

	@Value("${org.home}")
	private String pathForStorage;
	
	@Value("${org.marketPlace}")
	private String pathForMarketPlaces;

	@Autowired
	HttpServletResponse response;

	@Override
	public ResponseEntity saveInternalUsers(AccessBean accessBean) {

		if(accessBeanRepository.findByEmail(accessBean.getEmail()).isPresent()) {
			return new ResponseEntity(ErrorResponseConstants.EMAIL_ALREADY_EXISTS,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			accessBean.setDeleted(false);
			accessBeanRepository.save(accessBean);
			return new ResponseEntity(accessBean,HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity getAllInternalUsers() {

		List<AccessBean> getAllUsers = accessBeanRepository.findAll();
		if(getAllUsers.size()==0) {
			return new ResponseEntity(ErrorResponseConstants.NO_USERS_FOUND,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			getAllUsers	= getAllUsers.stream().filter(user -> user.isDeleted()==false).collect(Collectors.toList());
			return new ResponseEntity(getAllUsers,HttpStatus.OK);
		}

	}

	@Override
	public ResponseEntity fetchInternalUsersById(String id) {
		AccessBean internalUsers = new AccessBean();
		try {
			internalUsers = accessBeanRepository.findById(id).orElseThrow(()->new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND));
		} catch (Exception e) {
			return new ResponseEntity(ErrorResponseConstants.USER_NOT_FOUND,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity(internalUsers,HttpStatus.OK);

	}

	@Override
	public ResponseEntity softDeleteInternalUsers(String id) {
		AccessBean internalUsers = new AccessBean();

		try {
			internalUsers = accessBeanRepository.findById(id)
					.orElseThrow(()->new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND));
			internalUsers.setDeleted(true);
			accessBeanRepository.save(internalUsers);
		} catch (Exception e) {
			return new ResponseEntity(ErrorResponseConstants.USER_NOT_FOUND,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity(internalUsers,HttpStatus.OK);
	}		


	private List<String> getAccessList(AccessBean accessBean){

		List<String> accessList = new ArrayList();
		if(accessBean.isPresident()){
			accessList.add(ApplicationConstants.PRESIDENT);
		}
		if(accessBean.isAccountant()){
			accessList.add(ApplicationConstants.ACCOUNTANT);
		}
		if(accessBean.isCommitee()){
			accessList.add(ApplicationConstants.COMMITEE);
		}
		if(accessBean.isUser()){
			accessList.clear();
			accessList.add(ApplicationConstants.USER);
		}

		return accessList;

	} 

	@Override
	public ResponseEntity  login(String userName, String password) {
		AccessBean accessBean = accessBeanRepository.findByEmailAndPassword(userName, password);
		String cookie = jwtTokenUtil.generateToken(userName, accessBean.getAccessId(), getAccessList(accessBean), password);
		Cookie cookie1 = new Cookie("token",cookie);
		cookie1.setHttpOnly(true); // Make the cookie HTTP-only
		cookie1.setSecure(false); // Secure flag ensures cookie is sent over HTTPS
		cookie1.setMaxAge(60 * 60 * 24); // Set cookie expiration (in seconds)
		response.addCookie(cookie1);
		cookie1.setPath("/"); 
		//			System.out.println("cookies get values::"+cookie1.getValue());
		LoginResponse loginResponse = new LoginResponse();
		loginResponse.setToken(cookie);
		return new ResponseEntity(loginResponse,HttpStatus.OK);
	}

	public  Claims decodeAndValidateToken(String token) {
		try {
			return Jwts.parser()
					.setSigningKey(secretKey.getBytes())
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			log.error(ErrorResponseConstants.INVALID_TOKEN + e.getMessage());
			return null;
		}
	}

	@Override
	public ResponseEntity validateAndGenerateToken(String token) {

		AccessBean internalUsers = new AccessBean();
		UserResponse userresponse = new UserResponse();
		try {
			Claims claims = decodeAndValidateToken(token);

			if (claims == null) {
				throw new UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND);
			}

			String userName = String.valueOf(claims.get(ApplicationConstants.USERNAME)).replace(ApplicationConstants.REPLACE_WITH_FORWARDSLASH, ApplicationConstants.EMPTY_QUOTATION_MARK).trim().toLowerCase();
			internalUsers = accessBeanRepository.findByEmail(userName).orElseThrow(()-> new  UserNotFoundException(ErrorResponseConstants.USER_NOT_FOUND));
			userresponse.setAccessId(internalUsers.getAccessId());
			userresponse.setName(internalUsers.getName());
			userresponse.setPresident(internalUsers.isPresident());
			userresponse.setCommitee(internalUsers.isCommitee());
			userresponse.setAccountant(internalUsers.isAccountant());
			userresponse.setUser(internalUsers.isUser());
			userresponse.setEmail(internalUsers.getEmail());
			userresponse.setToken(jwtTokenUtil.generateToken(internalUsers.getName(), internalUsers.getAccessId(), getAccessList(internalUsers),
					String.valueOf(claims.get(ApplicationConstants.OID)).replace(ApplicationConstants.REPLACE_WITH_FORWARDSLASH, ApplicationConstants.EMPTY_QUOTATION_MARK).trim().toLowerCase()));
		} catch (Exception e) {
			return new ResponseEntity(ErrorResponseConstants.USER_NOT_FOUND,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (userresponse != null) {
			return new ResponseEntity(userresponse,HttpStatus.OK);
		}
		else {
			return new ResponseEntity(ErrorResponseConstants.USER_NOT_FOUND,HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	private void saveFileForEventsOrImages(String title,String folderPath, String fileType , MultipartFile file,
			String formattedDate,String description) throws IOException {
		EventsImagesAnnouncements object = new EventsImagesAnnouncements();

		if(!file.isEmpty()) {
			String originalFileName = file.getOriginalFilename();
			String extension = originalFileName.substring(originalFileName.lastIndexOf(ApplicationConstants.FULL_STOP));
			System.out.println("dhfjsdhf::"+extension);

			String newFileName = title+ ApplicationConstants.UNDERSCORE + fileType
					+ ApplicationConstants.UNDERSCORE
					+ formattedDate.replace(ApplicationConstants.COMMA, ApplicationConstants.HYPHEN) + extension;
			System.out.println(newFileName);
			String filePath = folderPath + ApplicationConstants.DOUBLE_SLASH + newFileName;
			System.out.println(":::::::::::::::::::::"+filePath);
			Path path = Paths.get(filePath);
			Files.write(path, file.getBytes());

			switch (fileType) {
			case ApplicationConstants.EVENTS :
				object.setId("1");
				object.setEventDescription(description);
				object.setEventTitle(title);
				object.setEventImageURL(newFileName);
				break;
			case ApplicationConstants.IMAGES :
				object.setId("2");
				object.setGalleryTitle(title);
				object.setGalleryDescription(description);
				object.setGalleryURL(description);
				break;
			default:
				break;
			}
			eventsImagesAnnouncementsRepo.save(object);
		}
	}

	@Override
	public ResponseEntity uploadEventsAnnocementsImages(MultipartFile events, MultipartFile imagesForHomePage,
			String title, String description) throws IOException {
		String folderPath = pathForStorage;
		File folder = new File(folderPath);
		ResponseEntity result = null;
		String strinFormateLocalDate = LocalDate.now().toString();

		if(folder.exists()) {
			if(events!=null) {
				saveFileForEventsOrImages(title, folderPath, ApplicationConstants.EVENTS, events,strinFormateLocalDate , description);
			}
			if(imagesForHomePage != null) {
				saveFileForEventsOrImages(title, folderPath, ApplicationConstants.IMAGES, imagesForHomePage, strinFormateLocalDate, description);
			}
			return new ResponseEntity("Files Uploaded",HttpStatus.OK);
		}
		else {
			return new ResponseEntity("Folder doesnot exsists",HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity postingAnnouncements(String title, String description) {

		if(title!=null && description !=null) {
			EventsImagesAnnouncements announcement = new EventsImagesAnnouncements();
			announcement.setAnnouncementTitle(title);
			announcement.setAnnouncementDescription(description);
			announcement.setAnnouncement(true);
			eventsImagesAnnouncementsRepo.save(announcement);
			return new ResponseEntity("Announcements created",HttpStatus.OK);
		}
		else {
			return new ResponseEntity("Inputs are not proper",HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@Override
	public ResponseEntity getAllAnnouncement() {
		List<EventsImagesAnnouncements>announcements =eventsImagesAnnouncementsRepo.findByAnnouncement(true);
		if(announcements.size()==0) {
			return new ResponseEntity("No Announcements found",HttpStatus.NOT_FOUND);
		}
		else {
			return new ResponseEntity(announcements,HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity getEvents() throws MalformedURLException {
		EventsImagesAnnouncements event= eventsImagesAnnouncementsRepo.findById("1").get();
		return getFile(event.getEventImageURL());
	}
	
	@Override
	public ResponseEntity getImages() throws MalformedURLException {
		EventsImagesAnnouncements event= eventsImagesAnnouncementsRepo.findById("2").get();
		return getFile(event.getEventImageURL());
	}
	
	@Override
	public ResponseEntity getSelectedMarketPlace(String marketPlaceId) {
		MarketPlaces marketPlaces = marketPlaceRepository.findById(marketPlaceId).get();
		GetDocumentURL documentURL = new GetDocumentURL();
		documentURL.setPathOfDocumnet(marketPlaces.getImage());
		return new ResponseEntity<>(documentURL,HttpStatus.OK);
	}
	
	private ResponseEntity getMPImage(String documentName) throws MalformedURLException {
		Path filePath = Paths.get(pathForMarketPlaces + ApplicationConstants.DOUBLE_SLASH +documentName);
		System.out.println(filePath.toString());
		GetDocumentURL documentResponse = new GetDocumentURL();
		if(filePath != null) {
			documentResponse.setPathOfDocumnet(filePath.toString());
			return new ResponseEntity(documentResponse,HttpStatus.OK);

		}else {
			return new ResponseEntity("File not found",HttpStatus.NOT_FOUND);
		}

	}


	private ResponseEntity getFile(String documentName) throws MalformedURLException {
		Path filePath = Paths.get(pathForStorage + ApplicationConstants.DOUBLE_SLASH +documentName);
		System.out.println(filePath.toString());
		GetDocumentURL documentResponse = new GetDocumentURL();
		if(filePath != null) {
			documentResponse.setPathOfDocumnet(filePath.toString());
			return new ResponseEntity(documentResponse,HttpStatus.OK);

		}else {
			return new ResponseEntity("File not found",HttpStatus.NOT_FOUND);
		}

	}

	public JSONObject decodeToken(String jwtToken) {
		return JwtTokenUtil.decodeUserToken(jwtToken);
	}

	@Override
	public ResponseEntity postMarketPlace(String token, String nameOfShop, String contactPerson, String mobileNumber, String location, String category, String city, String photoUrl) throws IOException {

		JSONObject jsonObject = decodeToken(token);
		if (!jsonObject.has("userId") || !jsonObject.has("access")) {
			throw new IllegalArgumentException("Token must contain 'id' and 'access' fields");
		}

		List accessList = jwtTokenUtil.getAccessList(token);

		if(accessList.contains(ApplicationConstants.PRESIDENT)){

	        MarketPlaces marketPlace = new MarketPlaces();
	        marketPlace.setNameOfShop(nameOfShop);
	        marketPlace.setContactPerson(contactPerson);
	        marketPlace.setMobileNumber(mobileNumber);
	        marketPlace.setLocation(location);
	        marketPlace.setCategory(category);
	        marketPlace.setCity(city);
	        marketPlace.setImage(photoUrl);
	        marketPlace.setCreatedDate(LocalDateTime.now());
			
			 marketPlaceRepository.save(marketPlace);
			return new ResponseEntity(marketPlace,HttpStatus.OK);
		}
		else {
			return new ResponseEntity("No access found",HttpStatus.NOT_FOUND);
		}
	}

	@Override
	public ResponseEntity getAllMarketPlaces() {

		List<MarketPlaces> marketPlacesList	= marketPlaceRepository.findAll();

		if(marketPlacesList.size()==0) {
			return new ResponseEntity("No data found",HttpStatus.NOT_FOUND);
		}
		else {
			return new ResponseEntity(marketPlacesList,HttpStatus.OK);
		}
	}

	@Override
	public List<String> getAllCategories() {
		
		List<MarketPlaces> list = marketPlaceRepository.findAll();
		
		return list.stream()
                .map(MarketPlaces::getCategory)
                .distinct()             
                .collect(Collectors.toList());
	}

	@Override
	public List<String> getAllCities() {
		
		List<MarketPlaces> list = marketPlaceRepository.findAll();
		
		return list.stream()
				.map(MarketPlaces::getCity)
				.distinct()
				.collect(Collectors.toList());
	}

	@Override
	public ResponseEntity deleteAnnouncement(String id) {
		try {
			Optional<EventsImagesAnnouncements> imageOptional = eventsImagesAnnouncementsRepo.findById(id);

			if (imageOptional.isPresent()) {
				eventsImagesAnnouncementsRepo.deleteById(id);

				return ResponseEntity.ok("Image with id " + id + " has been successfully deleted.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Image with id " + id + " not found.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while trying to delete the image.");
		}
	}

	

}

