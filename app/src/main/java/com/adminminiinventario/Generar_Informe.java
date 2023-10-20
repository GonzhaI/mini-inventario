package com.adminminiinventario;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Generar_Informe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generar_informe);
    }

    public void createPDF(String filePath, String content) {
        try {
            // Crear un archivo PDF y un documento
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filePath));
            Document doc = new Document(pdfDoc);

            // Agregar contenido al PDF
            Paragraph para = new Paragraph(content);
            doc.add(para);

            // Cerrar el documento
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}