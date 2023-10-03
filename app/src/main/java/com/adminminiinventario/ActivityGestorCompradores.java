package com.adminminiinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityGestorCompradores extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private static final int REQUEST_CODE_INGRESO_CLIENTE = 1;
    private TableLayout tableLayoutClientes;
    private Button btnAgregarCliente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestor_compradores);

        // Inicializa Firebase
        FirebaseApp.initializeApp(this);

        // Referencia a la base de datos Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("clientes");

        // Agrega un oyente para escuchar los cambios en la base de datos
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Cliente cliente = dataSnapshot.getValue(Cliente.class);

                if (cliente != null) {
                    agregarClienteALaTabla(cliente.getNombre(), cliente.isEsDeudor(), cliente.getDiasRestantes(), cliente.getDescuento());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        tableLayoutClientes = findViewById(R.id.tableLayoutClientes);
        btnAgregarCliente = findViewById(R.id.btnAgregarCliente);

        // Crear una fila para los títulos de las columnas
        TableRow headerRow = new TableRow(this);

        // Crear TextViews para los títulos
        TextView headerNombre = new TextView(this);
        headerNombre.setText("Nombre del Cliente");

        TextView headerDeudor = new TextView(this);
        headerDeudor.setText("¿Es Deudor?");

        TextView headerDiasRestantes = new TextView(this);
        headerDiasRestantes.setText("Días Restantes");

        TextView headerDescuento = new TextView(this);
        headerDescuento.setText("Descuento (%)");

        // Configurar espacio de columnas
        headerNombre.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        headerDeudor.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        headerDiasRestantes.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        headerDescuento.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

        // Agregar titulos a las columnas
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

                // Agregar los datos a la tabla
                agregarClienteALaTabla(nombre, esDeudor, diasRestantes, descuento);
            }
        }
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

        // Agregar TextViews a la fila
        newRow.addView(textViewNombre);
        newRow.addView(textViewDeudor);
        newRow.addView(textViewDiasRestantes);
        newRow.addView(textViewDescuento);

        // Agregar la nueva fila a la tabla
        tableLayoutClientes.addView(newRow);
    }
}