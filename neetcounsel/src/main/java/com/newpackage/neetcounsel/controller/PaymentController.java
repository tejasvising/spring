package com.newpackage.neetcounsel.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newpackage.neetcounsel.service.CallingAPIService;
import com.newpackage.neetcounsel.service.PaymentVerificationService;
import com.newpackage.neetcounsel.service.RazorpayService;
import com.newpackage.neetcounsel.utils.JwtUtil;
import com.razorpay.Order;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.json.*;
import java.util.*;
import com.newpackage.neetcounsel.dtos.*;
@Configuration
@RestController
//@RequestMapping("/api/payment")
@PropertySource("classpath:application.properties")

public class PaymentController {
	
	private final static Logger log = LoggerFactory.getLogger(Controller.class);
	
    @Autowired
    private RazorpayService razorpayService;
    
    @Value("${UPLOAD_URL}")
	private String UPLOAD_URL;
    
    @Value("${razorpay.key_id}")
	private String razorpay_key;
    @Autowired
    private CallingAPIService callingAPIService;
    @Autowired
    private PaymentVerificationService paymentVerificationService;
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/get-key")
	
    public ResponseEntity get_key(@CookieValue(name = "token", required = false) String token) {
    	
    	System.out.println("in get-key");
    	HashMap<String,String> map=new HashMap<>();
    	map.put("key_id", razorpay_key);
    	log.info("key_id: {}", razorpay_key);
    	return ResponseEntity.ok()
                //.headers(headers)
                .body(map);
    }
    
    @PostMapping("/order")
    public String createOrder() {
        try {
        	Double amount=399.00;
            Order order = razorpayService.createOrder(amount);
            return order.toString();
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
    
 // Inside your Controller class
    @PostMapping("/verify")
    public JSONArray verify_signature(@RequestBody ExamDetails ed) {
        return paymentVerificationService.verifyAndGenerate(ed);
    }
}
   

/* "details":{
        "userID":"67f0eebf85feb01af0526d90",
        "AIR":"1",
        "Category":"OPEN",
        "SubCategory":"FF",
        "Gender":"Female",
        "ExamType":"AKTU"
    }*/
