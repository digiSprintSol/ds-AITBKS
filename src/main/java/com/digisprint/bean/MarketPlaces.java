package com.digisprint.bean;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection= "market_places")
public class MarketPlaces {

	@Id
	private String id;
	
	private String nameOfShop;
	
	private String contactPerson;
	
	private String mobileNumber;
	
	private String location;
	
	private String category; //hotel ,food,
	
	private String city;
	
	private String image;
	
	@CreatedDate
	private LocalDateTime createdDate;
	
}
