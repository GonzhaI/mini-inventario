package com.adminminiinventario.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adminminiinventario.R;
import com.google.firebase.Timestamp;

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

        // Formatea el valor para mostrarlo sin el .0 decimal si es un número entero
        String valorFormateado = formatDouble(producto.getValor());
        holder.valor.setText(valorFormateado);

        if (producto.getFechaVencimiento() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaFormateada = sdf.format(producto.getFechaVencimiento());
            holder.fechaVencimiento.setText(fechaFormateada);
        } else {
            holder.fechaVencimiento.setText("Fecha no disponible");
        }
    }

    private String formatDouble(double value) {
        DecimalFormat df = new DecimalFormat("#.###"); // Define el formato deseado
        String formattedValue = df.format(value);
        return formattedValue;
    }




    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class InventarioViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout casilla_productos; // Utiliza el ID del RelativeLayout en tu casilla personalizada
        TextView nombre_producto;
        TextView valor;
        TextView fechaVencimiento;

        public InventarioViewHolder(View itemView) {
            super(itemView);
            nombre_producto = itemView.findViewById(R.id.nombre_producto);
            valor = itemView.findViewById(R.id.precio);
            fechaVencimiento = itemView.findViewById(R.id.fecha_vencimiento);
        }
    }
}
