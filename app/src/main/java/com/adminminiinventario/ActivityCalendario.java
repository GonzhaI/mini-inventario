package com.adminminiinventario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ActivityCalendario extends AppCompatActivity {
    TextView tv;
    Button botonFechaP;
    private String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);
        tv = findViewById(R.id.fecha);
        botonFechaP = findViewById(R.id.botonFecha);
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
    public void MostrarMensaje(View view){
        if (fecha != null && !fecha.isEmpty()){
            Toast.makeText(this, "Se ha guardado correctamente!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se ha ingresado una fecha", Toast.LENGTH_SHORT).show();
        }
    }
}







