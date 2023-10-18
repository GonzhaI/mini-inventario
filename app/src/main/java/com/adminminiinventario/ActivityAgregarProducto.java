package com.adminminiinventario;

import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.app.DatePickerDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.adminminiinventario.Productos;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class ActivityAgregarProducto extends AppCompatActivity {
    TextView MostrarFecha;
    EditText nombreProducto, cdBarrasProducto, cantidadProducto, valorProducto;
    private Timestamp fechaVencimiento;
    Button btnCamaraCdBarras, btnCamaraImagenProducto, btnAgregarProducto;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        // Inicializar los elementos de la interfaz
        MostrarFecha = findViewById(R.id.MostrarFecha);
        nombreProducto = findViewById(R.id.producto);
        cdBarrasProducto = findViewById(R.id.cdBarra);
        cantidadProducto = findViewById(R.id.cantidad);
        valorProducto = findViewById(R.id.valor);
        btnAgregarProducto = findViewById(R.id.btn_agregar_producto);
        btnCamaraCdBarras = findViewById(R.id.btnScanCodigoBarras);
        btnCamaraImagenProducto = findViewById(R.id.btn_imagen_producto);

        db = FirebaseFirestore.getInstance();

        TextView negocioNombreTextView = findViewById(R.id.negocioNombre);
        String negocio = getIntent().getStringExtra("negocio");
        negocioNombreTextView.setText(negocio);

        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idNegocio = negocio.toLowerCase();
                String producto = nombreProducto.getText().toString().trim().toLowerCase();
                String cdBarras = cdBarrasProducto.getText().toString().trim().toLowerCase();
                String cantidad = cantidadProducto.getText().toString().trim().toLowerCase();
                String valor = valorProducto.getText().toString().trim().toLowerCase();

                int cantidadInt = Integer.parseInt(cantidad);
                int valorInt = Integer.parseInt(valor);

                if (producto.isEmpty()) {
                    showMessage("Por favor, completa todos los campos.");
                } else {
                    // Crear una nueva instancia de Productos
                    Productos nuevoProducto = new Productos(idNegocio, cantidadInt, cdBarras, producto, valorInt, fechaVencimiento);

                    // Guardar en Firebase Firestore
                    db.collection("productos")
                            .add(nuevoProducto)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    showMessage("Producto agregado con éxito.");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessage("Error al agregar el producto: " + e.getMessage());
                                }
                            });
                }
            }
        });

        btnCamaraCdBarras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lanza la actividad de escaneo de codigo de barras
                IntentIntegrator intent = new IntentIntegrator(ActivityAgregarProducto.this);
                intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intent.setPrompt("Escanear Codigo");
                intent.setCameraId(0);
                intent.setBeepEnabled(true);
                intent.setBarcodeImageEnabled(true);
                intent.setOrientationLocked(true);
                intent.initiateScan();
            }
        });


    }

    public void abrirCalendarioAlimentos(View view) {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ActivityAgregarProducto.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                // Crea un Timestamp con la fecha seleccionada
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                fechaVencimiento = new Timestamp(calendar.getTime());

                // Actualiza el TextView con la fecha formateada
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String fechaFormateada = sdf.format(calendar.getTime());
                MostrarFecha.setText(fechaFormateada);

                // Imprime la fecha en la consola
                Log.d("Fecha", "Fecha seleccionada: " + fechaFormateada);
            }
        }, anio, mes, dia);
        dpd.show();
    }

    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Notificacion")
                .setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Lectura Cancelada", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                cdBarrasProducto.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }





}
