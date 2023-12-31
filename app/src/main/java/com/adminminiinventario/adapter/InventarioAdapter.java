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
        View view = LayoutInflater.from(context).inflate(R.layout.casilla_inventario, parent, false);
        return new InventarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventarioViewHolder holder, int position) {
        Producto producto = productList.get(position);

        holder.nombre_producto.setText(producto.getNombre_producto());
        int cantidad = producto.getCantidad();
        String cantidadFormateada = String.format("Unidades: %d", cantidad);
        holder.Cantidad.setText(cantidadFormateada);
        String valorFormateado = formatDouble(producto.getValor());
        holder.valor.setText(valorFormateado);

        if (producto.getImagenURL() != null && !producto.getImagenURL().isEmpty()) {
            Picasso.get().load(producto.getImagenURL()).into(holder.imagen_producto);
        } else {
            holder.imagen_producto.setImageResource(R.drawable.imagen_predeterminada);
        }

        if (producto.getFechaVencimiento() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaFormateada = sdf.format(producto.getFechaVencimiento());
            holder.fechaVencimiento.setText(fechaFormateada);
        } else {
            holder.fechaVencimiento.setText("Fecha no disponible");
        }

        String codigoBarras = producto.getCodigo_barra();
        if (codigoBarras != null || !codigoBarras.isEmpty()) {
            holder.codigo_barra.setText(codigoBarras);
        } else {
            holder.codigo_barra.setText("Codigo no disponible");
        }

        // Modificado para incluir la lógica de clic en el CardView
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Prepara los datos a enviar a la actividad de edición
                Intent intent = new Intent(context, ActivityEditar_productos.class);
                intent.putExtra("productos", producto);
                // Puedes agregar más datos según sea necesario
                context.startActivity(intent);
            }
        });
    }

    private String formatDouble(double value) {
        DecimalFormat df = new DecimalFormat("$#.###");
        return df.format(value);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class InventarioViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView nombre_producto;
        TextView valor;
        TextView fechaVencimiento;
        ImageView imagen_producto;
        TextView codigo_barra;
        TextView Cantidad;

        public InventarioViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.yay);
            nombre_producto = itemView.findViewById(R.id.nombre_producto);
            valor = itemView.findViewById(R.id.precio);
            fechaVencimiento = itemView.findViewById(R.id.fecha_vencimiento);
            imagen_producto = itemView.findViewById(R.id.imagen_producto);
            codigo_barra = itemView.findViewById(R.id.producto_id);
            Cantidad = itemView.findViewById(R.id.cantidad);


        }
    }
}
