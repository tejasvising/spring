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
import com.newpackage.neetcounsel.service.NeetCounselService;
import com.newpackage.neetcounsel.service.PDFService;
import com.newpackage.neetcounsel.service.PaymentProcessingService;
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
	private final NeetCounselService neetCounselService;
	private final UserPDFService userpdfservice;
	private final UserPDFRepository userPDFRepository;
	private final UserDetailsRepository userDetailsRepository;
	private final PaymentProcessingService paymentProcessingService;

	//private NeetService neetService;
	//public List<String> res1;
	private List<Neet> result;

	
	
	@GetMapping("/pdfdownload")
	
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
	public ResponseEntity<byte[]> generateNeetPdf(@RequestBody FormDetails formDetails) {
	    return neetCounselService.processNeetForm(formDetails);
	}
	
	@PostMapping("/callingapi")
	public ResponseEntity<String> callingapi(@RequestBody Map<String, Object> payload) {
	    return paymentProcessingService.handlePaymentAndCallApi(payload);
	}
	
	@GetMapping("/coldstart")
	public String forColdRestart() {
		return "Greetings";
	}
	
	
}
	   
	
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