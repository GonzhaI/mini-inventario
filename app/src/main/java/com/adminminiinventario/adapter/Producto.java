package com.adminminiinventario.adapter;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.Timestamp;
import java.util.Date;

public class Producto {
    private String nombre_producto;
    private double valor;
    private Date fechaVencimiento; // Fecha de vencimiento como objeto Date

    public Producto(String nombre_producto, double valor, Date fechaVencimiento) {
        this.nombre_producto = nombre_producto;
        this.valor = valor;
        this.fechaVencimiento = fechaVencimiento;
    }

    // Getters y setters para nombreProducto, precio y fechaVencimiento

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}
