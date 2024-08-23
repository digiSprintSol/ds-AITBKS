package com.digisprint.bean;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Document(collection="access_bean")
public class AccessBean {

	@Id
	private String accessId;
	
	private String name;
	
	private String email;
	
	private String password;
	
	private String phoneNumber;
	
	private boolean president;
	
	private boolean accountant;
	
	private boolean commitee;
	
	private boolean user;
	
	@CreatedDate
	private LocalDateTime dateOfAssignedPosition;

	private boolean deleted;
	
	private CharSequence otp;
	
	public String getEmail() {
		return email.toLowerCase();
	}
	
}
