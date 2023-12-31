package com.adminminiinventario.adapter;

import java.util.Date;
import java.io.Serializable;

public class Producto implements Serializable {
    private int cantidad;
    private String id_negocio;
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

    public Producto(int cantidad, String id_negocio,String nombre_producto, double valor, Date fechaVencimiento, String imagenURL, String codigo_barra) {
        this.cantidad = cantidad;
        this.id_negocio = id_negocio;
        this.nombre_producto = nombre_producto;
        this.valor = valor;
        this.fechaVencimiento = fechaVencimiento;
        this.imagenURL = imagenURL;
        this.codigo_barra = codigo_barra;
    }
    public Producto() {
        // Constructor vacío necesario para Firebase
    }

    // Getters y setters para nombreProducto, precio y fechaVencimiento
    public String getId_negocio() {
        return id_negocio;
    }

    public void setId_negocio(String id_negocio) {
        this.id_negocio = id_negocio;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }
    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
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
