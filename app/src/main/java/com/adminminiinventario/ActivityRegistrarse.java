package com.adminminiinventario;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ActivityRegistrarse extends AppCompatActivity {
    EditText usuarioEditText, nombre_negocioNameEditText, correoEditText, contrasenaEditText, rep_contrasenaEditText;
    Button bt_ingresarButton;
    private FirebaseFirestore db;
    private String businessName;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        usuarioEditText = findViewById(R.id.inp_usuario);
        nombre_negocioNameEditText = findViewById(R.id.inp_nombre_negocio);
        correoEditText = findViewById(R.id.inp_correo);
        contrasenaEditText = findViewById(R.id.inp_contraseña);
        rep_contrasenaEditText = findViewById(R.id.inp_rep_contraseña);
        bt_ingresarButton = findViewById(R.id.bt_ingresar);

        // Inicializa la instancia de Firestore
        db = FirebaseFirestore.getInstance();

        bt_ingresarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usuarioEditText.getText().toString();
                businessName = nombre_negocioNameEditText.getText().toString();
                final String email = correoEditText.getText().toString();
                password = contrasenaEditText.getText().toString();
                final String confirmPassword = rep_contrasenaEditText.getText().toString();

                if (username.isEmpty() || businessName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    showMessage("Por favor, completa todos los campos.");
                    return;
                }

                if (!isValidPassword(password)) {
                    showMessage("La contraseña debe tener al menos 8 caracteres y contener al menos 1 número.");
                    return;
                }

                if (!isValidEmail(email)) {
                    showMessage("El correo ingresado no es válido.");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    showMessage("Las contraseñas no coinciden.");
                    return;
                }

                // Verifica si el usuario o el correo ya existen antes de continuar
                checkUserAndEmailExistence(username, email);
            }
        });

    }

    private boolean isValidPassword(String password) {
        // Verificar si la contraseña cumple con los requisitos (mínimo 8 caracteres y al menos 1 número)
        return password.length() >= 8 && password.matches(".*\\d.*");
    }

    private boolean isValidEmail(String email) {
        // Definir la expresión regular para validar el correo electrónico
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

        // Verificar si el correo cumple con la expresión regular
        return email.matches(regex);
    }

    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Notificación Registro")
                .setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkUserAndEmailExistence(final String username, final String email) {
        // Realiza consultas para verificar si el usuario o el correo ya existen en la base de datos
        // Aquí debes implementar la lógica para consultar Firestore y verificar la existencia
        // Si el usuario o el correo no existen, puedes continuar con el registro, de lo contrario, muestra un mensaje de error

        db.collection("usuarios")
                .whereEqualTo("nombreUsuario", username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                    private void registerUser(final String username, final String businessName, final String email, final String password) {
                        // Crear un mapa con los datos del usuario
                        Map<String, Object> user = new HashMap<>();
                        user.put("username", username);
                        user.put("businessName", businessName);
                        user.put("email", email);

                        // Agregar los datos del usuario a Firestore
                        db.collection("usuarios")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        showMessage("Registro exitoso. ID del documento: " + documentReference.getId());

                                        // Redirigir al usuario a la página principal
                                        Intent intent = new Intent(ActivityRegistrarse.this, Pag_Principal.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        String errorMessage = "Error al registrar el usuario: " + e.getMessage();
                                        Log.e("Firebase", errorMessage);
                                        showMessage(errorMessage);
                                    }
                                });
                    }

                    private boolean userOrEmailExists = false;

// ...

                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            showMessage("El usuario ya existe.");
                            // Marcar que el usuario o correo ya existe
                            userOrEmailExists = true;
                        } else {
                            // Verificar la existencia del correo
                            db.collection("usuarios")
                                    .whereEqualTo("correo", email)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                showMessage("El correo ya está registrado.");
                                                // Marcar que el usuario o correo ya existe
                                                userOrEmailExists = true;
                                            } else {
                                                // Continuar con el registro porque el usuario y el correo no existen
                                                if (!userOrEmailExists) {
                                                    registerUser(username, businessName, email, password);
                                                }
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            showMessage("Error al verificar el correo: " + e.getMessage());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Error al verificar el usuario: " + e.getMessage());
                    }
                });
    }

    private void registerUser(String username, String businessName, String email, String password) {
        // Aquí debes implementar la lógica para registrar al usuario en Firestore
        // Después de registrar al usuario, puedes redirigirlo a la página principal si es exitoso, o mostrar un mensaje de error si falla
    }
}