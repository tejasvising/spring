package com.newpackage.neetcounsel.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.newpackage.neetcounsel.dtos.Neet;
import com.newpackage.neetcounsel.models.Aktu;
import com.newpackage.neetcounsel.service.PDFService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PDFServiceImpl implements PDFService {

    /*public byte[] generateConditionalPDF(String exam, List<?> entries) {
        if ("neet".equalsIgnoreCase(exam)) {
            return createNeetPDF((List<Neet>) entries);
        } else if ("aktu".equalsIgnoreCase(exam)) {
            return createAktuPDF(List<Aktu> nirf,List<Aktu> placement,List<Aktu> recommended);
        } else {
            throw new IllegalArgumentException("Unsupported exam type");
        }
    }*/

    // --- NEET PDF Generation ---
    @Override
    public byte[] createNeetPDF(List<Neet> result) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("NEET Counseling Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(5);
            float[] columnWidths = new float[]{10f, 38f, 10f, 13f, 10f};
            table.setWidths(columnWidths);

            addNeetTableHeader(table);
            addNeetRows(table, result);
            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        return baos.toByteArray();
    }

    private void addNeetTableHeader(PdfPTable table) {
        Stream.of("Institute Code", "Institute", "Admitted Round", "Allotted Category", "Subject")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPadding(5);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addNeetRows(PdfPTable table, List<Neet> result) {
        for (Neet itr : result) {
            table.addCell(String.valueOf(itr.getInstituteCode()));
            table.addCell(itr.getInstitute());
            table.addCell(itr.getAdmittedRound());
            table.addCell(itr.getAllottedCategory());
            table.addCell(itr.getSubject());
        }
    }

    // --- AKTU PDF Generation (Based on FastAPI logic) ---
    @Override
    public byte[] createAktuPDF(List<Aktu> nirf,List<Aktu> placement,List<Aktu> recommended) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            Font cellFont = new Font(Font.FontFamily.HELVETICA, 9);

            Paragraph title = new Paragraph("AKTU Counselling Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            PdfPTable table = new PdfPTable(8);
            table.setWidths(new float[]{2.5f, 2f, 1f, 1f, 1f, 1f, 1f, 1f});
            
            Paragraph title1 = new Paragraph("NIRF report", titleFont);
            title1.setAlignment(Element.ALIGN_CENTER);
            title1.setSpacingAfter(20);
            document.add(title1);
            addAktuTableHeader(table, headerFont);
            addAktuRows(table, nirf, cellFont);
            document.add(table);
            
            Paragraph title2 = new Paragraph("Placement report", titleFont);
            title2.setAlignment(Element.ALIGN_CENTER);
            title2.setSpacingAfter(20);
            document.add(title2);
            PdfPTable table1 = new PdfPTable(8);
            table1.setWidths(new float[]{2.5f, 2f, 1f, 1f, 1f, 1f, 1f, 1f});
            addAktuTableHeader(table1, headerFont);
            addAktuRows(table1, placement, cellFont);
            document.add(table1);
            
            Paragraph title3 = new Paragraph("Placement report", titleFont);
            title3.setAlignment(Element.ALIGN_CENTER);
            title3.setSpacingAfter(20);
            document.add(title3);
            PdfPTable table2 = new PdfPTable(8);
            table2.setWidths(new float[]{2.5f, 2f, 1f, 1f, 1f, 1f, 1f, 1f});
            addAktuTableHeader(table2, headerFont);
            addAktuRows(table2, recommended, cellFont);
            document.add(table2);
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
        return baos.toByteArray();
    }

    private void addAktuTableHeader(PdfPTable table, Font font) {
        String[] headers = {
                "Institute", "Program", "Round", "Quota",
                "Category", "Gender", "Open Rank", "Close Rank"
        };
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setBackgroundColor(new BaseColor(133, 193, 233)); // #85C1E9
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }
    }

    private void addAktuRows(PdfPTable table, List<Aktu> result, Font font) {
        BaseColor rowColor = new BaseColor(232, 248, 245); // #E8F8F5
        for (Aktu entry : result) {
            addCell(table, entry.getInstitute(), font, rowColor);
            addCell(table, entry.getProgram(), font, rowColor);
            addCell(table, entry.getRound(), font, rowColor);
            addCell(table, entry.getQuota(), font, rowColor);
            addCell(table, entry.getCategory(), font, rowColor);
            addCell(table, entry.getGender(), font, rowColor);
            addCell(table, entry.getOpeningRank(), font, rowColor);
            addCell(table, entry.getClosingRank(), font, rowColor);
        }
    }

    private void addCell(PdfPTable table, Object value, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : "N/A", font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(cell);
    }

	

	@Override
	public byte[] createPDF(List<Neet> result) {
		// TODO Auto-generated method stub
		return null;
	}
}
