package com.adminminiinventario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.adminminiinventario.model.Productos;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityAgregarProducto extends AppCompatActivity {

    TextView MostrarFecha;
    EditText nombreProducto, cdBarrasProducto, cantidadProducto, valorProducto;
    private ImageView imagenProducto;
    Button btnCamaraImagenProducto, btnAgregarProducto;
    private static final int REQUEST_IMAGE_CAPTURE_PRODUCT = 2;
    FirebaseFirestore db;
    private Timestamp fechaVencimiento;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        MostrarFecha = findViewById(R.id.MostrarFecha);
        nombreProducto = findViewById(R.id.producto);
        cdBarrasProducto = findViewById(R.id.cdBarra);
        cantidadProducto = findViewById(R.id.cantidad);
        valorProducto = findViewById(R.id.valor);
        btnAgregarProducto = findViewById(R.id.btn_agregar_producto);
        btnCamaraImagenProducto = findViewById(R.id.btn_imagen_producto);
        imagenProducto = findViewById(R.id.imagen_producto);
        db = FirebaseFirestore.getInstance();

        btnCamaraImagenProducto.setOnClickListener(v -> dispatchTakePictureIntent());

        MostrarFecha.setOnClickListener(v -> abrirCalendarioAlimentos());

        btnAgregarProducto.setOnClickListener(view -> agregarProductoAFirebase());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_PRODUCT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE_PRODUCT && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imagenProducto.setImageBitmap(imageBitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void abrirCalendarioAlimentos() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ActivityAgregarProducto.this, (datePicker, year, month, dayOfMonth) -> {
            calendario.set(year, month, dayOfMonth);
            fechaVencimiento = new Timestamp(calendario.getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fechaFormateada = sdf.format(calendario.getTime());
            MostrarFecha.setText(fechaFormateada);
        }, anio, mes, dia);
        dpd.show();
    }

    private void agregarProductoAFirebase() {
        String producto = nombreProducto.getText().toString().trim().toLowerCase();
        String cdBarras = cdBarrasProducto.getText().toString().trim().toLowerCase();
        String cantidad = cantidadProducto.getText().toString().trim().toLowerCase();
        String valor = valorProducto.getText().toString().trim().toLowerCase();

        int cantidadInt = Integer.parseInt(cantidad);
        int valorInt = Integer.parseInt(valor);

        if (producto.isEmpty()) {
            showMessage("Por favor, completa todos los campos.");
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("imagenes_productos").child(cdBarras + ".jpg");

            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> {
                showMessage("Error al subir la imagen: " + exception.getMessage());
            }).addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    Map<String, Object> datos = new HashMap<>();
                    datos.put("cantidad", cantidadInt);
                    datos.put("cdBarras", cdBarras);
                    datos.put("producto", producto);
                    datos.put("valor", valorInt);
                    datos.put("imagenUrl", imageUrl);

                    if (fechaVencimiento != null) {
                        datos.put("fechaVencimiento", fechaVencimiento);
                    }

                    db.collection("productos")
                            .add(datos)
                            .addOnSuccessListener(documentReference -> {
                                showMessage("Producto agregado con éxito.");
                            })
                            .addOnFailureListener(e -> {
                                showMessage("Error al agregar el producto: " + e.getMessage());
                            });
                });
            });
        }
    }

    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Notificación")
                .setPositiveButton("Aceptar", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
