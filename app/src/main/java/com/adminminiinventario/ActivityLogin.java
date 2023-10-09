package com.adminminiinventario;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

public class ActivityLogin extends AppCompatActivity {
    EditText passLogin, userLogin;
    Button bt_login;
    FirebaseFirestore db;
    FirebaseAuth auth;
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

                }else {

                    login(correo, contrasena);


                }
            }
        });

    }


     private void login(String correo, String contrasena){
        auth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    obtenerNegocioYContinuar();
                    Toast.makeText(ActivityLogin.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                }else{
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
    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Error")
                .setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}