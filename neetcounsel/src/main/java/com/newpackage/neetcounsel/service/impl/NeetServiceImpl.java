package com.newpackage.neetcounsel.service.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.newpackage.neetcounsel.dtos.FormDetails;
import com.newpackage.neetcounsel.dtos.Neet;
import com.newpackage.neetcounsel.repository.NeetRepository;
import com.newpackage.neetcounsel.service.NeetService;
import com.newpackage.neetcounsel.service.PDFService;

import com.newpackage.neetcounsel.service.UserPDFService;



@Service
public class NeetServiceImpl implements NeetService{

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private NeetRepository neetRepository;

    @Autowired
    private PDFService pdfService;

    @Autowired
    private UserPDFService userPdfService;
    public Optional<Integer> getMaxAIR(int instituteCode, String category, String subCategory) {
        Aggregation aggregation = newAggregation(
            match(
                Criteria.where("InstituteCode").is(instituteCode)
                        .and("Category").is(category)
                        .and("SubCategory").is(subCategory)
            ),
            group().max("AIR").as("maxAIR")
        );

        AggregationResults<MaxAIRResult> result = mongoTemplate.aggregate(aggregation, "neet", MaxAIRResult.class);
        return result.getMappedResults().stream().findFirst().map(MaxAIRResult::getMaxAIR);
    }
    
    public byte[] generateAndSaveNeetPDF(FormDetails ob) {
    	System.out.println("start of generateAndSaveNeetPDF");
        List<Neet> result = new ArrayList<>();

        if (ob == null) {
            throw new IllegalArgumentException("Input is null");
        }

        String category = ob.getCategory();
        String subcategory = ob.getSubCategory();
        double AIR = ob.getAIR();
        String userID = ob.getUserID();

        HashSet<Integer> set = new HashSet<>();
        List<Neet> greaterthanAIRdata = neetRepository.findByAirGreaterThanEqual(AIR);

        for (Neet itr : greaterthanAIRdata) {
            if (!set.contains(itr.getInstituteCode())) {
                if ((itr.getCategory().equals(category) || itr.getCategory().equals("General"))
                        && (itr.getSubCategory().equals(subcategory) || itr.getSubCategory().equals("NO"))) {
                    result.add(itr);
                    set.add(itr.getInstituteCode());
                }
            }
        }

        byte[] pdfBytes = pdfService.createNeetPDF(result);
        String filename = "neet_" + AIR + ".pdf";

        // Save PDF to database via userPDFService
        userPdfService.saveUserPDF(userID, filename, pdfBytes);

        return pdfBytes;
    }
    // Inner class to map the aggregation result
    private static class MaxAIRResult {
        private Integer maxAIR;

        public Integer getMaxAIR() {
            return maxAIR;
        }

        public void setMaxAIR(Integer maxAIR) {
            this.maxAIR = maxAIR;
        }
    }
}
