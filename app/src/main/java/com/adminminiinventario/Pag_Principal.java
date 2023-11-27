package com.adminminiinventario;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adminminiinventario.adapter.ProductosAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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




        TextView negocioNombreTextView = findViewById(R.id.negocioNombre);
        String negocio = getIntent().getStringExtra("negocio");
        negocioNombreTextView.setText(negocio);


        ImageButton calendario = findViewById(R.id.calendario);

        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir pagina Calendario
                Intent intent = new Intent(Pag_Principal.this, ActivityCalendario.class);
                intent.putExtra("negocio", negocio);
                startActivity(intent);
            }
        });

        ImageButton alimentosVencer = findViewById(R.id.alimentosVencer);
        alimentosVencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Pag_Principal.this, ActivityAlimentosVencidos.class);
                startActivity(intent);
            }
        });

        Intent intento = new Intent(this, ActivityAgregarProducto.class);
        intento.putExtra("negocio", negocio);
        ImageButton agregarProductos = findViewById(R.id.agregarProducto);
        agregarProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intento);
            }
        });


        ImageButton bt_cliente = findViewById(R.id.btn_clinetes);   
        bt_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir Pagina Cliente
                Intent intent = new Intent(Pag_Principal.this, ActivityGestorCompradores.class);
                startActivity(intent);
            }
        });
        ImageButton bt_notificaciones = findViewById(R.id.btn_notificaciones);
        bt_notificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir Pagina Cliente
                Intent intent = new Intent(Pag_Principal.this, Notificaciones.class);
                startActivity(intent);
            }
        });

        ImageButton bt_inventario = findViewById(R.id.imageButton_Inventario);
        bt_inventario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir Pagina Cliente
                Intent intent = new Intent(Pag_Principal.this, inventario.class);
                intent.putExtra("negocio", negocio);
                startActivity(intent);
            }
        });

        ImageButton bt_alimentos = findViewById(R.id.imageButton_alimentos);
        bt_alimentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir Pagina Cliente
                Intent intent = new Intent(Pag_Principal.this, Alimentos.class);
                intent.putExtra("negocio", negocio);
                startActivity(intent);
            }
        });

        ImageButton bt_objetos = findViewById(R.id.imageButton_objetos);
        bt_objetos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir Pagina Cliente
                Intent intent = new Intent(Pag_Principal.this, Objetos.class);
                intent.putExtra("negocio", negocio);
                startActivity(intent);
            }
        });

        ImageButton bt_ganancias = findViewById(R.id.imageButtonGanancias);
        bt_ganancias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir la Tabla de Ganancias
                Intent intent = new Intent(Pag_Principal.this, ActivityGraficaGanancias.class);
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


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String texto = newText.toLowerCase();
                buscarProductoPorNombre(texto, negocio);
                return true;
            }
        });
    }

    private void buscarProductoPorNombre(String nombreProducto, String negocio) {
        if (TextUtils.isEmpty(nombreProducto)) {
            productosList.clear();
            productosAdapter.notifyDataSetChanged();


            // Restaura las dimensiones originales del RecyclerView
            ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.recycler_view_height);
            ((ConstraintLayout.LayoutParams) layoutParams).topToTop = ConstraintLayout.LayoutParams.UNSET;
            ((ConstraintLayout.LayoutParams) layoutParams).bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            ((ConstraintLayout.LayoutParams) layoutParams).leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            ((ConstraintLayout.LayoutParams) layoutParams).rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            recyclerView.setLayoutParams(layoutParams);
            return;
        }

        // Realiza una consulta en Firebase Firestore para buscar productos por nombre
        db.collection("productos")
                .whereEqualTo("id_negocio", negocio)
                .whereEqualTo("producto", nombreProducto)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productosList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Productos producto = document.toObject(Productos.class);
                            Log.d("Firebase", "Código de barras: " + producto.getCdBarras());
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
                        ViewGroup.LayoutParams layoutParams = recyclerView.getLayoutParams();
                        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.recycler_view_height);

                        ((ConstraintLayout.LayoutParams) layoutParams).topToTop = ConstraintLayout.LayoutParams.UNSET;
                        ((ConstraintLayout.LayoutParams) layoutParams).bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                        ((ConstraintLayout.LayoutParams) layoutParams).leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                        ((ConstraintLayout.LayoutParams) layoutParams).rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;

                        recyclerView.setLayoutParams(layoutParams);

                    }
                });
    }




}
