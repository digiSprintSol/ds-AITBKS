package com.digisprint.bean;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;


@Data
@Document(collection = "payment_info")
public class PaymentInfo {

	@Id
	private Long paymentId;
	
	private String amountPaid;
	
	private String chequeNumber;
	
	private Date transactionDate;
	
	private String paymentDetailDocument;
}
