package com.example.djnig.technoservetm.conexion;

import android.os.StrictMode;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexionClass {
   public static Connection conn=null;
    public static Connection getConexion(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
           // String cadenaConexion = "jdbc:postgresql://192.168.1.100:5432/techno?user=postgres&password=admin";
            // String cadenaConexion = "jdbc:postgresql://192.168.1.100:5432/techno?user=postgres&password=techno";
           String cadenaConexion = "jdbc:postgresql://190.40.155.72:5432/techno?user=postgres&password=techno";
            //   String cadenaConexion = "jdbc:postgresql://"+numip.getText().toString()+"?user=postgres&password="+constrasenia.getText().toString();
            Class.forName("org.postgresql.Driver");
            // Si estás utilizando el emulador de android y tenes el PostgreSQL en tu misma PC no utilizar 127.0.0.1 o localhost como IP, utilizar 10.0.2.2
            conn = DriverManager.getConnection(cadenaConexion);

    } catch (SQLException se) {
        // Toast.makeText(getApplicationContext(),"oops! No se puede conectar. Error: " + se.toString(), Toast.LENGTH_SHORT).show();
        System.out.println("oops! No se puede conectar. Error: " + se.toString());
    } catch (ClassNotFoundException e) {
       // Toast.makeText(getActivity().getApplicationContext(),"oops! No se encuentra la clase. Error: " + e.getMessage(),Toast.LENGTH_SHORT).show();
          System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
    }
    return conn;
  }
  public static void closeConecction() throws SQLException {
      conn.close();
  }
}

