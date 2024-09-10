package com.digisprint.requestBean;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class UploadBean {

	private String title;
	private String description;
	private List<String> imageUrl;
	private LocalDate eventDate;
}
