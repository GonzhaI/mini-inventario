package com.adminminiinventario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActivityAlimentosVencidos extends AppCompatActivity {
    private FirebaseFirestore db;
    private TableLayout tableLayoutProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentos_vencidos);

        db = FirebaseFirestore.getInstance();
        tableLayoutProductos = findViewById(R.id.tableLayoutAlimentosPorVencer);

        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        Date fechaActual = calendar.getTime();

        // Calcular la fecha de vencimiento máxima (2 semanas desde ahora)
        calendar.add(Calendar.DAY_OF_YEAR, 14);
        Date fechaVencimientoMaxima = calendar.getTime();

        db.collection("productos")
                .whereGreaterThan("fechaVencimiento", fechaActual)
                .whereLessThan("fechaVencimiento", fechaVencimientoMaxima)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Productos producto = document.toObject(Productos.class);
                                agregarProductoALaTabla(producto);
                            }
                        } else {
                            Log.e("TuActivity", "Error al obtener los productos", task.getException());
                        }
                    }
                });
    }

    private boolean headersAdded = false; // Variable para verificar si los encabezados ya han sido agregados

    private void agregarProductoALaTabla(Productos producto) {
        TableRow newRow = new TableRow(this);

        TextView textViewNombreProducto = new TextView(this);
        textViewNombreProducto.setText(producto.getProducto());
        textViewNombreProducto.setGravity(Gravity.CENTER); // Centrar el texto

        TextView textViewCantidad = new TextView(this);
        textViewCantidad.setText(String.valueOf(producto.getCantidad()));
        textViewCantidad.setGravity(Gravity.CENTER); // Centrar el texto

        TextView textViewFechaVencimiento = new TextView(this);
        Timestamp fechaVencimiento = producto.getfechaVencimiento();

        if (fechaVencimiento != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fechaFormateada = sdf.format(fechaVencimiento.toDate());
            textViewFechaVencimiento.setText(fechaFormateada);
        } else {
            textViewFechaVencimiento.setText("N/A");
        }
        textViewFechaVencimiento.setGravity(Gravity.CENTER); // Centrar el texto

        // Configurar espacio de columnas
        textViewNombreProducto.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textViewCantidad.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textViewFechaVencimiento.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

        if (!headersAdded) {
            // Crear TextViews para los títulos
            TextView headernombreProducto = new TextView(this);
            headernombreProducto.setText("Nombre del Producto");

            TextView headerCantidad = new TextView(this);
            headerCantidad.setText("Cantidad");

            TextView headerfechaVencimiento = new TextView(this);
            headerfechaVencimiento.setText("Fecha de Vencimiento");

            // Agregar titulos a las columnas
            TableRow headerRow = new TableRow(this);
            headerRow.addView(headernombreProducto);
            headerRow.addView(headerCantidad);
            headerRow.addView(headerfechaVencimiento);

            // Agregar la fila de títulos a la parte superior de la tabla
            tableLayoutProductos.addView(headerRow, 0);

            headersAdded = true; // Marcar que los encabezados han sido agregados
        }

        // Agrega las otras columnas según necesites
        newRow.addView(textViewNombreProducto);
        newRow.addView(textViewCantidad);
        newRow.addView(textViewFechaVencimiento);

        // Agrega la fila a la tabla
        tableLayoutProductos.addView(newRow);
    }



}

