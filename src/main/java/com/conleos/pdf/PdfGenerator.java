package com.conleos.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;

public class PdfGenerator {

    public static ByteArrayOutputStream generateSamplePdf() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter pdf = PdfWriter.getInstance(document, baos);

        document.open();

        document.add(new Paragraph("This content was generated on the server."));

        document.close();

        return baos;
    }

}
