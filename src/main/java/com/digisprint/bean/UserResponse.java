package com.digisprint.bean;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserResponse {

	private String accessId;

	private String name;

	private String email;

	private String password;

	private boolean president;

	private boolean accountant;

	private boolean commitee;

	private boolean user;

	private LocalDateTime dateOfAssignedPosition;

	private String token;
}
