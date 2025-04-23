package com.newpackage.neetcounsel.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.newpackage.neetcounsel.dtos.AKTUdto;
import com.newpackage.neetcounsel.dtos.CallingAPIRequest;
import com.newpackage.neetcounsel.dtos.FormDetails;
import com.newpackage.neetcounsel.models.UserDetails;
import com.newpackage.neetcounsel.repository.UserDetailsRepository;

@Service
public class CallingAPIService {

    @Autowired
    private UserDetailsRepository userDetailsRepository;
    
    @Autowired 
    private NeetService neetService;
    
    @Autowired 
    private AKTUService aktuService;
    public void processPdfRequest(CallingAPIRequest req) {
    	System.out.println("in processpdfrequest");
        if ("aktu".equalsIgnoreCase(req.getExamType())) {
            // Build payload
            
            AKTUdto ob=new AKTUdto();
        	ob.setRank(Integer.parseInt(req.getAIR()));
        	ob.setCategory(req.getCategory());
        	ob.setGender(req.getGender());
        	ob.setSubcategory(req.getSubCategory());
        	ob.setUserID(req.getUserID());
        	//ob.setUserID(req.getUserID());
            aktuService.generateAndSaveAKTUPDF(ob);
            // Call external API
           // String response = callApi("http://localhost:7578/button-click", payload);

            // Save details
            saveUserDetails(req, "aktu_" + req.getAIR() + ".pdf");

        } else if ("neet".equalsIgnoreCase(req.getExamType())) {
        	System.out.println("in neet");
        	FormDetails ob=new FormDetails();
        	ob.setAIR(Double.parseDouble(req.getAIR()));
        	ob.setCategory(req.getCategory());
        	ob.setSubCategory(req.getSubCategory());
        	ob.setUserID(req.getUserID());
        	System.out.println("ob created");
           /* Map<String, Object> payload = Map.of(
                "AIR", req.getAIR(),
                "Category", req.getCategory(),
                "SubCategory", req.getSubCategory(),
                "userID", req.getUserID()
            );*/
            
            neetService.generateAndSaveNeetPDF(ob);
            System.out.println("after call api");
            /*String response = callApi("http://localhost:8080/EnterDetails", ob);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_PDF)); // or JSON if expecting that

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response1 = restTemplate.postForEntity("http://localhost:8080/EnterDetails", request, String.class);*/
           // return response.getBody();
            System.out.println("after call api");
            saveUserDetails(req, "neet_" + req.getAIR() + ".pdf");
            System.out.println("after saving");
        }
    }

    private String callApi(String url, Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_PDF)); // or JSON if expecting that

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response.getBody();
    }

    private void saveUserDetails(CallingAPIRequest req, String filename) {
        UserDetails ud = new UserDetails();
        ud.setCategory(req.getCategory());
        ud.setCreatedAt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        ud.setExamType(req.getExamType());
        ud.setFilename(filename);
        ud.setOrderID(req.getOrderID());
        ud.setPaymentID(req.getPaymentID());
        ud.setRank(req.getAIR());
        ud.setSubcategory(req.getSubCategory());
        ud.setUserId(req.getUserID());

        userDetailsRepository.save(ud);
    }
}
