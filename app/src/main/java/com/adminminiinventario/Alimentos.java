package com.adminminiinventario;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminminiinventario.adapter.AlimentosAdapter;
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

public class Alimentos extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private AlimentosAdapter adapter;
    private List<Producto> productList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private ListenerRegistration userDataListener;

    private List<String> elementosDelInventario = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this); // Asegúrate de que estás inicializando Firebase adecuadamente
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alimentos);

        // Inicializa la lista de elementos del inventario
        List<String> elementosDelInventario = new ArrayList<>();

        // Agregar elementos al inventario
        elementosDelInventario.add("Elemento 1");
        elementosDelInventario.add("Elemento 2");
        // ...

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.mostrar_casillas_alimentos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlimentosAdapter(this, productList);
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
        if (negocio != null) {
            db.collection("productos")
                    .whereEqualTo("id_negocio", negocio.toLowerCase())
                    .whereNotEqualTo("fechaVencimiento", null) // Filtra solo los productos con fecha de vencimiento no nula
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            productList.clear(); // Limpia la lista antes de agregar nuevos productos

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("MiApp", "Producto encontrado: " + document.getData().toString());

                                String idNegocio = document.getString("id_negocio");
                                String nombre_producto = document.getString("producto");
                                int cantidad = document.getLong("cantidad").intValue();
                                Double precio = document.getDouble("valor");
                                Timestamp fechaVencimientoTimestamp = document.getTimestamp("fechaVencimiento");
                                String imagenURL = document.getString("imagenUrl");
                                String codigo_barra = document.getString("cdBarras");

                                Log.d("MiApp", "Nombre: " + nombre_producto + ", Cantidad: " + cantidad);

                                if (nombre_producto != null && precio != null) {
                                    // Verifica si hay fecha de vencimiento
                                    Date fechaVencimientoDate = null;
                                    if (fechaVencimientoTimestamp != null) {
                                        fechaVencimientoDate = fechaVencimientoTimestamp.toDate();
                                    }

                                    Producto producto = new Producto(cantidad, idNegocio, nombre_producto, precio, fechaVencimientoDate, imagenURL, codigo_barra);
                                    productList.add(producto);
                                }
                            }

                            // Notifica al adaptador que los datos han cambiado
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("MiApp", "Error al obtener productos", task.getException());
                        }
                    });
        }
    }
}

