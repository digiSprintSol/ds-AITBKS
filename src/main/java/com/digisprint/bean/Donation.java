package com.digisprint.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "donations")
public class Donation {

	@Id
	private String id;

	private String fullName;
	
	private String donorPicUrl;

	private LocalDateTime dob;

	private String phoneNumber;

	private String status;
	
	private String profession;
	
	private String emailId;

	private String transactionId;

	private String amountPaid;
	
	private LocalDate transactionDate;

	private String transactionReceiptUploadUrl;
	
	private boolean acknowledge;
	
	@CreatedDate
	private LocalDateTime modifiedDate;
	
	@CreatedDate
	private LocalDateTime createdDate;
}
