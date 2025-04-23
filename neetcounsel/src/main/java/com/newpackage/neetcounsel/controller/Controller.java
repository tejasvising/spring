package com.newpackage.neetcounsel.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;



import com.newpackage.neetcounsel.dtos.*;
import com.newpackage.neetcounsel.models.UserDetails;
import com.newpackage.neetcounsel.repository.*;

import com.newpackage.neetcounsel.service.PDFService;
import com.newpackage.neetcounsel.service.UserPDFService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
@RestController
@PropertySource("classpath:application.properties")
@RequiredArgsConstructor
public class Controller {

	private String lang;
	private final static Logger log = LoggerFactory.getLogger(Controller.class);
	
	@Value("${UPLOAD_URL}")
	private String UPLOAD_URL; //= "http://localhost:5000/upload-pdf";
	
	@Value("${ALLOW_ORIGINS}")
	private static String allowOrigin;
	
	@Value("${razorpay.key_id}")
	private String razorpay_key;
	private final NeetRepository neetRepository;
	
	private final PDFService pdfservice;
	private final UserPDFService userpdfservice;
	private final UserPDFRepository userPDFRepository;
	private final UserDetailsRepository userDetailsRepository;
	//private NeetService neetService;
	//public List<String> res1;
	private List<Neet> result;

	
	/*  
	  public Controller(NeetRepository neetRepository, NeetService neetService,PDFService pdfservice,UserPDFRepository userPDFRepository) 
	  { 
		  this.neetRepository = neetRepository;
		  this.pdfservice=pdfservice; //this.neetService = neetService; //
		//  this.res1=application.res1;
		  this.userPDFRepository=userPDFRepository;
	  }
	 */
	/*
	 * @Bean CommandLineRunner demo(NeetRepository neetRepository) { return args ->
	 * {
	 * 
	 * 
	 * 
	 * 
	 * 
	 * log.info("Lookup each entry by InstituteCode..."); List<Neet>
	 * res=neetRepository.findByAIR(27); res1=neetRepository.findAllInstituteCode();
	 * //System.out.println("res1.size():"+res1.size()); (int i=0;i<res1.size();i++)
	 * {
	 * 
	 * log.info("Institue: {}",res1.get(i)); } for(int i=0;i<res.size();i++) { Neet
	 * entry=res.get(i); log.info("CollegeName: {}", entry.getInstitute()); }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * }; }
	 */
	@GetMapping("/pdfdownload")
	@CrossOrigin(origins ="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true", methods = {RequestMethod.GET} )
	ResponseEntity downloadPDF(@RequestParam String userID) {
		List<UserPDF> pdfs=userPDFRepository.findByUserID(userID);
		HttpHeaders headers = new HttpHeaders();
		//headers.setContentType(MediaType.APPLICATION_PDF);
        
		//byte[] arrresp=pdf.getFile();
        return ResponseEntity.ok()
                //.headers(headers)
                .body(pdfs);
	}
	
