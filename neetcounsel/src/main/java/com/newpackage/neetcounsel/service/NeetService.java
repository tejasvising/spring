package com.newpackage.neetcounsel.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.newpackage.neetcounsel.dtos.FormDetails;
import com.newpackage.neetcounsel.repository.NeetRepository;

import java.util.Optional;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;



@Service
public interface NeetService {

	 

    public Optional<Integer> getMaxAIR(int instituteCode, String category, String subCategory) ;
    public byte[] generateAndSaveNeetPDF(FormDetails ob) ;
    // Inner class to map the aggregation result
    
}
