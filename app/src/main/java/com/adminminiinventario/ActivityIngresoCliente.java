package com.adminminiinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityIngresoCliente extends AppCompatActivity {

    private EditText editTextNombre, editTextDiasRestantes, editTextDescuento;
    private CheckBox checkBoxDeudor;
    private Button btnGuardar;

    // Referencia a Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_ingreso_cliente);

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

                // Agregar el cliente a Firestore
                db.collection("clientes")
                        .add(cliente)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    // Cliente agregado exitosamente
                                    DocumentReference documentReference = task.getResult();
                                    String clienteKey = documentReference.getId();

                                    // Devolver los datos a ActivityGestorCompradores
                                    Intent data = new Intent();
                                    data.putExtra("nombre", nombre);
                                    data.putExtra("esDeudor", esDeudor);
                                    data.putExtra("diasRestantes", diasRestantes);
                                    data.putExtra("descuento", descuento);
                                    data.putExtra("clienteKey", clienteKey);

                                    setResult(RESULT_OK, data);

                                    // Crear un Intent para redirigir a ActivityGestorCompradores
                                    Intent intent = new Intent(ActivityIngresoCliente.this, ActivityGestorCompradores.class);

                                    // Iniciar la actividad
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Manejar errores al agregar el cliente
                                }
                            }
                        });
            }
        });
    }
}