package com.newpackage.neetcounsel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AKTUdto {
	@JsonProperty("rank")
	int rank;
	@JsonProperty("gender")
	String gender;
	@JsonProperty("category")
	String category;
	@JsonProperty("subcategory")
	String subcategory;
	@JsonProperty("userID")
	String userID;
}
