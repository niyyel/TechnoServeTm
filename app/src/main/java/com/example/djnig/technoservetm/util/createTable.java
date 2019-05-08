package com.example.djnig.technoservetm.util;

/**
 * Created by MASTER on 23/09/2017.
 */

public class createTable {

    //Constantes campos tabla usuario

    public static final String TABLE_CONTACTS="contact";
    public static final String campo_id="id";
    public static final String campo_name="nombre";
    public static final String campo_number="numero";

    public static final String CREATE_TABLE_CONTACTS="CREATE TABLE contact("+campo_id+" Integer PRIMARY KEY AUTOINCREMENT, "+campo_name+" TEXT,"+campo_number+" TEXT)";
}
