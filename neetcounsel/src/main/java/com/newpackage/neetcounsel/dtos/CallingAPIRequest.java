package com.newpackage.neetcounsel.dtos;
import lombok.*;
@Data
@NoArgsConstructor
public class CallingAPIRequest {
	private String userID;
    private String AIR;
    private String Category;
    private String paymentID;
    private String orderID;
    private String examType;
    private String SubCategory;
    private String Gender;
}
