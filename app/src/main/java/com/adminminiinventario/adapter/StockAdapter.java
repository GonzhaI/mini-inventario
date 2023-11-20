package com.adminminiinventario.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adminminiinventario.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;


public class StockAdapter extends RecyclerView.Adapter<StockAdapter.InventarioViewHolder> {

    private List<Producto> productList;
    private Context context;

    public StockAdapter(Context context, List<Producto> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public InventarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_casilla_stock, parent, false); // Utiliza el diseño de casilla personalizada
        return new InventarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventarioViewHolder holder, int position) {
        Producto producto = productList.get(position);

        holder.nombre_producto.setText(producto.getNombre_producto());
        // Formatear la cantidad en el formato "Cant: 00"
        String cantidadFormateada = String.format("Cant: %02d", producto.getCantidad());
        holder.Cantidad.setText(cantidadFormateada);

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




    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class InventarioViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout casilla_productos; // Utiliza el ID del RelativeLayout en tu casilla personalizada
        TextView nombre_producto;
        TextView Cantidad;
        TextView fechaVencimiento;
        ImageView imagen_producto;

        TextView codigo_barra;

        public InventarioViewHolder(View itemView) {
            super(itemView);
            nombre_producto = itemView.findViewById(R.id.nombre_producto_stock);
            Cantidad = itemView.findViewById(R.id.cantidad_stock);
            fechaVencimiento = itemView.findViewById(R.id.fecha_vencimiento_stock);
            imagen_producto = itemView.findViewById(R.id.imagen_producto_stock); // Asegúrate de que esta línea esté presente
            codigo_barra = itemView.findViewById(R.id.producto_id_stock);
        }
    }
}
