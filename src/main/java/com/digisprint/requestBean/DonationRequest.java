package com.digisprint.requestBean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DonationRequest {

	private String fullName;

	private LocalDateTime dob;

	private int phoneNumber;

	private String emailId;

	private String transactionId;

	private int amountPaid;

	private LocalDate transactionDate;

	private String transactionReceiptUploadUrl;

}
