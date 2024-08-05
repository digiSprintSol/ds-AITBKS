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
	
	private String eventImageName;
	
	private String eventDescription;
	
	private String imageTitle;
	
	private String imageName;
	
	private String imageDescription;
	
	private String announcementTitle;
	
	private String announcementDescription;
	
	private boolean announcement;
}
