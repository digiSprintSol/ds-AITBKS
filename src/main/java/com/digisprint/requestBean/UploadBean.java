package com.digisprint.requestBean;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class UploadBean {

	private String title;

	private List<String> imageURLs;

	private String description;

	private String qrCodeImageUrl;

	private String eventType;  // gallery schloarship awards and events

	private boolean announcement;

	private boolean qrCode;

	private LocalDate eventDate;

	private String sponsoredBy;

}
