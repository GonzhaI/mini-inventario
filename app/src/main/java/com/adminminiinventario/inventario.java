package com.adminminiinventario;

import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminminiinventario.adapter.InventarioAdapter;
import com.adminminiinventario.adapter.Producto;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class inventario extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private InventarioAdapter adapter;
    private List<Producto> productList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private ListenerRegistration userDataListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this); // Asegúrate de que estás inicializando Firebase adecuadamente
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.mostrar_casillas_prin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventarioAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        if (currentUser != null) {
            String uid = currentUser.getUid();
            userDataListener = db.collection("usuario").document(uid)
                    .addSnapshotListener((documentSnapshot, error) -> {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String negocio = documentSnapshot.getString("negocio");
                            // Ahora, con el negocio del usuario, obtén los productos relacionados
                            obtenerProductosDelNegocio(negocio);
                        }
                    });
        }
    }

    private void obtenerProductosDelNegocio(String negocio) {
        Log.d("MiApp", "Productos obtenidos: " + productList.size());
        db.collection("productos")
                .whereEqualTo("id_negocio", negocio.toLowerCase())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear(); // Limpia la lista antes de agregar nuevos productos

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String nombre_producto = document.getString("producto");
                            Double precio = document.getDouble("valor");
                            Timestamp fechaVencimientoTimestamp = document.getTimestamp("fechaVencimiento");

                            if (nombre_producto != null && precio != null) {
                                // Verifica si hay fecha de vencimiento
                                Date fechaVencimientoDate = null;
                                if (fechaVencimientoTimestamp != null) {
                                    fechaVencimientoDate = fechaVencimientoTimestamp.toDate();
                                }

                                Producto producto = new Producto(nombre_producto, precio, fechaVencimientoDate);
                                productList.add(producto);
                            }
                        }

                        // Notifica al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged();
                    }

                });
    }
}
