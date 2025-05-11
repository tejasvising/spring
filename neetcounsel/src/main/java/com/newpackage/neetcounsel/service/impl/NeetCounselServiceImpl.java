package com.newpackage.neetcounsel.service.impl;

import com.newpackage.neetcounsel.dtos.FormDetails;
import com.newpackage.neetcounsel.dtos.Neet;
import com.newpackage.neetcounsel.repository.NeetRepository;
import com.newpackage.neetcounsel.service.NeetCounselService;
import com.newpackage.neetcounsel.service.PDFService;
import com.newpackage.neetcounsel.service.UserPDFService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class NeetCounselServiceImpl implements NeetCounselService {

    private final NeetRepository neetRepository;
    private final PDFService pdfService;
    private final UserPDFService userPDFService;

    @Override
    public ResponseEntity<byte[]> processNeetForm(FormDetails ob) {
        Set<Integer> instituteSet = new HashSet<>();
        List<Neet> filtered = new ArrayList<>();

        for (Neet entry : neetRepository.findByAirGreaterThanEqual(ob.getAIR())) {
            if (!instituteSet.contains(entry.getInstituteCode())
                && (entry.getCategory().equals(ob.getCategory()) || entry.getCategory().equals("General"))
                && (entry.getSubCategory().equals(ob.getSubCategory()) || entry.getSubCategory().equals("NO"))) {
                filtered.add(entry);
                instituteSet.add(entry.getInstituteCode());
            }
        }

        byte[] pdf = pdfService.createPDF(filtered);
        String filename = "neet_" + ob.getAIR() + ".pdf";
        userPDFService.saveUserPDF(ob.getUserID(), filename, pdf);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", filename);
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}

