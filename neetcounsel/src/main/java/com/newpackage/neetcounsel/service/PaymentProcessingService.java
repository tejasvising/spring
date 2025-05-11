package com.newpackage.neetcounsel.service;

import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface PaymentProcessingService {
    ResponseEntity<String> handlePaymentAndCallApi(Map<String, Object> requestPayload);
}
