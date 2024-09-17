package com.digisprint.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "cultural_events")
public class CulturalEvents {

	@Id
	private String id;
	
	private String title;
	
	private List<String> imageURLs;
	
	private String description;
	
	private String qrCodeImageUrl;
	
	private String eventType;  // gallery schloarship awards and events
	
	private boolean announcement;
	
	private boolean qrCode;
	
	private LocalDateTime eventDate;
	
	private LocalDateTime createdDate;
	
	private String createdBy;
	
	private String sponsoredBy;
	
	private String place;

}
