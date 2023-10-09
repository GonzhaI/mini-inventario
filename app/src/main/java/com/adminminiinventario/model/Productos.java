package com.adminminiinventario.model;

public class Productos {

    private String id_negocio;
    private int cantidad;
    private String cdBarra;
    private String producto;
    private int valor;

    public Productos() {
    }

    public Productos(String id_negocio, int cantidad, String cdBarra, String producto, int valor) {
        this.id_negocio = id_negocio;
        this.cantidad = cantidad;
        this.cdBarra = cdBarra;
        this.producto = producto;
        this.valor = valor;
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
}

