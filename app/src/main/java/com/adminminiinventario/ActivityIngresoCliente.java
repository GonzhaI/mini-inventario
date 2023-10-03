package com.adminminiinventario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActivityIngresoCliente extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private EditText editTextNombre, editTextDiasRestantes, editTextDescuento;
    private CheckBox checkBoxDeudor;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_ingreso_cliente);

        // Inicializa la referencia a la base de datos Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("clientes");
        editTextNombre = findViewById(R.id.editTextNombre);
        checkBoxDeudor = findViewById(R.id.checkBoxDeudor);
        editTextDiasRestantes = findViewById(R.id.editTextDiasRestantes);
        editTextDescuento = findViewById(R.id.editTextDescuento);
        btnGuardar = findViewById(R.id.btnGuardar);


        // Boton guardar
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener valores de los campos
                String nombre = editTextNombre.getText().toString();
                boolean esDeudor = checkBoxDeudor.isChecked();
                int diasRestantes = Integer.parseInt(editTextDiasRestantes.getText().toString());
                int descuento = Integer.parseInt(editTextDescuento.getText().toString());

                // Crear un objeto para el cliente
                Cliente cliente = new Cliente(nombre, esDeudor, diasRestantes, descuento);

                // Agregar el cliente a la base de datos
                String clienteKey = databaseReference.push().getKey();
                databaseReference.child(clienteKey).setValue(cliente);

                // Devolver los datos a ActivityGestorCompradores
                Intent data = new Intent();
                data.putExtra("nombre", nombre);
                data.putExtra("esDeudor", esDeudor);
                data.putExtra("diasRestantes", diasRestantes);
                data.putExtra("descuento", descuento);

                setResult(RESULT_OK, data);

                // Crear un Intent para redirigir a ActivityGestorCompradores
                Intent intent = new Intent(ActivityIngresoCliente.this, ActivityGestorCompradores.class);

                // Iniciar la actividad
                startActivity(intent);
                finish();
            }
        });
    }
}