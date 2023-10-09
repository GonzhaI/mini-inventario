package com.adminminiinventario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.adminminiinventario.model.Productos;
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
        db = FirebaseFirestore.getInstance();

        TextView negocioNombreTextView = findViewById(R.id.negocioNombre);
        String negocio = getIntent().getStringExtra("negocio");
        negocioNombreTextView.setText(negocio);

        btnAgregarProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idNegocio = negocio.toLowerCase();
                String producto = nombreProducto.getText().toString().trim().toLowerCase();
                String cdBarras = cdBarrasProducto.getText().toString().trim().toLowerCase();
                String cantidad = cantidadProducto.getText().toString().trim().toLowerCase();
                String valor = valorProducto.getText().toString().trim().toLowerCase();


                int cantidadInt = Integer.parseInt(valor);
                int valorInt = Integer.parseInt(cantidad);

                if (producto.isEmpty()) {
                    showMessage("Por favor, completa todos los campos.");

                }else{
                    Productos nuevoProducto = new Productos(idNegocio,cantidadInt, cdBarras, producto, valorInt);
                    agregarProductoAFirestore(nuevoProducto);
                }
            }
        });

    }
    private void agregarProductoAFirestore(Productos producto) {
        db.collection("productos")
                .add(producto)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        showMessage("Producto agregado con éxito.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Error al agregar el producto: " + e.getMessage());
                    }
                });
    }
    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Notificacion")
                .setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}