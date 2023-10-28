package com.adminminiinventario;

import com.google.firebase.Timestamp;

public class Productos {

    private String id_negocio;

    private int cantidad;
    private String cdBarra;
    private String producto;
    private int valor;
    private String imagenUrl;
    private Timestamp fechaVencimiento;

    public Productos() {
    }

    public Productos(String id_negocio, int cantidad, String cdBarra, String producto, int valor, Timestamp fechaVencimiento, String imagenUrl) {
        this.imagenUrl = imagenUrl;
        this.id_negocio = id_negocio;
        this.cantidad = cantidad;
        this.cdBarra = cdBarra;
        this.producto = producto;
        this.valor = valor;
        this.fechaVencimiento = fechaVencimiento;
    }
    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getId_negocio() {
        return id_negocio;
    }

    public void setId_negocio(String id_negocio) {
        this.id_negocio = id_negocio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getCdBarra() {
        return cdBarra;
    }

    public void setCdBarra(String cdBarra) {
        this.cdBarra = cdBarra;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public Timestamp getfechaVencimiento() {
        return fechaVencimiento;
    }

    public void setfechaVencimiento(Timestamp fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}

