package com.digisprint.responseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DonationResponse {
	
	private String fullName;

	private LocalDateTime dob;

	private String phoneNumber;
	
	private String status;

	private String emailId;

	private String transactionId;

	private String amountPaid;
	
	private LocalDate transactionDate;

	private String transactionReceiptUploadUrl;

}
