package com.adminminiinventario.adapter;

import java.util.Date;

public class Producto {
    private String nombre_producto;
    private double valor;
    private Date fechaVencimiento; // Fecha de vencimiento como objeto Date
    private String imagenURL;
    private String codigo_barra;

    public String getCodigo_barra() {
        return codigo_barra;
    }

    public void setCodigo_barra(String codigo_barra) {
        this.codigo_barra = codigo_barra;
    }

    public Producto(String nombre_producto, double valor, Date fechaVencimiento, String imagenURL, String codigo_barra) {
        this.nombre_producto = nombre_producto;
        this.valor = valor;
        this.fechaVencimiento = fechaVencimiento;
        this.imagenURL = imagenURL;
        this.codigo_barra = codigo_barra;
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

    public String getImagenURL() {
        return imagenURL;
    }
    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }
    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }


}
