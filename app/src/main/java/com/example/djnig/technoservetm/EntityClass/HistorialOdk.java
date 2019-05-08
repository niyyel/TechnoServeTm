package com.example.djnig.technoservetm.EntityClass;

public class HistorialOdk {
    private String zona="";
    private String responsable="";
    private String entrenador="";
    private String nombreentrenador="";
    private int id=-1;
    private String celular="";
    private String email="";
    private String dia_campo="";
    private String cap_grupal="";
    private String vis_refor="";
    private String vis_reg_dato="";
    private String reg_pro="";
    private String vis_cap="";
    private String carac_finca="";
    private String total="";

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(String entrenador) {
        this.entrenador = entrenador;
    }

    public String getNombreentrenador() {
        return nombreentrenador;
    }

    public void setNombreentrenador(String nombreentrenador) {
        this.nombreentrenador = nombreentrenador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDia_campo() {
        return dia_campo;
    }

    public void setDia_campo(String dia_campo) {
        this.dia_campo = dia_campo;
    }

    public String getCap_grupal() {
        return cap_grupal;
    }

    public void setCap_grupal(String cap_grupal) {
        this.cap_grupal = cap_grupal;
    }

    public String getVis_refor() {
        return vis_refor;
    }

    public void setVis_refor(String vis_refor) {
        this.vis_refor = vis_refor;
    }

    public String getVis_reg_dato() {
        return vis_reg_dato;
    }

    public void setVis_reg_dato(String vis_reg_dato) {
        this.vis_reg_dato = vis_reg_dato;
    }

    public String getReg_pro() {
        return reg_pro;
    }

    public void setReg_pro(String reg_pro) {
        this.reg_pro = reg_pro;
    }

    public String getVis_cap() {
        return vis_cap;
    }

    public void setVis_cap(String vis_cap) {
        this.vis_cap = vis_cap;
    }

    public String getCarac_finca() {
        return carac_finca;
    }

    public void setCarac_finca(String carac_finca) {
        this.carac_finca = carac_finca;
    }

    @Override
    public String toString() {
        return "HistorialOdk{" +
                "zona='" + zona + '\'' +
                ", responsable='" + responsable + '\'' +
                ", entrenador='" + entrenador + '\'' +
                ", nombreentrenador='" + nombreentrenador + '\'' +
                ", id=" + id +
                ", celular='" + celular + '\'' +
                ", email='" + email + '\'' +
                ", dia_campo='" + dia_campo + '\'' +
                ", cap_grupal='" + cap_grupal + '\'' +
                ", vis_refor='" + vis_refor + '\'' +
                ", vis_reg_dato='" + vis_reg_dato + '\'' +
                ", reg_pro='" + reg_pro + '\'' +
                ", vis_cap='" + vis_cap + '\'' +
                ", carac_finca='" + carac_finca + '\'' +
                ", total='" + total + '\'' +
                '}';
    }
}
