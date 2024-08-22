package com.digisprint.bean;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "events_images_announcements")
public class EventsImagesAnnouncements {

	@Id
	private String id;
	
	private String eventTitle;
	
	private String eventImageURL;
	
	private String eventDescription;
	
	private String galleryTitle;
	
	private String galleryURL;
	
	private String galleryDescription;
	
	private String announcementTitle;
	
	private String announcementDescription;
	
	private String awardsTitle;
	
	private String awardDescription;
	
	private String awardImageURL;
	
	private String qrCodeImageUrl;
	
	private boolean events;
	
	private boolean gallery;
	
	private boolean awards;
	
	private boolean announcement;
	
	private boolean qrCode;
	
}
