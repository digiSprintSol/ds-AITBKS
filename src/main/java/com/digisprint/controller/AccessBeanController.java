package com.digisprint.controller;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digisprint.bean.AccessBean;
import com.digisprint.service.AccessBeanService;
import com.digisprint.utils.ApplicationConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
//@RequestMapping(ApplicationConstants.INTERNAL_USERS)
@Tag(name= ApplicationConstants.ROLE_MANAGEMENT)
@CrossOrigin
public class AccessBeanController {

	private AccessBeanService accessBeanService;
	
	public AccessBeanController(AccessBeanService accessBeanService) {
		super();
		this.accessBeanService = accessBeanService;
	}

	@Operation(summary = "Method Used for LogIn")
	@PostMapping("/login")
	String login(@RequestParam String userName, @RequestParam String password) {
		return accessBeanService.login(userName, password);
	}
	
	@Operation(summary = "Method is used for LogIn with Token")
	@PostMapping("/loginWithToken")
	ResponseEntity validateAndGenerateToken(@RequestParam String token) {
		return accessBeanService.validateAndGenerateToken(token);
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
	@PostMapping(value="/uploadEventsImages",consumes = { "multipart/form-data" })
	public ResponseEntity uploadEventsAnnocementsImages(@RequestParam(name="events",required =false) MultipartFile events,
			@RequestParam(name="imagesForHomePage",required =false) MultipartFile imagesForHomePage ,
		@RequestParam(name="title",required=false) String title,
		@RequestParam(name="description",required=false) String description) throws IOException{
		return accessBeanService.uploadEventsAnnocementsImages(events, imagesForHomePage, title, description);
	}
	
	@PostMapping(value="/postingAnnouncements")
	public ResponseEntity postingAnnouncements(@RequestParam String title, @RequestParam String description)	{
		return accessBeanService.postingAnnouncements(title,description);
	}
	
	@GetMapping(value="/getAllAnnouncements")
	public ResponseEntity getAllAnnouncement () {
		return accessBeanService.getAllAnnouncement();
	}
	
	@GetMapping(value="/getEvents")
	public ResponseEntity getEvents() throws MalformedURLException {
		return accessBeanService.getEvents();
	}
	
	@GetMapping(value="/getImages")
	public ResponseEntity getImages() throws MalformedURLException {
		return accessBeanService.getImages();
	}
	
}