package com.adminminiinventario;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityAlimentosVencidos extends AppCompatActivity {
    private FirebaseFirestore db;
    private TableLayout tableLayoutProductos;
    private TextView nombreUsuario;
    private boolean headersAdded = false;
    private List<Productos> productosMostrados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentos_vencidos);

        db = FirebaseFirestore.getInstance();
        tableLayoutProductos = findViewById(R.id.tableLayoutAlimentosPorVencer);
        nombreUsuario = findViewById(R.id.nombre_usuario);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            db.collection("usuario")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String negocio = documentSnapshot.getString("negocio").toLowerCase();
                                nombreUsuario.setText(negocio);

                                db.collection("productos")
                                        .whereEqualTo("id_negocio", negocio)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Productos producto = document.toObject(Productos.class);
                                                        agregarProductoALaTabla(producto);
                                                    }
                                                    mostrarProductosEnTablaOrdenada();
                                                } else {
                                                    Log.e("TuActivity", "Error al obtener los productos", task.getException());
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    private void agregarProductoALaTabla(Productos producto) {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        Date fechaActual = calendar.getTime();

        // Calcular la fecha de vencimiento máxima (2 semanas desde ahora)
        calendar.add(Calendar.DAY_OF_YEAR, 14);
        Date fechaVencimientoMaxima = calendar.getTime();

        // Obtener la fecha de vencimiento del producto, manejando el caso en que sea nulo
        Timestamp fechaVencimientoTimestamp = producto.getfechaVencimiento();
        Date fechaVencimiento = (fechaVencimientoTimestamp != null) ? fechaVencimientoTimestamp.toDate() : null;

        // Verificar si el producto tiene fecha de vencimiento y está a punto de vencer
        if (fechaVencimiento != null && fechaVencimiento.after(fechaActual) && fechaVencimiento.before(fechaVencimientoMaxima)) {
            productosMostrados.add(producto);
        }
    }

    private void ordenarProductosPorFecha() {
        Collections.sort(productosMostrados, new Comparator<Productos>() {
            @Override
            public int compare(Productos producto1, Productos producto2) {
                Date fecha1 = producto1.getfechaVencimiento().toDate();
                Date fecha2 = producto2.getfechaVencimiento().toDate();
                return fecha1.compareTo(fecha2);
            }
        });
    }

    private void mostrarProductosEnTablaOrdenada() {
        ordenarProductosPorFecha();

        for (Productos producto : productosMostrados) {
            TableRow newRow = new TableRow(this);

            TextView textViewNombreProducto = new TextView(this);
            textViewNombreProducto.setText(producto.getProducto());
            textViewNombreProducto.setGravity(Gravity.CENTER);

            TextView textViewCantidad = new TextView(this);
            textViewCantidad.setText(String.valueOf(producto.getCantidad()));
            textViewCantidad.setGravity(Gravity.CENTER);

            TextView textViewFechaVencimiento = new TextView(this);

            Date fechaVencimiento = producto.getfechaVencimiento().toDate();

            if (fechaVencimiento != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String fechaFormateada = sdf.format(fechaVencimiento);
                textViewFechaVencimiento.setText(fechaFormateada);
            } else {
                textViewFechaVencimiento.setText("N/A");
            }

            textViewFechaVencimiento.setGravity(Gravity.CENTER);

            textViewNombreProducto.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            textViewCantidad.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            textViewFechaVencimiento.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

            if (!headersAdded) {
                TextView headernombreProducto = new TextView(this);
                headernombreProducto.setText("Nombre del Producto");

                TextView headerCantidad = new TextView(this);
                headerCantidad.setText("Cantidad");

                TextView headerfechaVencimiento = new TextView(this);
                headerfechaVencimiento.setText("Fecha de Vencimiento");

                TableRow headerRow = new TableRow(this);
                headerRow.addView(headernombreProducto);
                headerRow.addView(headerCantidad);
                headerRow.addView(headerfechaVencimiento);

                tableLayoutProductos.addView(headerRow, 0);

                headersAdded = true;
            }

            newRow.addView(textViewNombreProducto);
            newRow.addView(textViewCantidad);
            newRow.addView(textViewFechaVencimiento);

            tableLayoutProductos.addView(newRow);
        }
    }
}
