package com.digisprint.bean;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.data.annotation.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class Address {

	@Id
	private Long addressId;

	private String addressLine1;

	private String addressLine2;

	private String country;

	private String state;

	private String city;
	
	private String postalCode;

	@CreatedDate
	private LocalDateTime creationDate;

	private LocalDateTime lastModifiedDate;

	private boolean defaultAddress;
	
	private String addressType; //office current permanent
	


}