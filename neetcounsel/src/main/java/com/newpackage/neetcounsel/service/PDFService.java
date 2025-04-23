package com.newpackage.neetcounsel.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.newpackage.neetcounsel.dtos.Neet;
import com.newpackage.neetcounsel.models.Aktu;
@Service
public interface PDFService {
	
	// void addTableHeader(PdfPTable table);
	 
	// void addRows(PdfPTable table,List<Neet> result);
	 
	 byte[] createPDF(List<Neet> result); 
	 public byte[] createNeetPDF(List<Neet> result);
	 public byte[] createAktuPDF(List<Aktu> nirf,List<Aktu> placement,List<Aktu> recommended);
	// public byte[] generateConditionalPDF(String exam, List<?> entries);
	 
}
