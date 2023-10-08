package com.adminminiinventario;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityAgregarProducto extends AppCompatActivity {
    EditText nombreProducto, cdBarrasProducto, cantidadProducto, valorProducto;
    Button btnCamaraCdBarras, btnCamaraImagenProducto, btnAgregarProducto;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);
        //EDIT TEXT
        nombreProducto = findViewById(R.id.producto);
        cdBarrasProducto = findViewById(R.id.cdBarra);
        cantidadProducto = findViewById(R.id.cantidad);
        valorProducto = findViewById(R.id.valor);
        //BOTONES
        btnAgregarProducto = findViewById(R.id.btn_agregar_producto);
        btnCamaraCdBarras = findViewById(R.id.bt_camera_cdBarras);
        btnCamaraImagenProducto = findViewById(R.id.btn_imagen_producto);

        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String producto = nombreProducto.getText().toString().trim();
                String cdBarras = cdBarrasProducto.getText().toString().trim();
                String cantidad = cantidadProducto.getText().toString().trim();
                String valor = valorProducto.getText().toString().trim();


                if (producto.isEmpty() || cdBarras.isEmpty() || cantidad.isEmpty() || valor.isEmpty()) {
                    showMessage("Por favor, completa todos los campos.");

                }
            }
        });

    }
    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Error")
                .setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}