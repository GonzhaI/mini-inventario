package com.adminminiinventario;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ActivityGestorCompradores extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final int REQUEST_CODE_INGRESO_CLIENTE = 1;
    private TableLayout tableLayoutClientes;
    private Button btnAgregarCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestor_compradores);

        // Inicializa Firebase Firestore
        db = FirebaseFirestore.getInstance();

        tableLayoutClientes = findViewById(R.id.tableLayoutClientes);
        btnAgregarCliente = findViewById(R.id.btnAgregarCliente);

        // Crear una fila para los títulos de las columnas
        TableRow headerRow = new TableRow(this);

        // Crear TextViews para los títulos
        TextView headerNombre = new TextView(this);
        headerNombre.setText("Nombre del Cliente");
        headerNombre.setTextColor(Color.WHITE);

        TextView headerDeudor = new TextView(this);
        headerDeudor.setText("¿Es Deudor?");
        headerDeudor.setTextColor(Color.WHITE);

        TextView headerDiasRestantes = new TextView(this);
        headerDiasRestantes.setText("Días Restantes");
        headerDiasRestantes.setTextColor(Color.WHITE);

        TextView headerDescuento = new TextView(this);
        headerDescuento.setText("Descuento (%)");
        headerDescuento.setTextColor(Color.WHITE);

        // Configurar espacio de columnas
        headerNombre.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        headerDeudor.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        headerDiasRestantes.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        headerDescuento.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));


        headerRow.addView(headerNombre);
        headerRow.addView(headerDeudor);
        headerRow.addView(headerDiasRestantes);
        headerRow.addView(headerDescuento);

        // Agregar la fila de títulos a la parte superior de la tabla
        tableLayoutClientes.addView(headerRow, 0);


        btnAgregarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar el formulario de ingreso del cliente
                Intent intent = new Intent(ActivityGestorCompradores.this, ActivityIngresoCliente.class);
                startActivityForResult(intent, REQUEST_CODE_INGRESO_CLIENTE);
            }
        });

        // Cargar clientes desde Firestore
        cargarClientesDesdeFirestore();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_INGRESO_CLIENTE) {
            if (resultCode == RESULT_OK && data != null) {
                // Obtener los datos del formulario de ingreso del cliente
                String nombre = data.getStringExtra("nombre");
                boolean esDeudor = data.getBooleanExtra("esDeudor", false);
                int diasRestantes = data.getIntExtra("diasRestantes", 0);
                int descuento = data.getIntExtra("descuento", 0);

                // Agregar los datos a Firestore
                agregarClienteAFirestore(nombre, esDeudor, diasRestantes, descuento);
            }
        }
    }

    private void agregarClienteAFirestore(String nombre, boolean esDeudor, int diasRestantes, int descuento) {
        // Agregar el cliente a Firestore
        CollectionReference clientesRef = db.collection("clientes");
        clientesRef.add(new Cliente(nombre, esDeudor, diasRestantes, descuento));
    }

    private void cargarClientesDesdeFirestore() {
        // Consultar la colección "clientes" en Firestore y ordenar por el campo "nombre" sin distinción entre mayúsculas y minúsculas
        CollectionReference clientesRef = db.collection("clientes");
        clientesRef.orderBy("nombre", Query.Direction.ASCENDING).orderBy("nombre", Query.Direction.DESCENDING)
                .addSnapshotListener(this, (value, error) -> {
                    if (error != null) {
                        // Manejar el error
                        return;
                    }

                    // Borrar las filas existentes en la tabla (excepto la fila de encabezado)
                    int childCount = tableLayoutClientes.getChildCount();
                    if (childCount > 1) {
                        tableLayoutClientes.removeViews(1, childCount - 1);
                    }

                    // Agregar clientes desde Firestore a la tabla
                    for (QueryDocumentSnapshot document : value) {
                        Cliente cliente = document.toObject(Cliente.class);
                        agregarClienteALaTabla(cliente.getNombre(), cliente.isEsDeudor(), cliente.getDiasRestantes(), cliente.getDescuento());
                    }
                });
    }

    private void agregarClienteALaTabla(String nombre, boolean esDeudor, int diasRestantes, int descuento) {
        // Crear una nueva fila para la tabla
        TableRow newRow = new TableRow(this);

        // Configurar ancho de las columnas
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        layoutParams.weight = 1; // Todas las columnas tendrán el mismo ancho

        // Crear TextViews para cada dato y configurar su texto y atributos de diseño
        TextView textViewNombre = new TextView(this);
        textViewNombre.setText(nombre);
        textViewNombre.setLayoutParams(layoutParams);

        TextView textViewDeudor = new TextView(this);
        textViewDeudor.setText(esDeudor ? "Si" : "No");
        textViewDeudor.setLayoutParams(layoutParams);

        TextView textViewDiasRestantes = new TextView(this);
        textViewDiasRestantes.setText(String.valueOf(diasRestantes));
        textViewDiasRestantes.setLayoutParams(layoutParams);

        TextView textViewDescuento = new TextView(this);
        textViewDescuento.setText(String.valueOf(descuento) + "%");
        textViewDescuento.setLayoutParams(layoutParams);

        // Boton para eliminar un cliente de la tabla y db
        Button deleteButton = new Button(this);
        deleteButton.setText("Eliminar");
        deleteButton.setLayoutParams(layoutParams);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el nombre del cliente que se va a eliminar (puedes usar otro identificador único)
                String nombreCliente = nombre;

                // Llamar al método para eliminar el cliente de Firestore
                eliminarClienteDeFirestore(nombreCliente);
            }
        });

        // Agregar TextViews a la fila
        newRow.addView(textViewNombre);
        newRow.addView(textViewDeudor);
        newRow.addView(textViewDiasRestantes);
        newRow.addView(textViewDescuento);
        newRow.addView(deleteButton);

        // Agregar la nueva fila a la tabla
        tableLayoutClientes.addView(newRow);
    }

    // Método para eliminar un cliente de Firestore
    private void eliminarClienteDeFirestore(String nombreCliente) {
        CollectionReference clientesRef = db.collection("clientes");
        clientesRef.whereEqualTo("nombre", nombreCliente)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Elimina el documento del cliente de Firestore
                            document.getReference().delete();
                        }
                        // Vuelve a cargar los clientes desde Firestore para actualizar la tabla
                        cargarClientesDesdeFirestore();
                    } else {
                        // Maneja el error
                    }
                });
    }
}