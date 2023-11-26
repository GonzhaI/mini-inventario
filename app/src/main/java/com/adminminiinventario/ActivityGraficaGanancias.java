package com.adminminiinventario;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ActivityGraficaGanancias extends AppCompatActivity {

    private TableLayout tablaGanancias;
    private EditText editTextGananciaDiaria;
    private FirebaseFirestore db;
    private String mesActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafica_ganancias);

        tablaGanancias = findViewById(R.id.tablaGanancias);
        editTextGananciaDiaria = findViewById(R.id.editTextGananciaDiaria);
        db = FirebaseFirestore.getInstance();

        mesActual = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

        inicializarTabla();
        cargarGananciasDesdeTabla();
        calcularGananciasTotales();

        editTextGananciaDiaria.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }});

        Button botonGuardarGanancia = findViewById(R.id.botonGuardarGanancia);
        botonGuardarGanancia.setOnClickListener(v -> guardarGanancia());
    }

    private void inicializarTabla() {
        int diasEnElMes = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextView("Dia", Gravity.CENTER));
        headerRow.addView(createTextView("Ganancia", Gravity.CENTER));
        headerRow.addView(createTextView("", Gravity.CENTER));

        // Configurar el ancho de las celdas del encabezado
        for (int i = 0; i < headerRow.getChildCount(); i++) {
            View view = headerRow.getChildAt(i);
            view.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        }

        tablaGanancias.addView(headerRow);

        headerRow.setBackgroundColor(Color.parseColor("#CCCCCC"));

        for (int dia = 1; dia <= diasEnElMes; dia++) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(String.valueOf(dia), Gravity.CENTER));
            row.addView(createTextView("", Gravity.CENTER));
            Button botonEditar = createButton("Editar", dia);

            // Verificar si el día aún no ha llegado
            if (dia > Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                // Si el día aún no ha llegado, deshabilitar el botón
                botonEditar.setEnabled(false);
            }

            row.addView(botonEditar);
            tablaGanancias.addView(row);
            cargarGananciaDiaria(String.valueOf(dia));

            // Configurar el ancho de las celdas de las filas
            for (int i = 0; i < row.getChildCount(); i++) {
                View view = row.getChildAt(i);
                view.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            }
        }

        cargarGananciasDesdeTabla();
        calcularGananciasTotales();
    }

    private void guardarGanancia() {
        String gananciaDiaria = editTextGananciaDiaria.getText().toString();
        int diaActual = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // Verificar si ya se insertó una ganancia para el día actual
        if (!gananciaDiaria.isEmpty() && !yaInsertadoGanancia(String.valueOf(diaActual))) {
            guardarGananciaFirestore(String.valueOf(diaActual), gananciaDiaria);
            actualizarTabla(String.valueOf(diaActual), gananciaDiaria);
            calcularGananciasTotales();
            editTextGananciaDiaria.setText("");

            // Deshabilitar el botón de guardarGanancia después de insertar una ganancia
            Button botonGuardarGanancia = findViewById(R.id.botonGuardarGanancia);
            botonGuardarGanancia.setEnabled(false);
        } else {
            Toast.makeText(getApplicationContext(), "Ingrese una ganancia válida o ya se ha ingresado para este día", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean yaInsertadoGanancia(String dia) {
        // Verificar si ya se insertó una ganancia para el día actual
        TableRow row = obtenerFilaPorDia(dia);
        if (row != null) {
            TextView textViewGanancia = (TextView) row.getChildAt(1);
            return !TextUtils.isEmpty(textViewGanancia.getText().toString());
        }
        return false;
    }

    private TableRow obtenerFilaPorDia(String dia) {
        // Obtener la fila correspondiente al día
        for (int i = 1; i < tablaGanancias.getChildCount(); i++) {
            TableRow row = (TableRow) tablaGanancias.getChildAt(i);
            TextView textViewDia = (TextView) row.getChildAt(0);
            String fechaTabla = textViewDia.getText().toString().replaceFirst("^0", "");

            if (fechaTabla.equals(dia)) {
                return row;
            }
        }
        return null;
    }

    private void cargarGananciaDiaria(String dia) {
        String userId = obtenerIdUsuarioActual();

        db.collection("ganancias")
                .document(generarIdGanancia(mesActual, userId, dia))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String gananciaDiaria = task.getResult().getString("ganancia");
                            actualizarTabla(dia, gananciaDiaria);
                        }
                    } else {
                        // Manejar el error
                    }
                });
    }

    private void actualizarTabla(String fecha, String gananciaDiaria) {
        String fechaSinCero = fecha.replaceFirst("^0", "");

        boolean filaExistente = false;
        for (int i = 1; i < tablaGanancias.getChildCount(); i++) {
            TableRow row = (TableRow) tablaGanancias.getChildAt(i);
            TextView textViewDia = (TextView) row.getChildAt(0);
            String fechaTabla = textViewDia.getText().toString().replaceFirst("^0", "");

            if (fechaTabla.equals(fechaSinCero)) {
                TextView textViewGanancia = (TextView) row.getChildAt(1);
                textViewGanancia.setText(gananciaDiaria);
                filaExistente = true;
                break;
            }
        }

        if (!filaExistente) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(fecha, Gravity.CENTER));
            row.addView(createTextView(gananciaDiaria, Gravity.CENTER));
            tablaGanancias.addView(row);
        }
    }

    private void cargarGananciasDesdeTabla() {
        for (int i = 1; i < tablaGanancias.getChildCount(); i++) {
            TableRow row = (TableRow) tablaGanancias.getChildAt(i);
            TextView textViewDia = (TextView) row.getChildAt(0);
            String dia = textViewDia.getText().toString();
            cargarGananciaDiaria(dia);
        }
        calcularGananciasTotales();
    }

    private void guardarGananciaFirestore(String dia, String gananciaDiaria) {
        String userId = obtenerIdUsuarioActual();
        String idGanancia = generarIdGanancia(mesActual, userId, dia);

        Map<String, Object> gananciaMap = new HashMap<>();
        gananciaMap.put("ganancia", gananciaDiaria);
        gananciaMap.put("mes", mesActual);
        gananciaMap.put("userId", userId);

        db.collection("ganancias")
                .document(idGanancia)
                .set(gananciaMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Éxito al guardar en Firestore
                    } else {
                        // Manejar el error
                    }
                });
    }

    private void calcularGananciasTotales() {
        long gananciasTotales = 0;

        for (int i = 1; i < tablaGanancias.getChildCount(); i++) {
            TableRow row = (TableRow) tablaGanancias.getChildAt(i);
            TextView textViewGanancia = (TextView) row.getChildAt(1);

            String gananciaDiariaStr = textViewGanancia.getText().toString().trim();

            try {
                long gananciaDiaria = Long.parseLong(gananciaDiariaStr);
                gananciasTotales += gananciaDiaria;
            } catch (NumberFormatException e) {
                // Manejar el error de conversión
                Log.e("Error", "Error al convertir gananciaDiaria a long: " + e.getMessage());
            }
        }

        // Obtener el nombre del mes en español
        String nombreMes = obtenerNombreMes(Calendar.getInstance().get(Calendar.MONTH) + 1);

        // Mostrar las ganancias totales en el TextView
        TextView textViewGananciasTotales = findViewById(R.id.textViewGananciasTotales);
        textViewGananciasTotales.setText("Ganancias totales en " + nombreMes + ": $" + gananciasTotales);
        textViewGananciasTotales.invalidate();

        // Agregar un log para imprimir las ganancias totales
        Log.d("GananciasTotales", "Ganancias totales en " + nombreMes + ": $" + gananciasTotales);
    }

    private String obtenerNombreMes(int numeroMes) {
        // Crear un objeto SimpleDateFormat con el patrón "MMMM" para obtener el nombre completo del mes
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM", new Locale("es", "ES"));

        // Crear un objeto Calendar con el mes proporcionado
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, numeroMes - 1); // Restar 1 porque en Calendar, enero es 0

        // Obtener el nombre del mes
        return sdf.format(calendar.getTime());
    }

    private String generarIdGanancia(String mes, String userId, String dia) {
        return mes + "-" + userId + "-" + dia;
    }

    private String obtenerIdUsuarioActual() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private View createTextView(String text, int gravity) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setGravity(gravity);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }

    private Button createButton(String text, int dia) {
        Button button = new Button(this);
        button.setText(text);
        button.setOnClickListener(v -> editarGanancia(dia));
        return button;
    }

    private void editarGanancia(int dia) {
        // Crear un cuadro de diálogo de edición
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Ganancia para el Día " + dia);

        // Crear un EditText en el cuadro de diálogo
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Configurar el botón de actualización en el cuadro de diálogo
        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            String nuevaGanancia = input.getText().toString().trim();
            // Validar y actualizar la ganancia en la base de datos
            if (!TextUtils.isEmpty(nuevaGanancia)) {
                actualizarGananciaEnFirebase(dia, nuevaGanancia);

                // Actualizar la tabla con la ganancia ingresada
                actualizarTabla(String.valueOf(dia), nuevaGanancia);

                // Calcular ganancias totales después de guardar la ganancia
                calcularGananciasTotales();
            } else {
                Toast.makeText(getApplicationContext(), "Ingrese una ganancia válida", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el botón de cancelar en el cuadro de diálogo
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        // Mostrar el cuadro de diálogo
        builder.show();

        calcularGananciasTotales();
    }

    private void actualizarGananciaEnFirebase(int dia, String nuevaGanancia) {
        // Obtener el mes actual
        String mesActual = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

        // Obtener el ID de usuario actual
        String userId = obtenerIdUsuarioActual();

        // Formatear el día para que tenga dos dígitos
        String diaFormateado = String.format(Locale.getDefault(), "%02d", dia);

        // Crear la fecha completa en el formato de la base de datos
        String fechaCompleta = mesActual + "-" + diaFormateado;

        // Realizar una consulta en la base de datos para encontrar la entrada correspondiente a la fecha
        db.collection("ganancias")
                .whereEqualTo("mes", mesActual)
                .whereEqualTo("userId", userId)
                .whereEqualTo("fecha", fechaCompleta)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener el ID único de la ganancia
                            String gananciaId = document.getString("gananciaId");

                            // Crear un mapa con los datos actualizados
                            Map<String, Object> datosActualizados = new HashMap<>();
                            datosActualizados.put("ganancia", nuevaGanancia);

                            // Actualizar la entrada en la base de datos usando el "gananciaId"
                            db.collection("ganancias")
                                    .document(gananciaId)
                                    .update(datosActualizados)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            // Éxito al actualizar la ganancia
                                            // Puedes realizar acciones adicionales si es necesario
                                        } else {
                                            // Manejar el error al actualizar la ganancia
                                        }
                                    });
                        }
                    } else {
                        // Manejar el error al realizar la consulta
                    }
                });
    }
}