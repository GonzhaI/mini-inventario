package com.adminminiinventario;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;


public class ActivityRegistrarse extends AppCompatActivity {
    //Tienes que inicializar la base de datos en este MAIN
    EditText usuarioEditText, nombre_negocioNameEditText, correoEditText, contrasenaEditText, rep_contrasenaEditText;
    Button bt_ingresarButton;

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



        bt_ingresarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usuarioEditText.getText().toString();
                String businessName = nombre_negocioNameEditText.getText().toString();
                String email = correoEditText.getText().toString();
                String password = contrasenaEditText.getText().toString();
                String confirmPassword = rep_contrasenaEditText.getText().toString();



                if (username.isEmpty()) {
                    showMessage("Debes ingresar un usuario");
                    return;
                }

                if (businessName.isEmpty()) {
                    showMessage("Debes ingresar el nombre de tu negocio");
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

                // Si todas las validaciones pasaron, puedes registrar al usuario aquí
                // Por ejemplo, puedes guardar los datos en una base de datos o realizar una solicitud de registro a través de una API.

                // Una vez registrado, redirige al usuario a la pantalla principal
                Intent intent = new Intent(ActivityRegistrarse.this, ActivityLogin.class);
                startActivity(intent);
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
                .setTitle("Error")
                .setPositiveButton("Aceptar", null);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

}
