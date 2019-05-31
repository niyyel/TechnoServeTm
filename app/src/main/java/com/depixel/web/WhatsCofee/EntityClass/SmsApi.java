package com.depixel.web.WhatsCofee.EntityClass;

public class SmsApi {
    public String mensaje="";
    public String numero="";
    public int estado=0;
    public  int id=0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "SmsApi{" +
                "mensaje='" + mensaje + '\'' +
                ", numero='" + numero + '\'' +
                ", estado=" + estado +
                ", id=" + id +
                '}';
    }
}