	@PostMapping("/EnterDetails")
	@CrossOrigin(origins = "http://localhost:3000,http://localhost:5000", allowedHeaders = "*", allowCredentials = "true", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS},exposedHeaders = "Content-Disposition" )
	ResponseEntity logicForData(@RequestBody FormDetails ob) {
		result = new ArrayList<>();
		if (ob == null) {
			System.out.println("ob is null");
		}
		
		String category = ob.getCategory();
		String subcategory = ob.getSubCategory();
		double AIR = ob.getAIR();
		String userID=ob.getUserID();	// log.info("Category: {}", ob.getCategory());
		// log.info("SubCategory: {}", ob.getSubCategory());

		HashSet<Integer> set = new HashSet<>();
		List<Neet> greaterthanAIRdata = neetRepository.findByAirGreaterThanEqual(AIR);
		for (int i = 0; i < greaterthanAIRdata.size(); i++) {
			Neet itr = greaterthanAIRdata.get(i);
			if (!set.contains(itr.getInstituteCode())) {
				if ((itr.getCategory().equals(category) || itr.getCategory().equals("General"))
						&& (itr.getSubCategory().equals(subcategory) || itr.getSubCategory().equals("NO"))) {
					result.add(itr);
					set.add(itr.getInstituteCode());
				}
			}
		}
		byte[] pdfBytes = pdfservice.createPDF(result);
		
		/******************************** to call flask api for saving pdf in database - mongodb ********************************************/
		RestTemplate restTemplate = new RestTemplate();
		
		
		/*Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("userID", ob.getUserID());
        requestPayload.put("AIR", ob.getAIR());
        requestPayload.put("exam", "NEET");  // Change to JEE or AKTU if needed
        requestPayload.put("pdfBytes", pdfBytes);

        // Set headers
        HttpHeaders header1 = new HttpHeaders();
        header1.set("Content-Type", "application/json");

        // Create HTTP request
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, header1);
        
        		// Make POST request
        ResponseEntity<String> response = restTemplate.exchange(UPLOAD_URL+"/upload-pdf", HttpMethod.POST, requestEntity, String.class);
*/
		//UserPDF userPDF=new UserPDF(userID,"neet_"+AIR+".pdf",pdfBytes);
		//userPDFRepository.save(userPDF);
		
		userpdfservice.saveUserPDF(userID, "neet_"+AIR+".pdf", pdfBytes);
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "neet_"+AIR+".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
	}
	
	ResponseEntity callingapi(@RequestBody Map<String, Object> requestPayload) {
		String paymentID=(String)requestPayload.get("paymentID");
		String orderID=(String)requestPayload.get("orderID");
		String examType=(String)requestPayload.get("examType");
		try {
		if(examType.equals("aktu")) {
			Map<String,Object> payload=new HashMap<>();
			payload.put("gender", requestPayload.get("Gender"));
			payload.put("rank", requestPayload.get("AIR"));
			payload.put("category", requestPayload.get("Category"));
			payload.put("subcategory", requestPayload.get("SubCategory"));
			payload.put("userID", requestPayload.get("userID"));
			
			HttpHeaders header1 = new HttpHeaders();
            header1.set("Content-Type", "application/json");
            header1.set("Accept", "application/pdf");
            RestTemplate restTemplate = new RestTemplate();
            // Create HTTP request
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, header1);
            
            		// Make POST request
            ResponseEntity<String> response = restTemplate.exchange("http://0.0.0.0:7578/button-click", HttpMethod.POST, requestEntity, String.class);
            
            if(response.getStatusCode().is2xxSuccessful()) {
            	LocalDate currentDate = LocalDate.now();
            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // common DB format

            	String dateString = currentDate.format(formatter);
            	System.out.println("API call response:"+response.getBody());
            	UserDetails ud=new UserDetails();
            	ud.setCategory((String)requestPayload.get("Category"));
            	ud.setCreatedAt(dateString);
            	ud.setExamType(examType);
            	ud.setFilename("aktu_"+(String)requestPayload.get("AIR")+".pdf");
            	ud.setOrderID(orderID);
            	ud.setPaymentID(paymentID);
            	ud.setRank((String)requestPayload.get("AIR"));
            	ud.setSubcategory((String)requestPayload.get("SubCategory"));
            	ud.setUserId((String)requestPayload.get("userID"));
            	userDetailsRepository.save(ud);
            }
		}
		else if(examType.equals("neet")) {
			Map<String,Object> payload=new HashMap<>();
			payload.put("AIR", requestPayload.get("AIR"));
			payload.put("Category", requestPayload.get("Category"));
			payload.put("SubCategory", requestPayload.get("SubCategory"));
			payload.put("userID", requestPayload.get("userID"));
			
			HttpHeaders header1 = new HttpHeaders();
            header1.set("Content-Type", "application/json");
            header1.set("Accept", "application/pdf");
            RestTemplate restTemplate = new RestTemplate();
            // Create HTTP request
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, header1);
            
            		// Make POST request
            ResponseEntity<String> response = restTemplate.exchange(UPLOAD_URL+"/EnterDetails", HttpMethod.POST, requestEntity, String.class);
            if(response.getStatusCode().is2xxSuccessful()) {
            	LocalDate currentDate = LocalDate.now();
            	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // common DB format

            	String dateString = currentDate.format(formatter);
            	System.out.println("API call response:"+response.getBody());
            	UserDetails ud=new UserDetails();
            	ud.setCategory((String)requestPayload.get("Category"));
            	ud.setCreatedAt(dateString);
            	ud.setExamType(examType);
            	ud.setFilename("neet_"+(String)requestPayload.get("AIR")+".pdf");
            	ud.setOrderID(orderID);
            	ud.setPaymentID(paymentID);
            	ud.setRank((String)requestPayload.get("AIR"));
            	ud.setSubcategory((String)requestPayload.get("SubCategory"));
            	ud.setUserId((String)requestPayload.get("userID"));
            	userDetailsRepository.save(ud);
            }
            
		}
		return ResponseEntity.ok().body("status successfull");
		}
		catch(Error e) {
			System.out.println(e);
			return ResponseEntity.internalServerError().body("e:"+e);
		}
	} 
	
}
	   /* table.addCell("row 1, col 1");
	    table.addCell("row 1, col 2");
	    table.addCell("row 1, col 3");*/
	
/*
 * for(int i=0;i<res1.size();i++) { //select max(AIR) from neet where
 * institutecode='' and category='' and subcategory='' double max1; double max2;
 * double max3; double max4;
 * max1=neetService.getMaxAIR(res1.get(i)==null?0:Integer.parseInt(res1.get(i)),
 * category, subcategory) .orElse(0);
 * max2=neetService.getMaxAIR(res1.get(i)==null?0:Integer.parseInt(res1.get(i)),
 * "General", "NO") .orElse(0);
 * max3=neetService.getMaxAIR(res1.get(i)==null?0:Integer.parseInt(res1.get(i)),
 * "General", subcategory) .orElse(0);
 * max4=neetService.getMaxAIR(res1.get(i)==null?0:Integer.parseInt(res1.get(i)),
 * category, "NO") .orElse(0); double max=Math.max(max3, max4); double
 * max0=Math.max(max1, max2); max=Math.max(max, max0); //
 * System.out.println("max:"+max); if(max>=AIR) { Neet
 * chk=neetRepository.findByAIR(max); if(chk==null) {
 * System.out.println("chk is null"); }
 * if(!set.contains(chk.getInstituteCode())) { set.add(chk.getInstituteCode());
 * result.add(chk); } //System.out.println("cnt: "+cnt); } //
 * log.info("Institute: {}, AIR: {}", res1.get(i), max1);
 * 
 * }
 */
/*for (int i = 0; i < result.size(); i++) {
	System.out.println(result.get(i).toString());
}*/
/*
 * for(int i=0;i<res1.size();i++) {
 * 
 * log.info("Institue: {}",res1.get(i)); }
 */