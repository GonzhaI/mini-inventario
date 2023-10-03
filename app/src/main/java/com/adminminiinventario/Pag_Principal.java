package com.adminminiinventario;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.constraintlayout.widget.ConstraintLayout;
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
    private int recyclerViewWidth;
    private int recyclerViewHeight;

    // Restricciones iniciales del RecyclerView
    private float initialVerticalBias = 0.3f;
    private float initialHorizontalBias = 0.402f;

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

                Intent intent = new Intent(Pag_Principal.this, ActivityIngresoCliente.class);
                startActivity(intent);
            }
        });

        ImageButton bt_inventario = findViewById(R.id.imageButton_Inventario);
        bt_inventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir Pagina Cliente
                Intent intent = new Intent(Pag_Principal.this, inventario.class);
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


        // Configura el RecyclerView
        recyclerViewWidth = getResources().getDimensionPixelSize(R.dimen.recycler_view_width);
        recyclerViewHeight = getResources().getDimensionPixelSize(R.dimen.recycler_view_height);

        ViewGroup.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                recyclerViewWidth,
                recyclerViewHeight
        );
        ((ConstraintLayout.LayoutParams) layoutParams).verticalBias = initialVerticalBias;
        ((ConstraintLayout.LayoutParams) layoutParams).horizontalBias = initialHorizontalBias;
        recyclerView.setLayoutParams(layoutParams);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productosAdapter = new ProductosAdapter(productosList);
        recyclerView.setAdapter(productosAdapter);

        // Configura el SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscarProductoPorNombre(newText);
                return true;
            }
        });
    }

    private void buscarProductoPorNombre(String nombreProducto) {
        if (TextUtils.isEmpty(nombreProducto)) {
            productosList.clear();
            productosAdapter.notifyDataSetChanged();
            return;
        }

        // Realiza una consulta en Firebase Firestore para buscar productos por nombre
        db.collection("productos")
                .whereEqualTo("producto", nombreProducto)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productosList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Productos producto = document.toObject(Productos.class);
                            productosList.add(producto);
                        }
                        productosAdapter.notifyDataSetChanged();

                        // Cambia las dimensiones del RecyclerView cuando se encuentra un dato
                        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
                        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.full_height);

                        // Cambia la posición vertical del RecyclerView utilizando android:layout_marginTop
                        ((ConstraintLayout.LayoutParams) layoutParams).topMargin = 100; // Coloca la cantidad de píxeles aquí
                        ((ConstraintLayout.LayoutParams) layoutParams).topToTop = R.id.search;

                        ((ConstraintLayout.LayoutParams) layoutParams).bottomToBottom = ConstraintLayout.LayoutParams.UNSET;
                        ((ConstraintLayout.LayoutParams) layoutParams).leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                        ((ConstraintLayout.LayoutParams) layoutParams).rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;

                        recyclerView.setLayoutParams(layoutParams);
                    } else {
                        // Maneja errores si la consulta falla
                    }
                });
    }




}
