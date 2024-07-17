package com.digisprint.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digisprint.bean.AccessBean;
import com.digisprint.service.AccessBeanService;
import com.digisprint.utils.ApplicationConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApplicationConstants.INTERNAL_USERS)
@Tag(name= ApplicationConstants.ROLE_MANAGEMENT)
@CrossOrigin("*")
public class AccessBeanController {

	private AccessBeanService accessBeanService;

	public AccessBeanController(AccessBeanService accessBeanService) {
		super();
		this.accessBeanService = accessBeanService;
	}
	
	
	@Operation(summary = "Method Used for LogIn")
	@PostMapping(value = "/login")
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
	

}
