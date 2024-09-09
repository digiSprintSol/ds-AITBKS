package com.digisprint.bean;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "schloarShips")
public class ScholarShip {

	@Id
	private String id;

	private String scholarShipName;

	private Boolean deleted;

	private String createdBy;

	@CreatedDate
	private LocalDateTime createdDate;

	private String modifiedBy;

	@CreatedDate
	private LocalDateTime modifiedDate;

}
