package com.adminminiinventario;

public class Cliente {
    private String nombre;
    private boolean esDeudor;
    private int diasRestantes;
    private int descuento;


    public Cliente() {
        // Constructor vacio
    }

    // Constructor
    public Cliente(String nombre, boolean esDeudor, int diasRestantes, int descuento) {
        this.nombre = nombre;
        this.esDeudor = esDeudor;
        this.diasRestantes = diasRestantes;
        this.descuento = descuento;
    }

    // Getters y setters (opcional, dependiendo de tus necesidades)
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEsDeudor() {
        return esDeudor;
    }

    public void setEsDeudor(boolean esDeudor) {
        this.esDeudor = esDeudor;
    }

    public int getDiasRestantes() {
        return diasRestantes;
    }

    public void setDiasRestantes(int diasRestantes) {
        this.diasRestantes = diasRestantes;
    }

    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
    }
}
