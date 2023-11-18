package com.adminminiinventario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityCasilla_inventario extends AppCompatActivity {

    private FirebaseFirestore db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
   super.onCreate(savedInstanceState);
   setContentView(R.layout.casilla_inventario);

   };

    public void irEditar(View view) {
        // Crear un Intent para ir a la otra actividad
        Intent intent = new Intent(this, ActivityEditar_productos.class);
        startActivity(intent);
    }






}



