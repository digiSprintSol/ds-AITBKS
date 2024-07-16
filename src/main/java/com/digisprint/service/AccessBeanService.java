package com.digisprint.service;

import org.springframework.http.ResponseEntity;

import com.digisprint.bean.AccessBean;

public interface AccessBeanService {
	
	String login(String userName, String password);
	ResponseEntity saveInternalUsers(AccessBean accessBean);
	ResponseEntity getAllInternalUsers();
	ResponseEntity fetchInternalUsersById(String id);
	ResponseEntity softDeleteInternalUsers(String id);
	ResponseEntity validateAndGenerateToken(String token);

}
