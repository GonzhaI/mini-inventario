package com.adminminiinventario;

import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    Button bt_ingresar;

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
}