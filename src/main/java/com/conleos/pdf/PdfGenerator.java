package com.conleos.pdf;

import com.conleos.common.FormUtil;
import com.conleos.common.Role;
import com.conleos.data.entity.Form;
import com.conleos.data.entity.FormStatus;
import com.conleos.data.entity.User;
import com.conleos.data.service.FormService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
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

    private static Font font = null;

    private static void generateAllFormPages(Document document, User trainee) {
        if (!trainee.getRole().equals(Role.Trainee)) {
            throw new RuntimeException("Role has to be Trainee.");
        }

        try {
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            font = new Font(baseFont, 9, Font.NORMAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        document.add(new Paragraph("Dieses Dokument wurde maschinell erzeugt.", font));
        document.add(new Paragraph("Name: " + user.getLastName() + " Vorname: " + user.getFirstName(), font));
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM uuuu", Locale.GERMAN);
        document.add(new Paragraph("Ausbildungsnachweis Nr. " + number + " Woche vom " + formatter.format(form.getMondayDate()) + " bis " + formatter.format(form.getMondayDate().plusDays(6)), font));

        document.add(new Paragraph(" ", font));
        document.add(new Paragraph("In dieser Woche gearbeitet: " + FormUtil.getLabelFromTotalTimeOfForm(form) + " h", font));

        generateFormEntry(document, form, form.getMonday(), form.getMondayDate().plusDays(0));
        generateFormEntry(document, form, form.getTuesday(), form.getMondayDate().plusDays(1));
        generateFormEntry(document, form, form.getWednesday(), form.getMondayDate().plusDays(2));
        generateFormEntry(document, form, form.getThursday(), form.getMondayDate().plusDays(3));
        generateFormEntry(document, form, form.getFriday(), form.getMondayDate().plusDays(4));
        generateFormEntry(document, form, form.getSaturday(), form.getMondayDate().plusDays(5));
        generateFormEntry(document, form, form.getSunday(), form.getMondayDate().plusDays(6));

        if (form.getUserWhoSignedOrRejected().getFullName() != null && form.getStatus().equals(FormStatus.Signed)) {
            document.add(new Paragraph("Unterzeichnet von: " + form.getUserWhoSignedOrRejected().getFullName(), font));
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
        if (FormUtil.getTotalMinutesFromEntry(entry) <= 0 && (entry.getDescription() == null || entry.getDescription().isEmpty())) {
            return;
        }
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd. MMMM uuuu", Locale.GERMAN);

        String dayLabel = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY);
        dayLabel += ", " + formatter.format(date);
        final String dayInfo = String.format("Von %s bis %s. Pausenzeit von %d Minuten.   %s", FormUtil.getLabelFromTotalTime(entry.getBegin().get(ChronoField.MINUTE_OF_DAY)), FormUtil.getLabelFromTotalTime(entry.getEnd().get(ChronoField.MINUTE_OF_DAY)), entry.getPause(), entry.getKindOfWork().toString());
        Paragraph paragraph = new Paragraph(dayInfo + " - " + dayLabel, font);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(paragraph);
        document.add(new Paragraph(" ", font));

        PdfPTable table = new PdfPTable(1);
        table.addCell(createCell(entry.getDescription()));
        document.add(table);
        document.add(new Paragraph(" ", font));
    }

    private static PdfPCell createCell(String text) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPhrase(new com.lowagie.text.Phrase(text, font));
        cell.setColspan(2);
        return cell;
    }

}
