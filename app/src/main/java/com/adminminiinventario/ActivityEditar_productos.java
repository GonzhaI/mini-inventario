package com.adminminiinventario;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.adminminiinventario.adapter.Producto;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ActivityEditar_productos extends AppCompatActivity {

    private TextView nombreProductoTextView;
    private TextView valorTextView;
    private TextView fechaVencimientoTextView;
    private TextView codigoBarrasTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editarproductos);

        // Inicializa las vistas en tu layout
        nombreProductoTextView = findViewById(R.id.producto);
        valorTextView = findViewById(R.id.valor);
        fechaVencimientoTextView = findViewById(R.id.MostrarFecha);
        codigoBarrasTextView = findViewById(R.id.cdBarra);

        // Obtiene el objeto Producto del intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("productos")) {
            Producto producto = (Producto) intent.getSerializableExtra("productos");

            // Actualiza las vistas con la información del producto
            nombreProductoTextView.setText(producto.getNombre_producto());
            String valorFormateado = formatDouble(producto.getValor());
            valorTextView.setText(valorFormateado);

            // Lógica para la fecha de vencimiento
            if (producto.getFechaVencimiento() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String fechaFormateada = sdf.format(producto.getFechaVencimiento());
                fechaVencimientoTextView.setText(fechaFormateada);
            } else {
                fechaVencimientoTextView.setText("Fecha no\ndisponible");
            }

            // Lógica para el código de barras
            String codigoBarras = producto.getCodigo_barra();
            if (codigoBarras != null && !codigoBarras.isEmpty()) {
                codigoBarrasTextView.setText(codigoBarras);
            } else {
                codigoBarrasTextView.setText("Codigo no disponible");
            }
        }
    }

    // Función para formatear un double a String
    private String formatDouble(double value) {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(value);
    }
}


