package com.adminminiinventario.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adminminiinventario.R;
import com.adminminiinventario.model.Productos;
import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder> {

    private List<Productos> productosList;

    public ProductosAdapter(List<Productos> productosList) {
        this.productosList = productosList;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Productos producto = productosList.get(position);

        holder.txtProductName.setText(producto.getProducto());
        holder.txtProductDescription.setText("Cantidad: " + producto.getCantidad());
        holder.txtProductBarcode.setText("Código de barras: " + producto.getCdBarra());
        holder.txtProductPrice.setText("Valor: $" + producto.getValor());
    }

    @Override
    public int getItemCount() {
        return productosList.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        public TextView txtProductName;
        public TextView txtProductDescription;
        public TextView txtProductBarcode;
        public TextView txtProductPrice;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductDescription = itemView.findViewById(R.id.txtProductDescription);
            txtProductBarcode = itemView.findViewById(R.id.txtProductCB);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
        }
    }
}

