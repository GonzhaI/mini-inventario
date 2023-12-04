package com.adminminiinventario;

import android.app.DatePickerDialog;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ActivityCalendario extends AppCompatActivity {
    TextView tv;
    Button botonFechaP;
    EditText nombreDistribuidor;
    private Timestamp fechaTimestamp;
    private FirebaseFirestore db;
    View pantalla_tutorial_distribuidor;
    private VideoView vv_tutorial_distribuidor;
    private Mqtt3AsyncClient client; // Declarar a nivel de clase para acceder desde otros métodos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        nombreDistribuidor = findViewById(R.id.nombreDistribuidor);
        tv = findViewById(R.id.fecha);
        botonFechaP = findViewById(R.id.botonFecha);
        db = FirebaseFirestore.getInstance();

        iniciarConexionMQTT();

        String negocio = getIntent().getStringExtra("negocio");

        pantalla_tutorial_distribuidor = findViewById(R.id.pantalla_tutorial_distribuidor);
        ImageView btnTutorial = findViewById(R.id.btn_tutorial_distribuidor);
        ImageView btnTutorialSalir = findViewById(R.id.btn_tutorial_distribuidor_salir);

        // Añadir referencias a los TextView que quieres mostrar
        TextView mensajeTutorial1 = findViewById(R.id.tv_mensaje_tutorial_1_distribuidor);
        TextView mensajeTutorial2 = findViewById(R.id.tv_mensaje_tutorial_2_distribuidor);

        // Inicializar el VideoView
        vv_tutorial_distribuidor = findViewById(R.id.vv_tutorial_distribuidor);

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
                if (pantalla_tutorial_distribuidor.getVisibility() == View.VISIBLE) {
                    pantalla_tutorial_distribuidor.setVisibility(View.GONE);
                    btnTutorialSalir.setVisibility(View.GONE);
                    btnTutorial.setVisibility(View.VISIBLE);

                    // Ocultar los TextView
                    mensajeTutorial1.setVisibility(View.GONE);
                    mensajeTutorial2.setVisibility(View.GONE);

                    // Detener y ocultar el VideoView
                    vv_tutorial_distribuidor.stopPlayback();
                    vv_tutorial_distribuidor.setVisibility(View.GONE);
                } else {
                    pantalla_tutorial_distribuidor.setVisibility(View.VISIBLE);
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
        pantalla_tutorial_distribuidor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Revierte los cambios al hacer clic en el overlayView
                pantalla_tutorial_distribuidor.setVisibility(View.GONE);
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

    public void abrirCalendario(View view) {
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
            String idNegocio = getIntent().getStringExtra("negocio").toLowerCase();

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

                        // Enviar mensaje MQTT al tópico
                        enviarMensajeMQTT(nombreDistribuidorStr);
                    })
                    .addOnFailureListener(e -> Toast.makeText(ActivityCalendario.this, "Error al agregar el distribuidor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No se ha seleccionado una fecha", Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciarConexionMQTT() {
        String clusterUrl = "9655037d6bc746c999d1edcdd48fd3b1.s2.eu.hivemq.cloud";
        int port = 8883;
        String username = "test.test";
        String password = "test.test1";

        client = MqttClient.builder()
                .useMqttVersion3()
                .identifier("ClienteCalendario" + System.currentTimeMillis())
                .serverHost(clusterUrl)
                .serverPort(port)
                .useSslWithDefaultConfig()
                .buildAsync();

        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(password.getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((connAck, throwable) -> {
                    if (throwable != null) {
                        // Manejar el fallo de conexión MQTT
                    } else {
                        // Conexión MQTT exitosa
                    }
                });
    }

    private void enviarMensajeMQTT(String nombreDistribuidor) {
        String topic = "distribuidores";

        String mensaje = "Nuevo distribuidor: " + nombreDistribuidor;

        client.publishWith()
                .topic(topic)
                .payload(mensaje.getBytes())
                .send()
                .whenComplete((publish, throwable) -> {
                    if (throwable != null) {
                        // Manejar el fallo de publicación MQTT
                    } else {
                        // Publicación MQTT exitosa
                    }
                });
    }

    public void Ir_a_ListaDistribuidores(View view) {
        Intent intent = new Intent(this, ActivityListaDistribuidores.class);

        // Iniciar el nuevo Activity
        startActivity(intent);
    }
}
