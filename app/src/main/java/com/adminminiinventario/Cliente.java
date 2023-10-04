package com.adminminiinventario;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

public class Cliente {
    @Exclude
    private DocumentReference documentReference;
    private String nombre;
    private boolean esDeudor;
    private int diasRestantes;
    private int descuento;

    public Cliente() {
        // Constructor vacío necesario para Firestore
    }

    public Cliente(String nombre, boolean esDeudor, int diasRestantes, int descuento) {
        this.nombre = nombre;
        this.esDeudor = esDeudor;
        this.diasRestantes = diasRestantes;
        this.descuento = descuento;
    }

    // Getter y setter para DocumentReference
    @Exclude
    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    @Exclude
    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isEsDeudor() {
        return esDeudor;
    }

    public int getDiasRestantes() {
        return diasRestantes;
    }

    public int getDescuento() {
        return descuento;
    }
}
