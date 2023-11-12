package com.adminminiinventario;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminminiinventario.adapter.InventarioAdapter;
import com.adminminiinventario.adapter.Producto;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class inventario extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private InventarioAdapter adapter;
    private List<Producto> productList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private ListenerRegistration userDataListener;
    private String negocio;

    private String titulotext = "Informe inventario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        negocio = getIntent().getStringExtra("negocio");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.mostrar_casillas_prin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventarioAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        Button botonGenerarInforme = findViewById(R.id.botonGenerarInforme);

        botonGenerarInforme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llama a la función para obtener productos y generar el informe PDF
                obtenerProductosYGenerarInforme();
            }
        });

        if (currentUser != null) {
            String uid = currentUser.getUid();
            userDataListener = db.collection("usuario").document(uid)
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String negocio = documentSnapshot.getString("negocio");
                            // Ahora, con el negocio del usuario, obtén los productos relacionados
                            obtenerProductosDelNegocio(negocio);
                        }
                    });
        }
    }

    // Método para obtener los productos del negocio
    private void obtenerProductosDelNegocio(String negocio) {
        if (negocio != null) {
            db.collection("productos")
                    .whereEqualTo("id_negocio", negocio.toLowerCase())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            productList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Producto producto = documentToProducto(document);
                                if (producto != null) {
                                    productList.add(producto);
                                }
                            }

                            adapter.notifyDataSetChanged();

                            // Después de obtener los productos, genera el informe PDF
                            generarInformePDF(productList);
                        }
                    });
        } else {
            Log.e("InventarioActivity", "El negocio es nulo");
        }
    }

    // Método para convertir un documento Firestore a un objeto Producto
    private Producto documentToProducto(QueryDocumentSnapshot document) {
        try {
            String idNegocio = document.getString("id_negocio");
            int cantidad = document.getLong("cantidad").intValue();
            String codigo_barra = String.valueOf(document.getLong("cdBarra"));
            String nombreProducto = document.getString("producto");
            double valor = document.getDouble("valor");
            Timestamp fechaVencimientoTimestamp = document.getTimestamp("fechaVencimiento");
            String imagenURL = document.getString("imagenUrl");

            Date fechaVencimientoDate = null;
            if (fechaVencimientoTimestamp != null) {
                fechaVencimientoDate = fechaVencimientoTimestamp.toDate();
            }

            Producto producto = new Producto(cantidad, idNegocio, nombreProducto, valor, fechaVencimientoDate, imagenURL, codigo_barra);
            producto.setValor(cantidad);

            return producto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para obtener productos y generar el informe PDF
    private void obtenerProductosYGenerarInforme() {
        obtenerProductosDelNegocio(negocio);
    }

    // Método para generar el informe PDF
    private void generarInformePDF(List<Producto> productos) {
        // Obtener el directorio específico de la aplicación en el almacenamiento externo
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "informe");

        // Verificar si el directorio existe, si no, intentar crearlo
        if (!directory.exists() && !directory.mkdirs()) {
            Log.e("InventarioActivity", "No se pudo crear el directorio.");
            return;
        }

        // Cambiar el nombre del archivo y obtener la ruta completa del archivo PDF
        String filePath = new File(directory, "informe.pdf").getAbsolutePath();

        try {
            // Crear un archivo PDF y un documento
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filePath));
            Document doc = new Document(pdfDoc);

            // Agregar título al PDF
            Paragraph titulo = new Paragraph(titulotext);
            doc.add(titulo);

            // Agregar elementos al PDF
            for (Producto producto : productos) {
                Paragraph para = new Paragraph(producto.getNombre_producto() +
                        " | " + producto.getFechaVencimiento() +
                        " | " + producto.getCantidad() +
                        " | " + "$" + producto.getValor() +
                        "\n");
                doc.add(para);
            }

            // Cerrar el documento
            doc.close();

            // Mostrar un mensaje de éxito
            showToast("Informe PDF creado en: " + filePath);
            Log.v("InventarioActivity", filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Método para mostrar mensajes en un Toast
    private void showToast(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}
