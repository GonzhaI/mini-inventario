package com.adminminiinventario;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActivityRegistrarse extends AppCompatActivity {

    EditText usuarioEditText, nombre_negocioNameEditText, correoEditText, contrasenaEditText, rep_contrasenaEditText;
    Button bt_ingresarButton;

    private String businessName;
    private String password;

    FirebaseFirestore db;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);



        // Inicializa las variables dentro del método onCreate
        usuarioEditText = findViewById(R.id.inp_usuario);
        nombre_negocioNameEditText = findViewById(R.id.inp_nombre_negocio);
        correoEditText = findViewById(R.id.inp_correo);
        contrasenaEditText = findViewById(R.id.inp_contraseña);
        rep_contrasenaEditText = findViewById(R.id.inp_rep_contraseña);
        bt_ingresarButton = findViewById(R.id.bt_ingresar);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


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

                else if (!isValidPassword(password)) {
                    showMessage("La contraseña debe tener al menos 8 caracteres y contener al menos 1 número.");
                    return;
                }

                else if (!isValidEmail(email)) {
                    showMessage("El correo ingresado no es válido.");
                    return;
                }

                else if (!password.equals(confirmPassword)) {
                    showMessage("Las contraseñas no coinciden.");
                    return;
                } else {


                                String nombreUsuario = usuarioEditText.getText().toString().trim();
                                String nombreNeogocio = nombre_negocioNameEditText.getText().toString().trim();
                                String correo = correoEditText.getText().toString().trim();
                                String contrasena = contrasenaEditText.getText().toString().trim();
                                findUser(nombreUsuario, nombreNeogocio, correo, contrasena);


                    }




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
    private void findUser(String nombreUsuario, String nombreNegocio, String correo, String contrasena) {
        db.collection("usuario")
                .whereEqualTo("usuario", nombreUsuario)
                .get()
                .addOnCompleteListener(usernameQuery -> {
                    if (usernameQuery.isSuccessful()) {
                        if (usernameQuery.getResult().isEmpty()) {

                            checkEmailAndRegister(nombreUsuario, nombreNegocio, correo, contrasena);
                        } else {
                            showMessage("El nombre de usuario ya está registrado");
                        }
                    } else {
                        // Manejo de errores en la consulta de nombre de usuario
                        showMessage("Error al comprobar la existencia del usuario.");
                    }
                });
    }
    private void checkEmailAndRegister(String nombreUsuario, String nombreNegocio, String correo, String contrasena) {
        // Comprobar la existencia del correo electrónico
        db.collection("usuario")
                .whereEqualTo("correo", correo)
                .get()
                .addOnCompleteListener(emailQuery -> {
                    if (emailQuery.isSuccessful()) {
                        if (emailQuery.getResult().isEmpty()) {
                            // El correo electrónico no existe, proceder con el registro
                            register(nombreUsuario, nombreNegocio, correo, contrasena);
                        } else {
                            showMessage("El correo electrónico ya está registrado.");
                        }
                    } else {
                        // Manejo de errores en la consulta de correo electrónico
                        showMessage("Error al comprobar la existencia del correo electrónico.");
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
    private void register(String nombreUsuario, String nombreNegocio, String correo, String contrasena){
        auth.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                String id = auth.getCurrentUser().getUid();
                Map<String, Object> map =new HashMap<>();
                map.put("id", id);
                map.put("usuario", nombreUsuario);
                map.put("negocio", nombreNegocio);
                map.put("correo",correo);

                db.collection("usuario").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                        startActivity(new Intent(ActivityRegistrarse.this, Pag_Principal.class) );
                        Toast.makeText(ActivityRegistrarse.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityRegistrarse.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivityRegistrarse.this, "Error al registrar", Toast.LENGTH_SHORT).show();
            }
        });
    }


}