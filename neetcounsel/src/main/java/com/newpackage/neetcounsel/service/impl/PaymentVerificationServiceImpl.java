package com.newpackage.neetcounsel.service.impl;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.newpackage.neetcounsel.dtos.CallingAPIRequest;
import com.newpackage.neetcounsel.dtos.ExamDetails;
import com.newpackage.neetcounsel.service.CallingAPIService;
import com.newpackage.neetcounsel.service.PaymentVerificationService;
import com.newpackage.neetcounsel.service.RazorpayService;

import lombok.RequiredArgsConstructor;

//com.newpackage.neetcounsel.service.impl.PaymentVerificationServiceImpl.java
@Service
@RequiredArgsConstructor
public class PaymentVerificationServiceImpl implements PaymentVerificationService {

 private final RazorpayService razorpayService;
 private final CallingAPIService callingAPIService;

 @Override
 public JSONArray verifyAndGenerate(ExamDetails ed) {
     String paymentId = ed.getRazorpay_payment_id();
     String orderId = ed.getRazorpay_order_id();
     String signature = ed.getRazorpay_signature();

     Map<String, Object> pdfgenDetails = ed.getDetails();

     JSONArray response = new JSONArray();
     if (paymentId == null || orderId == null || signature == null) {
    	 
         response.put(new JSONObject(Map.of("status", "400")));
         response.put(new JSONObject(Map.of("message", "Missing payment verification properties")));
         return response;
     }

     boolean isVerified = razorpayService.verifySignature(orderId, paymentId, signature);

     if (!isVerified) {
         response.put(new JSONObject(Map.of("status", "400")));
         response.put(new JSONObject(Map.of("message", "Invalid payment signature")));
         return response;
     }

     CallingAPIRequest request = new CallingAPIRequest();
     request.setUserID((String) pdfgenDetails.get("userID"));
     request.setAIR((String) pdfgenDetails.get("AIR"));
     request.setCategory((String) pdfgenDetails.get("Category"));
     request.setPaymentID(paymentId);
     request.setOrderID(orderId);
     request.setExamType((String) pdfgenDetails.get("ExamType"));

     if (pdfgenDetails.containsKey("SubCategory"))
         request.setSubCategory((String) pdfgenDetails.get("SubCategory"));
     if (pdfgenDetails.containsKey("Gender"))
         request.setGender((String) pdfgenDetails.get("Gender"));

     callingAPIService.processPdfRequest(request);

     response.put(new JSONObject(Map.of("status", "200")));
     response.put(new JSONObject(Map.of("message", "Payment Verified Successfully")));

     return response;
 }
}

