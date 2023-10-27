package com.adminminiinventario;

import android.os.Bundle;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminminiinventario.adapter.InventarioAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class inventario extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private InventarioAdapter adapter;
    private List<String> productList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private ListenerRegistration userDataListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        db.collection("productos")
                .whereEqualTo("id_negocio", negocio.toLowerCase())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear(); // Limpia la lista antes de agregar nuevos productos

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String nombre_producto = document.getString("producto");
                            productList.add(nombre_producto);
                        }

                        // Notifica al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged();
                    } else {
                        // Manejar el caso de error si es necesario
                    }
                });
    }
}
