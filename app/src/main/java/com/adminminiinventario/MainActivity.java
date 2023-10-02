package com.adminminiinventario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    Button bt_ingresar;
    Button bt_prueba;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_ingresar=(Button)findViewById(R.id.bt_registrarse);
        bt_prueba=(Button)findViewById(R.id.bt_prueba);

        bt_ingresar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ActivityRegistrarse.class);
                startActivity(i);
            }
        });

        bt_prueba.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ActivityCalendario.class);
                startActivity(i);
            }
        });



    }
    public void irLogin(View view) {
        // Crear un Intent para ir a la otra actividad
        Intent intent = new Intent(this, ActivityLogin.class);
        startActivity(intent);
    }
}