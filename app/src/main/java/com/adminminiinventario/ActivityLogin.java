package com.adminminiinventario;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

public class ActivityLogin extends AppCompatActivity {
    EditText passLogin, userLogin;
    Button bt_login;
    FirebaseFirestore db;
    FirebaseAuth auth;

    private PendingIntent pendingIntent;
    private final static String CHANNEL_ID = "NOTIFICACION";
    private final static int NOTIFICACION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        passLogin = findViewById(R.id.Password);
        userLogin = findViewById(R.id.inp_usuario);
        bt_login = findViewById(R.id.btn_login);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = userLogin.getText().toString().trim();
                String contrasena = passLogin.getText().toString().trim();

                if (correo.isEmpty() || contrasena.isEmpty()) {
                    showMessage("Por favor, completa todos los campos.");
                } else {
                    login(correo, contrasena);
                }
            }
        });

    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "NEW", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        createNotification();
    }

    private void createNotification() {
        setPendingIntent(Notificaciones.class);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                CHANNEL_ID)
                .setSmallIcon(R.drawable.imagen_predeterminada)
                .setContentTitle("Stock")
                .setContentText("Recuerda revisar tu Stock")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        managerCompat.notify(1, builder.build());
    }

    private void setPendingIntent(Class<?> clsActivity) {
        Intent intent = new Intent(this, clsActivity);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
    }


    private void login(String correo, String contrasena) {
        auth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Usuario ha iniciado sesión correctamente
                    obtenerNegocioYContinuar();
                    Toast.makeText(ActivityLogin.this, "Bienvenido", Toast.LENGTH_SHORT).show();

                    // Agregar la lógica de conexión MQTT y publicación aquí
                    iniciarConexionMQTT(correo);

                    createNotification();
                    createNotificationChannel();

                } else {
                    Toast.makeText(ActivityLogin.this, "Error al ingresar datos", Toast.LENGTH_SHORT).show();
                }
                if (task.getException() != null) {
                    String errorMessage = "Error al iniciar sesión. Verifique sus credenciales.";
                    Exception exception = task.getException();
                    errorMessage = exception.getMessage(); // Obtener el mensaje de error específico
                    showMessage(errorMessage);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivityLogin.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerNegocioYContinuar() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference usuarioRef = db.collection("usuario").document(userId);

            usuarioRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String negocio = documentSnapshot.getString("negocio");
                        if (negocio != null) {
                            // Iniciar la actividad Pag_Principal y pasar el valor de "negocio"
                            Intent intent = new Intent(ActivityLogin.this, Pag_Principal.class);
                            intent.putExtra("negocio", negocio);
                            startActivity(intent);
                            finish(); // Cerrar la actividad actual
                        } else {
                            showMessage("El campo 'negocio' no existe en el documento del usuario.");
                        }
                    } else {
                        showMessage("El documento del usuario no existe en Firestore.");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showMessage("Error al obtener el campo 'negocio' del usuario.");
                }
            });
        }
    }

    private void iniciarConexionMQTT(String usuario) {
        String clusterUrl = "9655037d6bc746c999d1edcdd48fd3b1.s2.eu.hivemq.cloud";
        int port = 8883;
        String username = "test.test";
        String password = "test.test1";
        String clientId = usuario + System.currentTimeMillis();
        Log.e("Client ID: ", clientId);

        Mqtt3AsyncClient client = MqttClient.builder()
                .useMqttVersion3()
                .identifier(clientId)
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
                        // Configurar suscripciones o iniciar la publicación
                        suscribirATopicoUsuarios(usuario, client);
                        publicarMensajeATopico(usuario, client, "He ingresado con exito!");
                    }
                });

    }

    private void suscribirATopicoUsuarios(String usuario, Mqtt3AsyncClient client) {
        String topicUsuarios = "usuarios";

        client.subscribeWith()
                .topicFilter(topicUsuarios)
                .callback(publish -> {
                    // Manejar los mensajes recibidos en el tópico de usuarios
                    byte[] payload = publish.getPayloadAsBytes();
                    String mensajeRecibido = new String(payload);
                    Log.e("Mensaje recibido", mensajeRecibido);
                })
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable != null) {
                        // Manejar el fallo de suscripción MQTT
                    } else {
                        // Manejar la suscripción exitosa
                    }
                });
    }


    private void publicarMensajeATopico(String usuario, Mqtt3AsyncClient client, String mensaje) {
        String topicUsuario = "usuarios";
        String registro = "Ha ingresado "+ usuario;
        client.publishWith()
                .topic(topicUsuario)
                .payload(registro.getBytes())
                .send()
                .whenComplete((publish, throwable) -> {
                    if (throwable != null) {
                        // Manejar el fallo de publicación MQTT
                        Log.v("Fallo de publicación", throwable.getMessage());
                    } else {
                        // Manejar la publicación exitosa, por ejemplo, registro o métricas
                        Log.v("Publicación exitosa", "He ingresado en el topic! " + topicUsuario);
                        Log.v("MQTT", registro);
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