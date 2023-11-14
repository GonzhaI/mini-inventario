package com.adminminiinventario;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityGraficaGanancias extends AppCompatActivity {

    private TableLayout tablaGanancias;
    private EditText editTextGananciaDiaria;
    private FirebaseFirestore db;
    private String ultimoMesRegistrado = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_ganancias);

        tablaGanancias = findViewById(R.id.tablaGanancias);
        editTextGananciaDiaria = findViewById(R.id.editTextGananciaDiaria);
        db = FirebaseFirestore.getInstance();

        // Obtener el número de días en el mes actual
        Calendar calendar = Calendar.getInstance();
        int diasEnElMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Crear un encabezado para la tabla
        TableRow headerRow = new TableRow(this);

        // Agregar celdas al encabezado a la tabla
        headerRow.addView(createTextView("Dia", Gravity.CENTER));
        headerRow.addView(createTextView("Ganancia", Gravity.CENTER));

        // Agregar la fila de encabezado a la tabla
        tablaGanancias.addView(headerRow);

        // Crear filas para los días del mes actual
        for (int dia = 1; dia <= diasEnElMes; dia++) {
            TableRow row = new TableRow(this);

            // Agregar celda para el día
            row.addView(createTextView(String.valueOf(dia), Gravity.CENTER));

            // Agregar celda para la ganancia
            row.addView(createTextView("", Gravity.CENTER));

            // Agregar la fila a la tabla
            tablaGanancias.addView(row);
        }

        // Filtro para que solo se acepten numeros
        editTextGananciaDiaria.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // Permitir solo dígitos
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        }});

        // Boton para guardar ganancias
        Button botonGuardarGanancia = findViewById(R.id.botonGuardarGanancia);
        botonGuardarGanancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la ganancia ingresada
                String gananciaDiaria = editTextGananciaDiaria.getText().toString();

                guardarGananciaFirestore(gananciaDiaria);

                // Obtener el día actual
                int diaActual = calendar.get(Calendar.DAY_OF_MONTH);

                // Actualizar la tabla con la ganancia ingresada
                actualizarTabla(diaActual, gananciaDiaria);

                // Limpiar el campo de entrada
                editTextGananciaDiaria.setText("");
            }
        });

        // Verificar si el mes actual es diferente al mes registrado en la última entrada
        verificarYReiniciarTabla();
    }

    private void actualizarTabla(int diaActual, String gananciaDiaria) {
        // Verificar si ya existe una fila para el día actual
        boolean filaExistente = false;
        for (int i = 1; i < tablaGanancias.getChildCount(); i++) {
            TableRow row = (TableRow) tablaGanancias.getChildAt(i);
            TextView textViewDia = (TextView) row.getChildAt(0);

            if (Integer.parseInt(textViewDia.getText().toString()) == diaActual) {
                // La fila ya existe, actualizar la celda de ganancia
                TextView textViewGanancia = (TextView) row.getChildAt(1);
                textViewGanancia.setText(gananciaDiaria);
                filaExistente = true;
                break;
            }
        }

        // Si no existe una fila para el día actual, crear una nueva fila
        if (!filaExistente) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(String.valueOf(diaActual), Gravity.CENTER));
            row.addView(createTextView(gananciaDiaria, Gravity.CENTER));
            tablaGanancias.addView(row);
        }
    }

    private void verificarYReiniciarTabla() {
        // Obtener el mes actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-yyyy", Locale.getDefault());
        String mesActual = dateFormat.format(new Date());

        // Obtener el último mes registrado en la tabla de Firestore
        db.collection("ganancias")
                .orderBy("mes", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (com.google.firebase.firestore.QueryDocumentSnapshot document : task.getResult()) {
                                String ultimoMesRegistrado = document.getString("mes");

                                // Comparar el mes actual con el último mes registrado
                                if (!mesActual.equals(ultimoMesRegistrado)) {
                                    // Si son diferentes, reiniciar la tabla
                                    reiniciarTabla();
                                }
                            }
                        }
                    }
                });
    }

    private void reiniciarTabla() {
        // Limpiar todas las filas excepto la primera (encabezado)
        tablaGanancias.removeViews(1, tablaGanancias.getChildCount() - 1);

        // Actualizar el mes registrado en Firestore
        Map<String, Object> nuevaEntrada = new HashMap<>();
        nuevaEntrada.put("mes", new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date()));

        db.collection("ganancias")
                .add(nuevaEntrada)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Éxito al reiniciar la tabla
                        } else {
                            // Manejar el error
                        }
                    }
                });
    }

    private void guardarGananciaFirestore(String gananciaDiaria) {
        // Obtener la fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date());

        // Crear un mapa con los datos a almacenar
        Map<String, Object> gananciaMap = new HashMap<>();
        gananciaMap.put("ganancia", gananciaDiaria);
        gananciaMap.put("fecha", fechaActual);
        gananciaMap.put("mes", new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date()));

        // Almacenar la ganancia en Firestore
        db.collection("ganancias")
                .add(gananciaMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Éxito al guardar en Firestore
                        } else {
                            // Manejar el error
                        }
                    }
                });
    }

    private void actualizarTabla(String gananciaDiaria) {
        // Obtener la fecha actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaActual = dateFormat.format(new Date());

        // Verificar si ya existe una fila para la fecha actual
        boolean filaExistente = false;
        for (int i = 1; i < tablaGanancias.getChildCount(); i++) {
            TableRow row = (TableRow) tablaGanancias.getChildAt(i);
            TextView textViewDia = (TextView) row.getChildAt(0);

            if (textViewDia.getText().toString().equals(fechaActual)) {
                // La fila ya existe, actualizar la celda de ganancia
                TextView textViewGanancia = (TextView) row.getChildAt(1);
                textViewGanancia.setText(gananciaDiaria);
                filaExistente = true;
                break;
            }
        }

        // Si no existe una fila para la fecha actual, crear una nueva fila
        if (!filaExistente) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(fechaActual, Gravity.CENTER));
            row.addView(createTextView(gananciaDiaria, Gravity.CENTER));
            tablaGanancias.addView(row);
        }
    }

    private View createTextView(String text, int gravity) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setGravity(gravity);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }
}