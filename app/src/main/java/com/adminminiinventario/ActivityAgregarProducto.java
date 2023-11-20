package com.adminminiinventario;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityAgregarProducto extends AppCompatActivity {

    TextView MostrarFecha;
    EditText nombreProducto, cdBarrasProducto, cantidadProducto, valorProducto;
    private ImageView imagenProducto;
    Button btnCamaraImagenProducto, btnAgregarProducto, btnScanCodigoBarras;
    private static final int REQUEST_IMAGE_CAPTURE_PRODUCT = 2;
    FirebaseFirestore db;
    private Timestamp fechaVencimiento;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        MostrarFecha = findViewById(R.id.MostrarFecha);
        nombreProducto = findViewById(R.id.producto);
        cdBarrasProducto = findViewById(R.id.cdBarra);
        cantidadProducto = findViewById(R.id.cantidad);
        valorProducto = findViewById(R.id.valor);
        btnAgregarProducto = findViewById(R.id.btn_agregar_producto);
        btnScanCodigoBarras = findViewById(R.id.btnScanCodigoBarras);
        btnScanCodigoBarras.setOnClickListener(v -> escanearCodigoBarras());

        btnCamaraImagenProducto = findViewById(R.id.btn_imagen_producto);
        imagenProducto = findViewById(R.id.imagen_producto);
        db = FirebaseFirestore.getInstance();

        btnCamaraImagenProducto.setOnClickListener(v -> dispatchTakePictureIntent());

        MostrarFecha.setOnClickListener(v -> abrirCalendarioAlimentos());

        btnAgregarProducto.setOnClickListener(view -> agregarProductoAFirebase());
    }
    private void escanearCodigoBarras() {
        IntentIntegrator intent = new IntentIntegrator(this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intent.setPrompt("Escanear Codigo");
        intent.setCameraId(0);
        intent.setBeepEnabled(true);
        intent.setBarcodeImageEnabled(true);
        intent.setOrientationLocked(true);
        intent.initiateScan();
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_PRODUCT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE_PRODUCT && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imagenProducto.setImageBitmap(imageBitmap);
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Lectura Cancelada", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    cdBarrasProducto.setText(result.getContents());
                }
            }
        }
    }



    public void abrirCalendarioAlimentos() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ActivityAgregarProducto.this, (datePicker, year, month, dayOfMonth) -> {
            calendario.set(year, month, dayOfMonth);
            fechaVencimiento = new Timestamp(calendario.getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fechaFormateada = sdf.format(calendario.getTime());
            MostrarFecha.setText(fechaFormateada);
        }, anio, mes, dia);
        dpd.show();
    }

    private Bitmap obtenerImagenPredeterminada() {
        Drawable drawable = getResources().getDrawable(R.drawable.imagen_predeterminada);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        return bitmapDrawable.getBitmap();
    }

    private void agregarProductoAFirebase() {
        // Obtener los valores de los campos
        String producto = nombreProducto.getText().toString().trim();
        String cdBarras = cdBarrasProducto.getText().toString().trim();
        String cantidadStr = cantidadProducto.getText().toString().trim();
        String valorStr = valorProducto.getText().toString().trim();
        String idNegocio = getIntent().getStringExtra("negocio"); // Obtener el id_negocio

        // Validación de campos vacíos
        if (producto.isEmpty() || cdBarras.isEmpty() || cantidadStr.isEmpty() || valorStr.isEmpty()) {
            showMessage("Por favor, completa todos los campos.");
        } else {
            int cantidad = Integer.parseInt(cantidadStr);
            int valor = Integer.parseInt(valorStr);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if (imageBitmap != null) {
                // Si imageBitmap no es nulo, lo comprimimos
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            } else {
                // Si imageBitmap es nulo, obtenemos la imagen predeterminada y la comprimimos
                Bitmap imagenPredeterminada = obtenerImagenPredeterminada();
                imagenPredeterminada.compress(Bitmap.CompressFormat.PNG, 100, baos);
            }

            byte[] data = baos.toByteArray();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("imagenes_productos").child(cdBarras + ".jpg");

            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> {
                showMessage("Error al subir la imagen: " + exception.getMessage());
            }).addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    Map<String, Object> datos = new HashMap<>();
                    // Agregar los datos del producto al Map
                    datos.put("id_negocio", idNegocio);
                    datos.put("producto", producto);
                    datos.put("cdBarras", cdBarras);
                    datos.put("cantidad", cantidad);
                    datos.put("valor", valor);
                    datos.put("imagenUrl", imageUrl);

                    if (fechaVencimiento != null) {
                        datos.put("fechaVencimiento", fechaVencimiento);
                    }

                    // Guardar en Firebase
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("productos")
                            .add(datos)
                            .addOnSuccessListener(documentReference -> showMessage("Producto agregado con éxito."))
                            .addOnFailureListener(e -> showMessage("Error al agregar el producto: " + e.getMessage()));
                });
            });
        }
    }

    public void abrirCalendarioAlimentos(View view) {
        Calendar cal = Calendar.getInstance();
        int anio = cal.get(Calendar.YEAR);
        int mes = cal.get(Calendar.MONTH);
        int dia = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(ActivityAgregarProducto.this, new DatePickerDialog.OnDateSetListener() {
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

    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Notificación")
                .setPositiveButton("Aceptar", (dialog, id) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}