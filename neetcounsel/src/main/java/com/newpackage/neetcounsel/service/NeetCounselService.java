package com.newpackage.neetcounsel.service;



import com.newpackage.neetcounsel.dtos.FormDetails;
import org.springframework.http.ResponseEntity;

public interface NeetCounselService {
    ResponseEntity<byte[]> processNeetForm(FormDetails formDetails);
}
