package com.newpackage.neetcounsel.dtos;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Document(collection = "neet")
@Data
public class Neet {
	@Id 
	private String id;
	private String QuotaName;
	private double AIR;
	private String Category;
	private String SubCategory;
	@Field("InstituteCode")
	private int InstituteCode;
	private String Institute;
	private String Subject;
	private String AllottedCategory;
	private String Allottedph;
	private String AdmittedRound;

	
}


