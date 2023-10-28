package com.adminminiinventario;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.util.List;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Generar_Informe extends AppCompatActivity {

    String titulotext = "Este es el título del documento";
    List<String> items = new ArrayList<>();



    public void createPDF(String filePath, String content) {
        try {
            // Crear un archivo PDF y un documento
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filePath));
            Document doc = new Document(pdfDoc);

            // Agregar contenido al PDF
            for (String item : items) {
                Paragraph para = new Paragraph(item);
                doc.add(para);
            }

            // Cerrar el documento
            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Agregar elementos al informe
    private void agregarElemento(String elemento) {
        items.add(elemento);
        agregarElemento("Elemento 1");
        agregarElemento("Elemento 2");
    }

    // Lógica para generar el informe PDF
    private void generarInformePDF() {
        String filePath = "/ruta/del/archivo/informe.pdf";
        createPDF(filePath, titulotext);
    }
}
