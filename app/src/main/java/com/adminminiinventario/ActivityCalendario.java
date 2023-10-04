package com.adminminiinventario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class ActivityCalendario extends AppCompatActivity {
    TextView tv;
    Button botonFechaP;
    private String fecha;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        tv = findViewById(R.id.fecha);
        botonFechaP = findViewById(R.id.botonFecha);
        db = FirebaseFirestore.getInstance();
    }

    public void abrirCalendario(View view) {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ActivityCalendario.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                tv.setText(fecha);
            }
        }, anio, mes, dia);
        dpd.show();
    }

    public void MostrarMensaje(View view) {
        if (fecha != null && !fecha.isEmpty()) {
            // Aquí, enviamos la fecha a Firebase Firestore
            Map<String, Object> datos = new HashMap<>();
            datos.put("Fecha", fecha);

            // "fechaPrueba" es el nombre de tu colección en Firestore
            db.collection("fechaPrueba").add(datos)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Manejar el éxito
                            Toast.makeText(ActivityCalendario.this, "Fecha guardada correctamente!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Manejar el fallo
                            Toast.makeText(ActivityCalendario.this, "Error al guardar la fecha", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No se ha seleccionado una fecha", Toast.LENGTH_SHORT).show();
        }
    }
}







