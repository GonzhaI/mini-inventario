package com.adminminiinventario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;

public class ActivityCalendario extends AppCompatActivity {
    TextView tv;
    Button botonFechaP;

    EditText nombreDistribuidor;

    private Timestamp fechaTimestamp;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        nombreDistribuidor = findViewById(R.id.nombreDistribuidor);
        tv = findViewById(R.id.fecha);
        botonFechaP = findViewById(R.id.botonFecha);
        db = FirebaseFirestore.getInstance();
    }

    public void abrirCalendario(View view) {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ActivityCalendario.this, (datePicker, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            fechaTimestamp = new Timestamp(calendar.getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fechaFormateada = sdf.format(calendar.getTime());
            tv.setText(fechaFormateada);
        }, anio, mes, dia);
        dpd.show();
    }

    public void MostrarMensaje(View view) {
        if (fechaTimestamp != null) {
            // Obtener la ID del negocio
            String idNegocio = getIntent().getStringExtra("negocio");

            // Obtener el nombre del distribuidor
            String nombreDistribuidorStr = nombreDistribuidor.getText().toString().trim();

            if (nombreDistribuidorStr.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa el nombre del distribuidor", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear un objeto Distribuidor
            Distribuidor distribuidor = new Distribuidor(idNegocio, nombreDistribuidorStr, fechaTimestamp);

            // Agregar el distribuidor a Firebase
            db.collection("llegadaMercaderia")
                    .add(distribuidor)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ActivityCalendario.this, "Distribuidor agregado con éxito", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ActivityCalendario.this, "Error al agregar el distribuidor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No se ha seleccionado una fecha", Toast.LENGTH_SHORT).show();
        }
    }
}






