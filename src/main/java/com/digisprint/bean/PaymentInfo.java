package com.digisprint.bean;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;


@Data
public class PaymentInfo {

	private String transactionId;
	
	private String amountPaid;
		
	private LocalDate transactionDate;
	
	private String paymentDetailDocument;
}
