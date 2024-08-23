package com.digisprint.requestBean;

import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;

@Data
public class UploadBean {

	private String title;
	private String description;
	private String imageUrl;
}
