package com.digisprint.requestBean;

import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;

@Data
public class LoginPayload {

	private String username;
	private String password;
}
