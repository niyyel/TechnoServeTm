package com.example.djnig.technoservetm.EntityClass;

public class ListaNumberClass {
    private String celular="";
    private String nombre="";

    public ListaNumberClass(String nombre, String celular) {
        this.nombre = nombre;
        this.celular = celular;
    }


    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
