package com.digisprint.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "donations")
public class Donation {

	@Id
	private String id;

	private String fullName;

	private LocalDateTime dob;

	private int phoneNumber;

	private String emailId;

	private String transactionId;

	private int amountPaid;
	
	private LocalDate transactionDate;

	private String transactionReceiptUploadUrl;
}
