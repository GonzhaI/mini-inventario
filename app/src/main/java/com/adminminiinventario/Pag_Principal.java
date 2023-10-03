package com.adminminiinventario;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminminiinventario.adapter.ProductosAdapter;
import com.adminminiinventario.model.Productos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class Pag_Principal extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private ProductosAdapter productosAdapter;

    private FirebaseFirestore db;

    private List<Productos> productosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pag_principal);
        ImageButton calendario = findViewById(R.id.calendario);
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir pagina Calendario
                Intent intent = new Intent(Pag_Principal.this, ActivityCalendario.class);
                startActivity(intent);
            }
        });
        ImageButton bt_cliente = findViewById(R.id.btn_clinetes);
        bt_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir Pagina Cliente
                Intent intent = new Intent(Pag_Principal.this, ActivityIngresoCliente.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.search);

        db = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productosAdapter = new ProductosAdapter(productosList);
        recyclerView.setAdapter(productosAdapter);

        // Agrega un listener al SearchView para escuchar cambios de texto
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filtra los resultados en tiempo real a medida que el usuario escribe en el SearchView
                buscarProductoPorNombre(newText);
                return true;
            }
        });
    }

    private void buscarProductoPorNombre(String nombreProducto) {
        // Borra la lista de productos si no hay un nombre de producto válido
        if (TextUtils.isEmpty(nombreProducto)) {
            productosList.clear();
            productosAdapter.notifyDataSetChanged();
            return;
        }

        // Realiza una consulta en Firestore para buscar productos por nombre
        db.collection("productos")
                .whereEqualTo("producto", nombreProducto)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        productosList.clear();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Productos producto = snapshot.toObject(Productos.class);
                            productosList.add(producto);
                        }
                        productosAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Maneja errores de Firestore aquí si es necesario
                    }
                });
    }
}
