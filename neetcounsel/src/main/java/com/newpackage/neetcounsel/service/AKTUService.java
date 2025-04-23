package com.newpackage.neetcounsel.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.newpackage.neetcounsel.dtos.AKTUdto;
import com.newpackage.neetcounsel.models.Aktu;
import com.newpackage.neetcounsel.repository.AktuRepository;
@Service
public class AKTUService {
	@Autowired
	private AktuRepository aktuRepository;
	
	@Autowired
    private PDFService pdfService;
	@Autowired
    private UserPDFService userPdfService;
	
	public List<Aktu> fetchMatchingEntries(AKTUdto data) {
        String gender = data.getGender();
        String category = data.getCategory();
        String subcategory = data.getSubcategory();
        int rank = data.getRank();

        // Determine gender filter
        String genderFilter;
        if ("Male".equalsIgnoreCase(gender)) {
            genderFilter = "Both";
        } else {
            genderFilter = "Female".equalsIgnoreCase(gender) ? "Female" : "Both";
        }

        // Create fallback category filters
        List<String> categoryFilters = new ArrayList<>();

        if ("FF".equalsIgnoreCase(subcategory)) {
            if ("Female".equalsIgnoreCase(gender) && "EWS".equalsIgnoreCase(category)) {
                categoryFilters.add(category + "(" + subcategory + ")");
                categoryFilters.add("OPEN");
                categoryFilters.add(category + "(GL)");
                categoryFilters.add("EWS(OPEN)");
            } else if ("Male".equalsIgnoreCase(gender) && "EWS".equalsIgnoreCase(category)) {
                categoryFilters.add(category + "(" + subcategory + ")");
                categoryFilters.add("OPEN");
                categoryFilters.add("EWS(OPEN)");
            } else if ("Female".equalsIgnoreCase(gender)) {
                categoryFilters.add(category + "(" + subcategory + ")");
                categoryFilters.add("OPEN");
                categoryFilters.add(category + "(Girl)");
                categoryFilters.add(category);
            } else {
                categoryFilters.add(category + "(" + subcategory + ")");
                categoryFilters.add("OPEN");
                categoryFilters.add(category + "(OPEN)");
            }
        } else if ("AF".equalsIgnoreCase(subcategory) || "PH".equalsIgnoreCase(subcategory)) {
            categoryFilters.add(category + "(" + subcategory + ")");
            categoryFilters.add("OPEN");
            if ("Female".equalsIgnoreCase(gender)) {
                categoryFilters.add(category + "(GL)");
            }
            categoryFilters.add(category + "(OPEN)");
        } else if ("OPEN".equalsIgnoreCase(category)) {
            if ("Female".equalsIgnoreCase(gender)) {
                categoryFilters.add(category);
                categoryFilters.add(category + "(GL)");
            } else {
                categoryFilters.add(category);
            }
        } else {
            categoryFilters.add(category);
            categoryFilters.add("OPEN");
            if ("Female".equalsIgnoreCase(gender)) {
                categoryFilters.add(category + "(GL)");
            }
        }

        return aktuRepository.findByGenderAndCategoryInAndClosingRankGreaterThanEqual(
                genderFilter,
                categoryFilters,
                rank
        );
    }

	public List<Aktu> fetchMatchingEntries2(AKTUdto data) {
	    List<String> genderFilter;
	    if ("Male".equalsIgnoreCase(data.getGender())) {
	        genderFilter = List.of("Both");
	    } else if ("Female".equalsIgnoreCase(data.getGender())) {
	        genderFilter = List.of("Both", "Female");
	    } else {
	        genderFilter = List.of("Both", "Male", "Female");
	    }

	    List<String> categoryFilter = List.of(data.getCategory(), "OPEN");

	    return aktuRepository.findByGenderInAndCategoryInAndClosingRankGreaterThanEqual(
	            genderFilter,
	            categoryFilter,
	            data.getRank()
	    );
	}

	public List<Aktu> fetchMatchingEntries3(AKTUdto data) {
	    List<String> genderFilter;
	    if ("Male".equalsIgnoreCase(data.getGender())) {
	        genderFilter = List.of("Both");
	    } else if ("Female".equalsIgnoreCase(data.getGender())) {
	        genderFilter = List.of("Both", "Female");
	    } else {
	        genderFilter = List.of("Both", "Male", "Female");
	    }

	    List<String> categoryFilter = List.of(data.getCategory(), "OPEN");

	    return aktuRepository.findByGenderInAndCategoryInAndClosingRankGreaterThanEqual(
	            genderFilter,
	            categoryFilter,
	            data.getRank()
	    );
	}

    private List<Aktu> fetchGenericFallback(AKTUdto data) {
        String gender = data.getGender();
        int rank = data.getRank();
        String genderFilter = "Male".equalsIgnoreCase(gender) ? "Both" : "Female".equalsIgnoreCase(gender) ? "Female" : "Both";
        List<String> categories = List.of(data.getCategory(), "OPEN");

        return aktuRepository.findByGenderAndCategoryInAndClosingRankGreaterThanEqual(
                genderFilter, categories, rank
        );
    }
	public byte[] generateAndSaveAKTUPDF(AKTUdto ob) {
		List<Aktu> nirf=fetchMatchingEntries(ob);
		List<Aktu> placement=fetchMatchingEntries2(ob);
		List<Aktu> recommended=fetchMatchingEntries3(ob);
		int rank=ob.getRank();
		byte[] pdfBytes = pdfService.createAktuPDF(nirf,placement,recommended);
        String filename = "aktu_" + rank + ".pdf";
        String userID=ob.getUserID();
        // Save PDF to database via userPDFService
        userPdfService.saveUserPDF(userID, filename, pdfBytes);

        return pdfBytes;
	}
}
