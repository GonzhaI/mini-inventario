package com.adminminiinventario;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import java.io.File;
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

    private List<String> elementosDelInventario = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this); // Asegúrate de que estás inicializando Firebase adecuadamente
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        // Inicializa la lista de elementos del inventario
        List<String> elementosDelInventario = new ArrayList<>();

        // Agregar elementos al inventario
        elementosDelInventario.add("Elemento 1");
        elementosDelInventario.add("Elemento 2");
        // ...

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.mostrar_casillas_prin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventarioAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        Button botonGenerarInforme = findViewById(R.id.botonGenerarInforme);

        botonGenerarInforme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llama a la función para generar el informe PDF aquí
                generarInformePDF();
            }
        });


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
                            String imagenURL = document.getString("imagenUrl"); // Obtiene la URL de la imagen
                            String codigo_barra = document.getString("cdBarras");

                            if (nombre_producto != null && precio != null) {
                                // Verifica si hay fecha de vencimiento
                                Date fechaVencimientoDate = null;
                                if (fechaVencimientoTimestamp != null) {
                                    fechaVencimientoDate = fechaVencimientoTimestamp.toDate();
                                }

                                Producto producto = new Producto(nombre_producto, precio, fechaVencimientoDate, imagenURL, codigo_barra);
                                productList.add(producto);
                            }
                        }

                        // Notifica al adaptador que los datos han cambiado
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    public void generarInformePDF() {
        // Ruta de la carpeta de destino en el almacenamiento interno de la aplicación
        File directory = getFilesDir();
        String directorioDestino = directory.getAbsolutePath();

        // Verificar si el directorio existe
        File directorio = new File(directorioDestino);
        if (!directorio.exists()) {
            if (directorio.mkdirs()) {
                System.out.println("Directorio creado correctamente.");
            } else {
                System.out.println("No se pudo crear el directorio.");
                return;  // Si no se pudo crear el directorio, no generamos el PDF
            }
        }

        // Continúa con la generación del PDF
        String filePath = new File(directorio, "informe.pdf").getAbsolutePath();

        // Convierte la lista de elementos en una cadena para incluirla en el PDF
        String contenido = convertirListaAString(elementosDelInventario);

        Generar_Informe generadorPDF = new Generar_Informe();
        generadorPDF.createPDF(filePath, contenido);
    }

    private String convertirListaAString(List<String> lista) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String elemento : lista) {
            stringBuilder.append(elemento).append("\n"); // Agrega cada elemento en una nueva línea
        }
        return stringBuilder.toString();
    }
}

