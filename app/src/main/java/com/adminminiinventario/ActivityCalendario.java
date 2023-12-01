package com.adminminiinventario;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;

public class ActivityCalendario extends AppCompatActivity {
    TextView tv;
    Button botonFechaP;

    EditText nombreDistribuidor;

    private Timestamp fechaTimestamp;
    private FirebaseFirestore db;

    View pantalla_tutorial_agregar_cliente;

    private VideoView vv_tutorial_distribuidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nombreDistribuidor = findViewById(R.id.nombreDistribuidor);
        tv = findViewById(R.id.fecha);
        botonFechaP = findViewById(R.id.botonFecha);
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_calendario);

        String negocio = getIntent().getStringExtra("negocio");
    }



        pantalla_tutorial_agregar_cliente = findViewById(R.id.pantalla_tutorial_agregar_cliente);
        ImageView btnTutorial = findViewById(R.id.btn_tutorial_agregar_cliente);
        ImageView btnTutorialSalir = findViewById(R.id.btn_tutorial_agregar_cliente_salir);

        // Añadir referencias a los TextView que quieres mostrar
        TextView mensajeTutorial1 = findViewById(R.id.tv_mensaje_tutorial_1_agregar_cliente);
        TextView mensajeTutorial2 = findViewById(R.id.tv_mensaje_tutorial_2_agregar_cliente);

        // Inicializar el VideoView
        vv_tutorial_distribuidor = findViewById(R.id.vv_tutorial_ingreso_clientes);

        // Configurar la fuente del video
        String videoTutorial_agregar_productos = "android.resource://" + getPackageName() + "/" + R.raw.video_distruibuidor;
        Uri uri = Uri.parse(videoTutorial_agregar_productos);
        vv_tutorial_distribuidor.setVideoURI(uri);

        MediaController mediaController = new MediaController(this);
        vv_tutorial_distribuidor.setMediaController(mediaController);
        mediaController.setAnchorView(vv_tutorial_distribuidor);

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
                    vv_tutorial_distribuidor.stopPlayback();
                    vv_tutorial_distribuidor.setVisibility(View.GONE);
                } else {
                    pantalla_tutorial_agregar_cliente.setVisibility(View.VISIBLE);
                    btnTutorial.setVisibility(View.GONE);
                    btnTutorialSalir.setVisibility(View.VISIBLE);

                    // Mostrar los TextView
                    mensajeTutorial1.setVisibility(View.VISIBLE);
                    mensajeTutorial2.setVisibility(View.VISIBLE);

                    // Iniciar y mostrar el VideoView
                    vv_tutorial_distribuidor.setVisibility(View.VISIBLE);
                    vv_tutorial_distribuidor.start();
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
                vv_tutorial_distribuidor.stopPlayback();
                vv_tutorial_distribuidor.setVisibility(View.GONE);
            }
        });
    }

        public void abrirCalendario (View view){
            Calendar cal = Calendar.getInstance();
            int anio = cal.get(Calendar.YEAR);
            int mes = cal.get(Calendar.MONTH);
            int dia = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(ActivityCalendario.this, (datePicker, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                fechaTimestamp = new Timestamp(calendar.getTime());

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String fechaFormateada = sdf.format(calendar.getTime());
                tv.setText(fechaFormateada);
            }, anio, mes, dia);
            dpd.show();
        }

    public void MostrarMensaje(View view) {
        if (fechaTimestamp != null) {
            // Obtener la ID del negocio
            String idNegocio = getIntent().getStringExtra("negocio");

            // Obtener el nombre del distribuidor
            String nombreDistribuidorStr = nombreDistribuidor.getText().toString().trim();

            if (nombreDistribuidorStr.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa el nombre del distribuidor", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear un objeto Distribuidor
            Distribuidor distribuidor = new Distribuidor(idNegocio, nombreDistribuidorStr, fechaTimestamp);

            // Agregar el distribuidor a Firebase
            db.collection("llegadaMercaderia")
                    .add(distribuidor)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ActivityCalendario.this, "Distribuidor agregado con éxito", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ActivityCalendario.this, "Error al agregar el distribuidor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No se ha seleccionado una fecha", Toast.LENGTH_SHORT).show();
        }
    }
}






