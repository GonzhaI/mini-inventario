package com.adminminiinventario;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

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

    View pantalla_tutorial_agregar_cliente;

    private VideoView vv_tutorial_ingreso_clientes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_ingreso_cliente);

        pantalla_tutorial_agregar_cliente = findViewById(R.id.pantalla_tutorial_agregar_cliente);
        ImageView btnTutorial = findViewById(R.id.btn_tutorial_agregar_cliente);
        ImageView btnTutorialSalir = findViewById(R.id.btn_tutorial_agregar_cliente_salir);

        // Añadir referencias a los TextView que quieres mostrar
        TextView mensajeTutorial1 = findViewById(R.id.tv_mensaje_tutorial_1_agregar_cliente);
        TextView mensajeTutorial2 = findViewById(R.id.tv_mensaje_tutorial_2_agregar_cliente);

        // Inicializar el VideoView
        vv_tutorial_ingreso_clientes = findViewById(R.id.vv_tutorial_ingreso_clientes);

        // Configurar la fuente del video
        String videoTutorial_agregar_productos = "android.resource://" + getPackageName() + "/" + R.raw.video_gestion_compradores;
        Uri uri = Uri.parse(videoTutorial_agregar_productos);
        vv_tutorial_ingreso_clientes.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        vv_tutorial_ingreso_clientes.setMediaController(mediaController);
        mediaController.setAnchorView(vv_tutorial_ingreso_clientes);

        btnTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar visibilidad del overlayView y botones
                if (pantalla_tutorial_agregar_cliente.getVisibility() == View.VISIBLE) {
                    pantalla_tutorial_agregar_cliente.setVisibility(View.GONE);
                    btnTutorialSalir.setVisibility(View.GONE);
                    btnTutorial.setVisibility(View.VISIBLE);

                    // Ocultar los TextView
                    mensajeTutorial1.setVisibility(View.GONE);
                    mensajeTutorial2.setVisibility(View.GONE);

                    // Detener y ocultar el VideoView
                    vv_tutorial_ingreso_clientes.stopPlayback();
                    vv_tutorial_ingreso_clientes.setVisibility(View.GONE);
                } else {
                    pantalla_tutorial_agregar_cliente.setVisibility(View.VISIBLE);
                    btnTutorial.setVisibility(View.GONE);
                    btnTutorialSalir.setVisibility(View.VISIBLE);

                    // Mostrar los TextView
                    mensajeTutorial1.setVisibility(View.VISIBLE);
                    mensajeTutorial2.setVisibility(View.VISIBLE);

                    // Iniciar y mostrar el VideoView
                    vv_tutorial_ingreso_clientes.setVisibility(View.VISIBLE);
                    vv_tutorial_ingreso_clientes.start();
                }
            }
        });

        // Agregar un OnClickListener al overlayView para manejar la visibilidad de los TextView
        pantalla_tutorial_agregar_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Revierte los cambios al hacer clic en el overlayView
                pantalla_tutorial_agregar_cliente.setVisibility(View.GONE);
                btnTutorialSalir.setVisibility(View.GONE);
                btnTutorial.setVisibility(View.VISIBLE);

                // Ocultar los TextView
                mensajeTutorial1.setVisibility(View.GONE);
                mensajeTutorial2.setVisibility(View.GONE);

                // Detener y ocultar el VideoView
                vv_tutorial_ingreso_clientes.stopPlayback();
                vv_tutorial_ingreso_clientes.setVisibility(View.GONE);
            }
        });






















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