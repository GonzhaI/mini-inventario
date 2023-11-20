package com.adminminiinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {


    private FirebaseFirestore db;

    Button bt_ingresar;
    TextView bt_editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_ingresar=(Button)findViewById(R.id.bt_registrarse);

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance();
        bt_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para abrir PaginaPrincipal
                Intent intent = new Intent(MainActivity.this, ActivityRegistrarse.class);
                startActivity(intent);
            }
        });

    }

    public void irLogin(View view) {
        // Crear un Intent para ir a la otra actividad
        Intent intent = new Intent(this, ActivityLogin.class);
        startActivity(intent);
    }

    public void abrirCalendarioAlimentos(View view) {
    }
}