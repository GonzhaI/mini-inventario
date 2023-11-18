package com.adminminiinventario.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.adminminiinventario.ActivityEditar_productos;
import com.adminminiinventario.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class InventarioAdapter extends RecyclerView.Adapter<InventarioAdapter.InventarioViewHolder> {

    private List<Producto> productList;
    private Context context;

    public InventarioAdapter(Context context, List<Producto> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public InventarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.casilla_inventario, parent, false); // Utiliza el diseño de casilla personalizada
        return new InventarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventarioViewHolder holder, int position) {
        Producto producto = productList.get(position);

        holder.nombre_producto.setText(producto.getNombre_producto());
        String valorFormateado = formatDouble(producto.getValor());
        holder.valor.setText(valorFormateado);

        // Cargar y mostrar la imagen utilizando Picasso
        if (producto.getImagenURL() != null && !producto.getImagenURL().isEmpty()) {
            Picasso.get()
                    .load(producto.getImagenURL())
                    .into(holder.imagen_producto);
        } else {
            // Si no hay URL de imagen, puedes mostrar una imagen predeterminada o dejarlo vacío
            holder.imagen_producto.setImageResource(R.drawable.imagen_predeterminada); // Reemplaza con tu propia imagen predeterminada
        }

        if (producto.getFechaVencimiento() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaFormateada = sdf.format(producto.getFechaVencimiento());
            holder.fechaVencimiento.setText(fechaFormateada);
        } else {
            holder.fechaVencimiento.setText("Fecha no disponible");
        }

        String codigoBarras = producto.getCodigo_barra();
        if (codigoBarras != null && !codigoBarras.isEmpty()) {
            holder.codigo_barra.setText(codigoBarras);
        } else {
            holder.codigo_barra.setText("Codigo no disponible");
        }


    }




    private String formatDouble(double value) {
        DecimalFormat df = new DecimalFormat("$#.###"); // Define el formato deseado
        String formattedValue = df.format(value);
        return formattedValue;
    }




    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class InventarioViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;; // Utiliza el ID del RelativeLayout en tu casilla personalizada
        TextView nombre_producto;
        TextView valor;
        TextView fechaVencimiento;
        ImageView imagen_producto;

        TextView codigo_barra;

        public InventarioViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.yay);
            nombre_producto = itemView.findViewById(R.id.nombre_producto);
            valor = itemView.findViewById(R.id.precio);
            fechaVencimiento = itemView.findViewById(R.id.fecha_vencimiento);
            imagen_producto = itemView.findViewById(R.id.imagen_producto); // Asegúrate de que esta línea esté presente
            codigo_barra = itemView.findViewById(R.id.producto_id);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Manejar el clic en el CardView, por ejemplo, iniciar un nuevo Activity
                    Intent intent = new Intent(context, ActivityEditar_productos.class);
                    // Puedes pasar datos adicionales al nuevo Activity si es necesario
                    context.startActivity(intent);
                }
            });
        }

    }
}
