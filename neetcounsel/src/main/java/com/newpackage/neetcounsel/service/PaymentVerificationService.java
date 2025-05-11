package com.newpackage.neetcounsel.service;

import org.json.JSONArray;

import com.newpackage.neetcounsel.dtos.ExamDetails;

//com.newpackage.neetcounsel.service.PaymentVerificationService.java
public interface PaymentVerificationService {
 JSONArray verifyAndGenerate(ExamDetails ed);
}

