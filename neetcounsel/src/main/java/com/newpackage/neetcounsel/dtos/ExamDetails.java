package com.newpackage.neetcounsel.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.*;
@Data
@NoArgsConstructor
public class ExamDetails {
	@JsonProperty("razorpay_payment_id")
	String razorpay_payment_id;
	@JsonProperty("razorpay_order_id")
	String razorpay_order_id;
	@JsonProperty("razorpay_signature")
	String razorpay_signature;
	@JsonProperty("details")
	Map<String,Object> details;
}
