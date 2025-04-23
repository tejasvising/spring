package com.newpackage.neetcounsel.controller;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newpackage.neetcounsel.service.CallingAPIService;
import com.newpackage.neetcounsel.service.RazorpayService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
@CrossOrigin(origins = "*") // allow frontend to connect
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
    
    @GetMapping("/get-key")
	//@CrossOrigin(origins ="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true", methods = {RequestMethod.GET} )
    public ResponseEntity get_key() {
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
    
    
    @PostMapping("/verify")
    public JSONArray verify_signature(@RequestBody ExamDetails ed) {
    	String payment_id=ed.getRazorpay_payment_id();
    	String order_id=ed.getRazorpay_order_id();
    	String signature=ed.getRazorpay_signature();
    	
    	System.out.println("payment_id"+payment_id);
    	System.out.println("order_id"+order_id);
    	System.out.println("signature"+signature);
    	
    	Map<String,Object> pdfgen_details=ed.getDetails();
    	System.out.println("got pdfgen_details");
    	try {
    		String exam_type = (String)pdfgen_details.get("ExamType");
    		Map<String, Object> requestPayload = new HashMap<>();
    		
    		requestPayload.put("userID", pdfgen_details.get("userID"));
    		requestPayload.put("AIR", pdfgen_details.get("AIR"));
    		requestPayload.put("Category", pdfgen_details.get("Category"));
    		requestPayload.put("paymentID",payment_id);
    		requestPayload.put("orderID", order_id);
    		requestPayload.put("examtype", exam_type);
    		if(exam_type.equals("neet") || exam_type.equals("aktu"))requestPayload.put("SubCategory", pdfgen_details.get("SubCategory"));
    		if(exam_type.equals("aktu")) requestPayload.put("Gender", pdfgen_details.get("Gender"));
    		
    		if(payment_id==null || order_id==null || signature==null) {
    			JSONObject obj1=new JSONObject("status","400");
    			JSONObject obj2=new JSONObject("message","Missing payment verification properties");
    			JSONArray arr=new JSONArray();
    			arr.put(obj1);
    			arr.put(obj2);
    			return arr;
    		}
    		System.out.println("verification begins!!!!");
    		boolean isVerified = razorpayService.verifySignature(
    				order_id,
    				payment_id,
    				signature
                );
    		System.out.println("it is verified?"+isVerified);
    		if(!isVerified) {
    			JSONObject obj1=new JSONObject("status","400");
    			JSONObject obj2=new JSONObject("message","Invalid payment signature");
    			JSONArray arr=new JSONArray();
    			arr.put(obj1);
    			arr.put(obj2);
    			return arr;
    		}
    		System.out.println("Received PDF Generation Details:"+ pdfgen_details);
    		System.out.println("New body (without examType):"+ requestPayload);
    		/*HttpHeaders header1 = new HttpHeaders();
            header1.set("Content-Type", "application/json");
            RestTemplate restTemplate = new RestTemplate();
            // Create HTTP request
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, header1);
            
            		// Make POST request
            ResponseEntity<String> response = restTemplate.exchange(UPLOAD_URL+"/callingapi", HttpMethod.POST, requestEntity, String.class);
            if(response.getStatusCode().is2xxSuccessful()) {
            	System.out.println("API call response:"+response.getBody());
            }
            else {
            	System.out.println("API call failed");
            }*/
    		CallingAPIRequest request = new CallingAPIRequest();

    		request.setUserID((String) pdfgen_details.get("userID"));
    		request.setAIR((String) pdfgen_details.get("AIR"));
    		request.setCategory((String) pdfgen_details.get("Category"));
    		request.setPaymentID(payment_id);
    		request.setOrderID(order_id);
    		request.setExamType(exam_type);
    		callingAPIService.processPdfRequest(request);
            JSONObject obj1=new JSONObject("status","200");
			JSONObject obj2=new JSONObject("message","Payment Verified Successfully");
			JSONArray arr=new JSONArray();
			arr.put(obj1);
			arr.put(obj2);
			return arr;
    	}
    	catch(Error e) {
    		JSONObject obj1=new JSONObject("status","400");
			JSONObject obj2=new JSONObject("message","Payment Verification Failed");
			JSONArray arr=new JSONArray();
			arr.put(obj1);
			arr.put(obj2);
			return arr;
    	}
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
