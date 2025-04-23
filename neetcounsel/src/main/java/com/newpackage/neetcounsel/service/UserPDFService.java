package com.newpackage.neetcounsel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpackage.neetcounsel.dtos.UserPDF;
import com.newpackage.neetcounsel.repository.UserPDFRepository;

@Service
public class UserPDFService {

    @Autowired
    private UserPDFRepository userPDFRepository;

    public void saveUserPDF(String userID, String filename, byte[] pdfBytes) {
      //  String fileName = examType + "_" + AIR + ".pdf";
        UserPDF userPDF = new UserPDF(userID, filename, pdfBytes);
        userPDFRepository.save(userPDF);
    }
}

