package com.conleos.pdf;

import com.conleos.common.FormUtil;
import com.conleos.common.Role;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.FormStatus;
import com.conleos.data.entity.User;
import com.conleos.data.service.FormService;
import com.lowagie.text.*;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.vaadin.flow.component.UI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
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
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM uuuu", Locale.GERMAN);
        document.add(new Paragraph("Ausbildungsnachweis Nr. " + number + " Woche vom " + formatter.format(form.getMondayDate()) + " bis " + formatter.format(form.getMondayDate().plusDays(6))));

        document.add(new Paragraph(" "));
        document.add(new Paragraph("In dieser Woche gearbeitet: " + FormUtil.getLabelFromTotalTimeOfForm(form) + " h"));

        generateFormEntry(document, form, form.getMonday(), form.getMondayDate().plusDays(0));
        generateFormEntry(document, form, form.getTuesday(), form.getMondayDate().plusDays(1));
        generateFormEntry(document, form, form.getWednesday(), form.getMondayDate().plusDays(2));
        generateFormEntry(document, form, form.getThursday(), form.getMondayDate().plusDays(3));
        generateFormEntry(document, form, form.getFriday(), form.getMondayDate().plusDays(4));
        generateFormEntry(document, form, form.getSaturday(), form.getMondayDate().plusDays(5));
        generateFormEntry(document, form, form.getSunday(), form.getMondayDate().plusDays(6));

        if (form.getUserWhoSignedOrRejected().getFullName() != null && form.getStatus().equals(FormStatus.Signed)) {
            document.add(new Paragraph("Unterzeichnet von: " + form.getUserWhoSignedOrRejected().getFullName()));
            Image image = null;
            try {
                image = Image.getInstance(form.getUserWhoSignedOrRejected().getSignatureImage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Optionally, set image properties
            image.scaleToFit(200, 200); // Scale the image to fit within 200x200 pixels
            image.setAlignment(com.lowagie.text.Image.ALIGN_CENTER);

            document.add(image);
        }
    }

    private static void generateFormEntry(Document document, Form form, Form.FormEntry entry, LocalDate date) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM uuuu", Locale.GERMAN);

        String dayLabel = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY);
        dayLabel += ", " + formatter.format(date);
        Paragraph paragraph = new Paragraph(dayLabel);
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(paragraph);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);
        table.addCell(createCell(entry.getDescription(), Element.ALIGN_CENTER));
        table.addCell(createCell(entry.getKindOfWork().toString(), Element.ALIGN_CENTER));
        document.add(table);
        document.add(new Paragraph(" "));
    }

    private static PdfPCell createCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPhrase(new com.lowagie.text.Phrase(text));
        return cell;
    }

}
