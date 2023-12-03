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

public class ActivityListaDistribuidores extends AppCompatActivity {

    private FirebaseFirestore db;
    private TableLayout tableLayoutDistribuidores;
    private TextView nombreUsuario;
    private boolean headersAdded = false;
    private List<Distribuidor> distribuidoresMostrados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_distribuidores);

        db = FirebaseFirestore.getInstance();
        tableLayoutDistribuidores = findViewById(R.id.tableLayoutListaDistribuidor);
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

                                // Consulta para obtener distribuidores
                                db.collection("llegadaMercaderia")
                                        .whereEqualTo("id_negocio", negocio)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Distribuidor distribuidor = document.toObject(Distribuidor.class);
                                                        agregarDistribuidorALaTabla(distribuidor);
                                                        Log.e("Lista distribuidor","se esta ejecutando");
                                                    }
                                                    mostrarDistribuidoresEnTablaOrdenada();
                                                } else {
                                                    Log.e("TuActivity", "Error al obtener los distribuidores", task.getException());
                                                }
                                            }
                                        });
                            }
                        }
                    });
        }
    }

    private void agregarDistribuidorALaTabla(Distribuidor distribuidor) {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        Date fechaActual = calendar.getTime();

        // Calcular la fecha de llegada máxima (1 semana desde ahora)
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date fechaLlegadaMaxima = calendar.getTime();

        // Obtener la fecha de llegada del distribuidor, manejando el caso en que sea nulo
        Timestamp fechaLlegadaTimestamp = distribuidor.getFechaLlegada();
        Date fechaLlegada = (fechaLlegadaTimestamp != null) ? fechaLlegadaTimestamp.toDate() : null;

        // Verificar si el distribuidor tiene fecha de llegada y está a punto de llegar
        if (fechaLlegada != null && fechaLlegada.after(fechaActual) && fechaLlegada.before(fechaLlegadaMaxima)) {
            distribuidoresMostrados.add(distribuidor);
        }
    }

    private void ordenarDistribuidoresPorFechaLlegada() {
        Collections.sort(distribuidoresMostrados, new Comparator<Distribuidor>() {
            @Override
            public int compare(Distribuidor distribuidor1, Distribuidor distribuidor2) {
                Date fechaLlegada1 = distribuidor1.getFechaLlegada().toDate();
                Date fechaLlegada2 = distribuidor2.getFechaLlegada().toDate();
                return fechaLlegada1.compareTo(fechaLlegada2);
            }
        });
    }

    private void mostrarDistribuidoresEnTablaOrdenada() {
        ordenarDistribuidoresPorFechaLlegada();

        for (Distribuidor distribuidor : distribuidoresMostrados) {
            TableRow newRow = new TableRow(this);

            TextView textViewNombreDistribuidor = new TextView(this);
            textViewNombreDistribuidor.setText(distribuidor.getNombreDistribuidor());
            textViewNombreDistribuidor.setGravity(Gravity.CENTER);

            TextView textViewFechaLlegada = new TextView(this);

            Date fechaLlegada = distribuidor.getFechaLlegada().toDate();

            if (fechaLlegada != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String fechaFormateada = sdf.format(fechaLlegada);
                textViewFechaLlegada.setText(fechaFormateada);
            } else {
                textViewFechaLlegada.setText("N/A");
            }

            textViewFechaLlegada.setGravity(Gravity.CENTER);

            textViewNombreDistribuidor.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.9f));
            textViewFechaLlegada.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.3f));

            if (!headersAdded) {
                TextView headerNombreDistribuidor = new TextView(this);
                headerNombreDistribuidor.setText("Nombre del Distribuidor");

                TextView headerFechaLlegada = new TextView(this);
                headerFechaLlegada.setText("Fecha de Llegada");

                TableRow headerRow = new TableRow(this);
                headerRow.addView(headerNombreDistribuidor);
                headerRow.addView(headerFechaLlegada);

                tableLayoutDistribuidores.addView(headerRow, 0);

                headersAdded = true;
            }

            newRow.addView(textViewNombreDistribuidor);
            newRow.addView(textViewFechaLlegada);

            tableLayoutDistribuidores.addView(newRow);
        }
    }
}
