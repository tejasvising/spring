package com.newpackage.neetcounsel.service.impl;

import com.newpackage.neetcounsel.models.UserDetails;
import com.newpackage.neetcounsel.repository.UserDetailsRepository;
import com.newpackage.neetcounsel.service.PaymentProcessingService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentProcessingServiceImpl implements PaymentProcessingService {

    private final UserDetailsRepository userDetailsRepository;

    @Value("${UPLOAD_URL}")
    private String uploadUrl;

    @Override
    public ResponseEntity<String> handlePaymentAndCallApi(Map<String, Object> payload) {
        try {
            String examType = (String) payload.get("examType");
            if ("aktu".equalsIgnoreCase(examType)) {
                return callAktuAPI(payload);
            } else if ("neet".equalsIgnoreCase(examType)) {
                return callNeetAPI(payload);
            } else {
                return ResponseEntity.badRequest().body("Unsupported exam type");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Exception: " + e.getMessage());
        }
    }

    private ResponseEntity<String> callAktuAPI(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_PDF));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of(
            "gender", payload.get("Gender"),
            "rank", payload.get("AIR"),
            "category", payload.get("Category"),
            "subcategory", payload.get("SubCategory"),
            "userID", payload.get("userID")
        ), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://0.0.0.0:7578/button-click", HttpMethod.POST, request, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            saveUserDetails(payload, "aktu");
        }
        return ResponseEntity.ok("status successful");
    }

    private ResponseEntity<String> callNeetAPI(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_PDF));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(Map.of(
            "AIR", payload.get("AIR"),
            "Category", payload.get("Category"),
            "SubCategory", payload.get("SubCategory"),
            "userID", payload.get("userID")
        ), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(uploadUrl + "/EnterDetails", HttpMethod.POST, request, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            saveUserDetails(payload, "neet");
        }
        return ResponseEntity.ok("status successful");
    }

    private void saveUserDetails(Map<String, Object> payload, String examType) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        UserDetails user = new UserDetails();
        user.setUserId((String) payload.get("userID"));
        user.setExamType(examType);
        user.setCategory((String) payload.get("Category"));
        user.setSubcategory((String) payload.get("SubCategory"));
        user.setRank((String) payload.get("AIR"));
        user.setPaymentID((String) payload.get("paymentID"));
        user.setOrderID((String) payload.get("orderID"));
        user.setFilename(examType + "_" + payload.get("AIR") + ".pdf");
        user.setCreatedAt(date);
        userDetailsRepository.save(user);
    }
}
