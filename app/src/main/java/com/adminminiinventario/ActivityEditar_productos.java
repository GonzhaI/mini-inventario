package com.adminminiinventario;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adminminiinventario.adapter.Producto;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityEditar_productos extends AppCompatActivity {

    private FirebaseFirestore db;
    TextView MostrarFecha;
    private TextView nombreUsuario;
    private TextView nombreProductoTextView;
    private TextView valorTextView;
    private TextView fechaVencimientoTextView;
    private TextView codigoBarrasTextView;
    private TextView cantidadTextView;
    private ImageView imagenProductoImageView;
    private Timestamp fechaVencimiento;

    // Variable para almacenar el código de barras del producto
    private String codigoBarrasProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editarproductos);

        // Inicializa las vistas en tu layout
        nombreProductoTextView = findViewById(R.id.producto);
        valorTextView = findViewById(R.id.valor);
        fechaVencimientoTextView = findViewById(R.id.MostrarFecha);
        codigoBarrasTextView = findViewById(R.id.cdBarra);
        cantidadTextView = findViewById(R.id.cantidad);
        imagenProductoImageView = findViewById(R.id.imagen_producto);
        MostrarFecha = findViewById(R.id.MostrarFecha);

        // Agrega estas líneas para inicializar el TextView
        nombreUsuario = findViewById(R.id.nombre_usuario);
        db = FirebaseFirestore.getInstance();

        // Obtén el id_negocio del usuario actual y móstralo en el TextView
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();

            db.collection("usuario")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String idNegocio = documentSnapshot.getString("negocio").toLowerCase();
                                nombreUsuario.setText(idNegocio);
                            }
                        }
                    });
        }

        // Obtiene el objeto Producto del intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("productos")) {
            Producto producto = (Producto) intent.getSerializableExtra("productos");

            // Guarda el código de barras del producto para usarlo al actualizar
            codigoBarrasProducto = producto.getCodigo_barra();

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
                fechaVencimientoTextView.setText("Fecha no disponible");
            }

            // Lógica para el código de barras
            String codigoBarras = producto.getCodigo_barra();
            if (codigoBarras != null && !codigoBarras.isEmpty()) {
                codigoBarrasTextView.setText(codigoBarras);
            } else {
                codigoBarrasTextView.setText("Codigo no disponible");
            }

            // Lógica para la cantidad
            int cantidad = producto.getCantidad();
            cantidadTextView.setText(String.valueOf(cantidad));

            // Lógica para la imagen
            if (producto.getImagenURL() != null && !producto.getImagenURL().isEmpty()) {
                Picasso.get().load(producto.getImagenURL()).into(imagenProductoImageView);
            } else {
                imagenProductoImageView.setImageResource(R.drawable.imagen_predeterminada);
            }
        }
        Button btnGuardarCambios = findViewById(R.id.btn_agregar_producto);
        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llama al método para actualizar el producto en Firestore
                actualizarProductoEnFirestore();
            }
        });
    }

    // Función para formatear un double a String
    private String formatDouble(double value) {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(value);
    }

    // Abrir calendario
    public void abrirCalendarioAlimentos(View view) {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ActivityEditar_productos.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                // Crea un Timestamp con la fecha seleccionada
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                fechaVencimiento = new Timestamp(calendar.getTime());

                // Actualiza el TextView con la fecha formateada
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String fechaFormateada = sdf.format(calendar.getTime());
                MostrarFecha.setText(fechaFormateada);

                // Imprime la fecha en la consola
                Log.d("Fecha", "Fecha seleccionada: " + fechaFormateada);
            }
        }, anio, mes, dia);
        dpd.show();
    }

    // Método para actualizar el producto en Firestore
    private void actualizarProductoEnFirestore() {
        // Obtén los nuevos datos editados
        String nuevoNombre = nombreProductoTextView.getText().toString().trim();
        String nuevoCodigoBarras = codigoBarrasTextView.getText().toString().trim();
        int nuevaCantidad = Integer.parseInt(cantidadTextView.getText().toString().trim());
        double nuevoValor = Double.parseDouble(valorTextView.getText().toString().trim());
        Timestamp nuevaFechaVencimiento = parseFechaVencimiento(fechaVencimientoTextView.getText().toString().trim());

        // Verifica que el nombre del producto y el id_negocio coincidan
        String idNegocio = nombreUsuario.getText().toString().toLowerCase();
        if (idNegocio.isEmpty()) {
            showMessage("Error: no se pudo obtener el id_negocio del usuario");
            return;
        }

        Log.d("ActualizarProducto", "ID Negocio: " + idNegocio);
        Log.d("ActualizarProducto", "Código de Barras a actualizar: " + nuevoCodigoBarras);

        // Crea un Map con los nuevos datos
        Map<String, Object> nuevosDatos = new HashMap<>();
        nuevosDatos.put("producto", nuevoNombre);
        nuevosDatos.put("cdBarras", nuevoCodigoBarras);
        nuevosDatos.put("cantidad", nuevaCantidad);
        nuevosDatos.put("valor", nuevoValor);
        nuevosDatos.put("fechaVencimiento", nuevaFechaVencimiento);

        // Accede a la instancia de FirebaseFirestore y actualiza el primer documento encontrado
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("productos")
                .whereEqualTo("id_negocio", idNegocio)
                .whereEqualTo("cdBarras", codigoBarrasProducto)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Obtiene el primer documento
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Log.d("ActualizarProducto", "ID Documento: " + document.getId());

                        // Actualiza el documento con los nuevos datos
                        db.collection("productos").document(document.getId())
                                .update(nuevosDatos)
                                .addOnSuccessListener(aVoid -> {
                                    // Éxito al actualizar
                                    // Puedes mostrar un mensaje o realizar otras acciones si es necesario
                                    showMessage("Producto actualizado con éxito");
                                })
                                .addOnFailureListener(e -> {
                                    // Error al actualizar
                                    // Puedes mostrar un mensaje o realizar otras acciones si es necesario
                                    showMessage("Error al actualizar el producto: " + e.getMessage());
                                });
                    } else {
                        showMessage("Error al obtener el producto a actualizar: " + task.getException());
                    }
                });
    }

    // Función para mostrar un mensaje (puedes personalizar según tu necesidad)
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Función para convertir la cadena de fecha de vencimiento a un objeto Date (puedes ajustar según tus necesidades)
    private Timestamp parseFechaVencimiento(String fechaVencimientoStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date fecha = sdf.parse(fechaVencimientoStr);

            // Convierte la Date a Timestamp y retorna
            return fecha != null ? new Timestamp(fecha) : null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
