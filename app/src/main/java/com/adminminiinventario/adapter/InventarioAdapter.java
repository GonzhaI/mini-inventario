package com.adminminiinventario.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adminminiinventario.R;

public class InventarioAdapter extends RecyclerView.Adapter<InventarioAdapter.InventarioViewHolder> {

    private List<String> productList;
    private Context context;

    public InventarioAdapter(Context context, List<String> productList ) {
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
        String product = productList.get(position);
        holder.nombre_producto.setText(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class InventarioViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout casilla_productos; // Utiliza el ID del RelativeLayout en tu casilla personalizada
        TextView nombre_producto;

        public InventarioViewHolder(View itemView) {
            super(itemView);
            nombre_producto = itemView.findViewById(R.id.nombre_producto); // Asegúrate de usar el ID correcto
        }
    }
}
