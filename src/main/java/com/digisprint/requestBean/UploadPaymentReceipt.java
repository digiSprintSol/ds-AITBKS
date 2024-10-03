package com.digisprint.requestBean;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UploadPaymentReceipt {

	private String transactionId;

	private String amountPaid;

	private String paymentImageUrl;
}
