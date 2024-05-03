package com.conleos.pdf;

import com.conleos.common.Role;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.User;
import com.conleos.data.service.FormService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.vaadin.flow.component.UI;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class PdfGenerator {

    public static ByteArrayOutputStream generateSamplePdf() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter pdf = PdfWriter.getInstance(document, baos);

        document.open();

        document.add(new Paragraph("Dieses Dokument wurde maschinell erzeugt."));

        document.close();

        return baos;
    }

    public static ByteArrayOutputStream generateNachweisPdf(User trainee) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter pdf = PdfWriter.getInstance(document, baos);

        document.open();

        generateAllFormPages(document, trainee);

        document.close();

        return baos;
    }

    private static void generateAllFormPages(Document document, User trainee) {
        if (!trainee.getRole().equals(Role.Trainee)) {
            throw new RuntimeException("Role has to be Trainee.");
        }

        List<Form> forms = FormService.getInstance().getFormsByOwner(trainee);

        int number = 1;
        for (Form form : forms) {
            generateSinglePageForm(document, form, number++);
            document.newPage();
        }

    }

    private static void generateSinglePageForm(Document document, Form form, int number) {
        User user = form.getOwner();
        document.add(new Paragraph("Dieses Dokument wurde maschinell erzeugt."));
        document.add(new Paragraph("Name: " + user.getLastName() + " Vorname: " + user.getFirstName()));
        document.add(new Paragraph("Ausbildungsnachweis Nr. " + number + " Woche vom " + form.getMondayDate() + " bis " + form.getMondayDate().plusDays(6)));

        document.add(new Paragraph(" "));

        for (int day = 0; day < 7; day++) {
            LocalDate date = form.getMondayDate().plusDays(day);
            List<Form.FormEntry> entries = form.getEntriesByDate(date);

            if (entries.isEmpty()) {
                continue;
            }

            String dayLabel = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY);
            dayLabel += ", " + date.toString();
            Paragraph paragraph = new Paragraph(dayLabel);
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(paragraph);
            document.add(new Paragraph(" "));

            for (Form.FormEntry entry : entries) {
                PdfPTable table = new PdfPTable(2);
                table.addCell(createCell(entry.getDescription(), Element.ALIGN_CENTER));
                table.addCell(createCell(entry.getKindOfWork().toString(), Element.ALIGN_CENTER));
                document.add(table);
            }
            document.add(new Paragraph(" "));

        }

    }

    private static PdfPCell createCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPhrase(new com.lowagie.text.Phrase(text));
        return cell;
    }

}
