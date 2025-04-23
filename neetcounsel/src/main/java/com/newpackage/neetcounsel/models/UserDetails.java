package com.newpackage.neetcounsel.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "details_user")
public class UserDetails {
	@Id
    private String id;
    private String paymentID;
    private String orderID;
    private String filename;
    private String rank; 
    private String category;
    private String subcategory;
    private String userId;
    private String createdAt;
    private String examType;
}
