package com.adminminiinventario;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

        // Obtener el número de días en el mes actual
        int diasEnElMes = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);

        // Obtener el mes actual y asignarlo a la variable miembro
        mesActual = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

        // Crear el encabezado para la tabla
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextView("Dia", Gravity.CENTER));
        headerRow.addView(createTextView("Ganancia", Gravity.CENTER));
        tablaGanancias.addView(headerRow);

        // Crear filas para los días del mes actual
        for (int dia = 1; dia <= diasEnElMes; dia++) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(String.valueOf(dia), Gravity.CENTER));
            row.addView(createTextView("", Gravity.CENTER));
            tablaGanancias.addView(row);
        }

        // Cargar ganancias desde Firestore
        cargarGananciasDesdeFirestore();

        // Calcular ganancias totales
        calcularGananciasTotales(mesActual);

        // Filtro para aceptar solo números en el campo de ganancia
        editTextGananciaDiaria.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }});

        // Botón para guardar ganancias
        Button botonGuardarGanancia = findViewById(R.id.botonGuardarGanancia);
        botonGuardarGanancia.setOnClickListener(v -> {
            // Obtener la ganancia ingresada
            String gananciaDiaria = editTextGananciaDiaria.getText().toString();

            // Guardar la ganancia en Firestore
            guardarGananciaFirestore(gananciaDiaria);

            // Obtener el día actual
            int diaActual = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

            // Actualizar la tabla con la ganancia ingresada
            actualizarTabla(String.valueOf(diaActual), gananciaDiaria);

            // Calcular ganancias totales después de guardar la ganancia
            calcularGananciasTotales(mesActual);

            // Limpiar el campo de entrada
            editTextGananciaDiaria.setText("");
        });

        // Verificar si el mes actual es diferente al mes registrado en la última entrada
        verificarYReiniciarTabla();
    }

    private void actualizarTabla(String fechaIngresada, String gananciaDiaria) {
        // Eliminar cero a la izquierda en la fecha ingresada
        String fechaSinCero = fechaIngresada.replaceFirst("^0", "");

        // Verificar si ya existe una fila para la fecha ingresada
        boolean filaExistente = false;
        for (int i = 1; i < tablaGanancias.getChildCount(); i++) {
            TableRow row = (TableRow) tablaGanancias.getChildAt(i);
            TextView textViewDia = (TextView) row.getChildAt(0);

            // Eliminar cero a la izquierda en la fecha de la tabla
            String fechaTabla = textViewDia.getText().toString().replaceFirst("^0", "");

            if (fechaTabla.equals(fechaSinCero)) {
                // La fila ya existe, actualizar la celda de ganancia
                TextView textViewGanancia = (TextView) row.getChildAt(1);
                textViewGanancia.setText(gananciaDiaria);
                filaExistente = true;
                break;
            }
        }

        // Si no existe una fila para la fecha ingresada, crear una nueva fila
        if (!filaExistente) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(fechaIngresada, Gravity.CENTER));
            row.addView(createTextView(gananciaDiaria, Gravity.CENTER));
            tablaGanancias.addView(row);
        }
    }


    private void verificarYReiniciarTabla() {
        // Obtener el mes actual
        String mesActual = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

        // Obtener el último mes registrado en la tabla de Firestore
        db.collection("ganancias")
                .orderBy("mes", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String ultimoMesRegistrado = document.getString("mes");

                            // Comparar el mes actual con el último mes registrado
                            if (!mesActual.equals(ultimoMesRegistrado)) {
                                // Si son diferentes, reiniciar la tabla
                                reiniciarTabla();

                                // Calcular y mostrar las ganancias totales en el mes actual
                                calcularGananciasTotales(mesActual);
                            }
                        }
                    }
                });
    }

    private void calcularGananciasTotales(String mesActual) {
        // Limpiar el TextView antes de actualizar
        TextView textViewGananciasTotales = findViewById(R.id.textViewGananciasTotales);
        textViewGananciasTotales.setText("");

        // Obtener el mes y el año actual
        String[] partesMesActual = mesActual.split("-");
        String mes = partesMesActual[0];
        String anio = partesMesActual[1];

        // Almacena las fechas ya procesadas para evitar duplicados
        Set<String> fechasProcesadas = new HashSet<>();

        // Realizar una consulta a la base de datos para obtener las ganancias del mes actual
        db.collection("ganancias")
                .whereEqualTo("mes", mesActual)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double gananciasTotales = 0.0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener la fecha y la ganancia diaria
                            String fecha = document.getString("fecha");
                            String gananciaDiaria = document.getString("ganancia");

                            // Extraer solo la parte de la fecha sin la hora
                            String fechaSinHora = fecha.substring(0, 10);

                            // Verificar si ya se ha procesado esta fecha
                            if (!fechasProcesadas.contains(fechaSinHora)) {
                                // Agregar la fecha actual al conjunto de fechas procesadas
                                fechasProcesadas.add(fechaSinHora);

                                // Verificar que la ganancia diaria no esté vacía
                                if (gananciaDiaria != null && !gananciaDiaria.isEmpty()) {
                                    // Sumar la ganancia diaria a las ganancias totales
                                    gananciasTotales += Double.parseDouble(gananciaDiaria);
                                }
                            }
                        }

                        // Obtener el nombre del mes en español
                        String nombreMes = obtenerNombreMes(Integer.parseInt(mes));

                        // Mostrar las ganancias totales en el TextView
                        textViewGananciasTotales.setText("Ganancias totales en " + nombreMes + " de " + anio + ": $" + gananciasTotales);
                    } else {
                        // Manejar el error
                        textViewGananciasTotales.setText("Error al obtener las ganancias.");
                    }
                });
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

    private void reiniciarTabla() {
        // Limpiar todas las filas excepto la primera (encabezado)
        tablaGanancias.removeViews(1, tablaGanancias.getChildCount() - 1);

        // Actualizar el mes registrado en Firestore
        Map<String, Object> nuevaEntrada = new HashMap<>();
        nuevaEntrada.put("mes", new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date()));

        db.collection("ganancias")
                .add(nuevaEntrada)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Éxito al reiniciar la tabla
                    } else {
                        // Manejar el error
                    }
                });
    }

    // Almacena las ganancias de cada usuario
    private void guardarGananciaFirestore(String gananciaDiaria) {
        // Obtener la fecha actual
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Obtener el ID de usuario actual
        String userId = obtenerIdUsuarioActual();

        // Crear un mapa con los datos a almacenar
        Map<String, Object> gananciaMap = new HashMap<>();
        gananciaMap.put("ganancia", gananciaDiaria);
        gananciaMap.put("fecha", fechaActual);
        gananciaMap.put("mes", new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date()));
        gananciaMap.put("userId", userId);  // Agregar el ID de usuario

        // Almacenar la ganancia en Firestore
        db.collection("ganancias")
                .add(gananciaMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Éxito al guardar en Firestore

                        // Calcular ganancias totales después de guardar la ganancia
                        calcularGananciasTotales(mesActual);
                    } else {
                        // Manejar el error
                    }
                });
    }

    private void cargarGananciasDesdeFirestore() {
        // Obtener el mes actual
        String mesActual = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

        // Obtener el ID de usuario actual
        String userId = obtenerIdUsuarioActual();

        // Almacena las fechas ya procesadas para evitar duplicados
        Set<String> fechasProcesadas = new HashSet<>();

        // Realizar una consulta a la base de datos para obtener las ganancias del mes actual para el usuario actual
        db.collection("ganancias")
                .whereEqualTo("mes", mesActual)
                .whereEqualTo("userId", userId)  // Filtrar por el ID de usuario
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Obtener la fecha y la ganancia diaria
                            String fecha = document.getString("fecha");
                            String gananciaDiaria = document.getString("ganancia");

                            // Extraer solo la parte de la fecha sin la hora
                            String fechaSinHora = fecha.substring(0, 10);

                            // Verificar si ya se ha procesado esta fecha
                            if (!fechasProcesadas.contains(fechaSinHora)) {
                                // Agregar la fecha actual al conjunto de fechas procesadas
                                fechasProcesadas.add(fechaSinHora);

                                // Actualizar la tabla con la ganancia correspondiente
                                actualizarTabla(fecha.substring(8), gananciaDiaria);
                            }
                        }
                    } else {
                        // Manejar el error
                    }
                });
    }

    // Obtener el ID del usuario
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
}