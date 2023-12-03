package com.adminminiinventario;

import com.google.firebase.Timestamp;

public class Distribuidor {
    private String id_negocio;
    private String nombreDistribuidor;
    private Timestamp fechaLlegada;

    public Distribuidor(String id_negocio, String nombreDistribuidor, Timestamp fechaLlegada) {
        this.id_negocio = id_negocio;
        this.nombreDistribuidor = nombreDistribuidor;
        this.fechaLlegada = fechaLlegada;
    }
    public Distribuidor() {
    }

    public String getId_negocio() {
        return id_negocio;
    }

    public void setId_negocio(String id_negocio) {
        this.id_negocio = id_negocio;
    }

    public String getNombreDistribuidor() {
        return nombreDistribuidor;
    }

    public void setNombreDistribuidor(String nombreDistribuidor) {
        this.nombreDistribuidor = nombreDistribuidor;
    }

    public Timestamp getFechaLlegada() {
        return fechaLlegada;
    }

    public void setFechaLlegada(Timestamp fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }
}
