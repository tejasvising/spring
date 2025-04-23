package com.newpackage.neetcounsel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FormDetails {
	@JsonProperty("AIR")
	double AIR;
	@JsonProperty("Category")
	String Category;
	@JsonProperty("SubCategory")
	String SubCategory;
	@JsonProperty("userID")
	String userID;

}
