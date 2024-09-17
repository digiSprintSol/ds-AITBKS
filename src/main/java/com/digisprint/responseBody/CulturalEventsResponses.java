package com.digisprint.responseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class CulturalEventsResponses {

	private String title;
	
	private List<String> imageURLs;
	
	private String description;
	
	private String qrCodeImageUrl;
	
	private String eventType;  // gallery schloarship awards and events
	
	private boolean announcement;
	
	private boolean qrCode;
	
	private LocalDateTime eventDate;
	
	private String createdBy;
	
	private String sponsoredBy;
	
	private String place;
}
