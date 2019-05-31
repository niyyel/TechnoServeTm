package com.depixel.web.WhatsCofee;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Advance on 15/11/2017.
 */

public class Listar extends AppCompatActivity {
//mis variables
private ListView lista;

    TextView lbl;
    EditText datobuscar;
    ListView lv;
  private TextView idGeneral;
   // final Context context = this;
    ListAdapter adapter;
    ArrayList<HashMap<String, String>> myList;
    public static final int requestcode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_historial);
        lista = (ListView) findViewById(R.id.list_usuario);
        obtenerLista();
       // txtInfo = (TextView) findViewById(R.id.txtInfo);
    }
    public void obtenerLista()
    {

    }
    public ArrayList<HashMap<String, String>> getAllUsers(Integer id)
    {
        ArrayList<HashMap<String, String>> proList;
        proList = new ArrayList<HashMap<String, String>>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
             String cadenaConexion = "jdbc:postgresql://192.168.1.100:5432/techno?user=postgres&password=techno";
         //   String cadenaConexion = "jdbc:postgresql://"+numip.getText().toString()+"?user=postgres&password="+constrasenia.getText().toString();
            Class.forName("org.postgresql.Driver");
            // Si estás utilizando el emulador de android y tenes el PostgreSQL en tu misma PC no utilizar 127.0.0.1 o localhost como IP, utilizar 10.0.2.2
            Connection conn = DriverManager.getConnection(cadenaConexion);
            //Connection conn = DriverManager.getConnection("jdbc:postgresql://"+numip.getText().toString()+":5432/siinco", "postgres", "admin");
            //En el stsql se puede agregar cualquier consulta SQL deseada.
            String stsql = " select z.descripcion as zona,concat(r.nombres,' ',r.ap_paterno) as responsable, " +
                    "        concat(e.nombre,' ',e.appaterno,' ',apmaterno) as entrenador,u.id,u.celular,u.email, " +
                    "        (select count(id) from HistorialFrag where id_usuario=u.id and tipo = 'CAPACITACION GRUPAL' " +
                    "        and fecha_envio between '2018-11-01' and current_date) as cap_grupal, " +
                    "        (select count(id) from HistorialFrag where id_usuario=u.id and tipo = 'VISITA DE REFORZAMIENTO' " +
                    "        and fecha_envio between '2018-11-01' and current_date) as vis_refor, " +
                    "        (select count(id) from HistorialFrag where id_usuario=u.id and tipo like 'VISITA DE REGISTRO DE DATOS' " +
                    "        and fecha_envio between '2018-11-01' and current_date) as vis_reg_dato, " +
                    "        (select count(id) from HistorialFrag where id_usuario=u.id and tipo = 'REGISTRO DE PRODUCTOR' " +
                    "        and fecha_envio between '2018-11-01' and current_date) as reg_pro, " +
                    "        (select count(id) from HistorialFrag where id_usuario=u.id and tipo = 'VISITA DE CAPACITACION' " +
                    "        and fecha_envio between '2018-11-01' and current_date) as vis_cap, " +
                    "        (select count(id) from HistorialFrag where id_usuario=u.id and tipo like '%CARACTER%' " +
                    "        and fecha_envio between '2018-11-01' and current_date) as carac_finca, " +
                    "        (select count(id) from HistorialFrag where id_usuario=u.id and tipo in ('VISITA DE CAPACITACION','CAPACITACION GRUPAL', " +
                    "        'VISITA DE REFORZAMIENTO','VISITA DE REGISTRO DE DATOS','REGISTRO DE PRODUCTOR','CARACTERÍSTICAS DE FINCA') " +
                    "        and fecha_envio between '2018-11-01' and current_date) as total " +
                    "         from responsable r  inner join entrenador e on e.id_responsable=r.id_responsable " +
                    "        inner join users u on u.dni=e.dni inner join zona z on z.id_zona=r.id_zona " +
                    "        and z.id_zona like '%1%' and e.estado='1' " +
                    "        and r.id_responsable like '%%' and e.id_entrenador like '%%' " +
                    "        order by z.id_zona,r.nombres,e.nombre ";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(stsql);
            //conectarse al android para obtener los usuarios registrados y comprobar si existen o no aun

            while(rs.next()){
                String entrenador = rs.getString("entrenador");
                int total = rs.getInt("total");

                //Id, Company,Name,Price
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("entrenador",entrenador);
                map.put("total",""+total);
                proList.add(map);

                //  Toast.makeText(getApplicationContext(),"incri:: "+numinscrip+" / nombre:/ "+propietario, Toast.LENGTH_SHORT).show();
            }
            // Toast.makeText(getApplicationContext(),"Registros Agregados: "+numAgregado, Toast.LENGTH_SHORT).show();
            conn.close();
        } catch (SQLException se) {
            // Toast.makeText(getApplicationContext(),"oops! No se puede conectar. Error: " + se.toString(), Toast.LENGTH_SHORT).show();
            System.out.println("oops! No se puede conectar. Error: " + se.toString());
        } catch (ClassNotFoundException e) {
            Toast.makeText(getApplicationContext(),"oops! No se encuentra la clase. Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            //  System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
        }
        return proList;
    }
    public void onClicEditar(View view) {
      /*  Intent miIntent1=new Intent(getApplicationContext(),ListaUsuario.class);
        startActivity(miIntent1);*/
    }
    public void onClicBuscar(View view) {
      /*  datobuscar=(EditText) findViewById(R.id.editTextBuscar);
        System.out.println("ddd"+datobuscar.getText().toString());
        ArrayList<HashMap<String, String>> myList = conn.getAllUsersByDatos(datobuscar.getText().toString());
        System.out.println("pasee");
        if (myList.size() != 0) {
            System.out.println("ingresee");
            ListAdapter adapter = new SimpleAdapter(ListaUsuario.this, myList,
                    R.layout.formato_fila, new String[]{"campo_id", "campo_nombre"},
                    new int[]{R.id.txt_id, R.id.txt_name});
            lista.setAdapter(adapter);
            // acción cuando hacemos click en item para poder modificarlo o eliminarlo
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {

                    idGeneral = (TextView) findViewById(R.id.txt_id);
                    String text = parent.getItemAtPosition(position).toString();
                    text = text.substring ( text.indexOf ( "campo_id=") + 9, text.length ()-1);
                   // Toast.makeText(getApplicationContext(), "id: "+text, Toast.LENGTH_SHORT).show();
                    Intent modify_intent = new Intent(getApplicationContext(), registro.class);
                    modify_intent.putExtra("idUsuario", Integer.parseInt(text));
                    startActivity(modify_intent);
                }
            });
        }*/
    }

}
