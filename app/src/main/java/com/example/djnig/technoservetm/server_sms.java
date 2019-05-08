package com.example.djnig.technoservetm;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.djnig.technoservetm.ApiWeb.ApiService;
import com.example.djnig.technoservetm.EntityClass.ListaNumberClass;
import com.example.djnig.technoservetm.EntityClass.SmsApi;
import com.example.djnig.technoservetm.conexion.conexionClass;
import com.example.djnig.technoservetm.util.ConnectionSQLiteHelper;
import com.example.djnig.technoservetm.util.createTable;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class server_sms extends Fragment {
    EditText editsms;
    Spinner combo1;
    Retrofit cliente;
    ApiService apiService;
    List<ListaNumberClass> listaMensajes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //
        View view= inflater.inflate(R.layout.fragment_server_sms, container, false);
        Button btnLanzarActivity = (Button) view.findViewById(R.id.buttonEnviarSms);
          editsms=(EditText)view.findViewById(R.id.editTextSms);
        btnLanzarActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert= new AlertDialog.Builder(getContext());
                alert.setMessage("¿Esta seguro de enviar?");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EnviarMensajePbg();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity().getApplicationContext(),"No realizó ninguna acción",Toast.LENGTH_SHORT).show();

                    }
                });
                alert.show();
                // Toast.makeText(getActivity().getApplicationContext(),"enviando...",Toast.LENGTH_SHORT).show();
            }
        });

        return view ;
    }
    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
       /* timex ti=new timex();
        ti.execute();*/
    }

    public  void  ejecutar(){
        timex ti=new timex();
        ti.execute();
    }
    public void espera() {
       /* try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
    private class timex extends AsyncTask<Void,Integer,Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            Integer num=  EnviarMensajeLista();
            espera();
            return num;
        }

        @Override
        protected void onPostExecute(Integer cant) {
            ejecutar();
//            Toast.makeText(getActivity().getApplicationContext(),"Cantidad: "+cant,Toast.LENGTH_LONG).show();
        }
    }
    //para consultar los mensajes y enviar
    public void enviarMesajeLocal(){
        try {
            //por api
            cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
            apiService = cliente.create(ApiService.class);
            apiService.getNumberPbgGenero(MainActivity.dniAsesor).enqueue(new Callback<List<ListaNumberClass>>() {
                @Override
                public void onResponse(Call<List<ListaNumberClass>> call, Response<List<ListaNumberClass>> response) {
                    if (response.isSuccessful()) {
                        //conectarse al android para obtener los usuarios registrados y comprobar si existen o no aun
                        SmsManager sms = SmsManager.getDefault();
                        listaMensajes = response.body();
                        String mensajex =editsms.getText().toString();

                        //conversion
                        String PLAIN_ASCII = "AaEeIiOoUu" // grave
                                + "AaEeIiOoUuYy" // acute
                                + "AaEeIiOoUuYy" // circumflex
                                + "AaOoNn" // tilde
                                + "AaEeIiOoUuYy" // umlaut
                                + "Aa" // ring
                                + "Cc" // cedilla
                                + "OoUu" // double acute
                                ;
                        String UNICODE = "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
                                + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
                                + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177"
                                + "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
                                + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF"
                                + "\u00C5\u00E5" + "\u00C7\u00E7" + "\u0150\u0151\u0170\u0171";
                        StringBuilder sb = new StringBuilder();
                        int nx = editsms.getText().toString().trim().length();
                        for (int i = 0; i < nx; i++) {
                            char c = editsms.getText().toString().charAt(i);
                            int pos = UNICODE.indexOf(c);
                            if (pos > -1) {
                                sb.append(PLAIN_ASCII.charAt(pos));
                            } else {
                                sb.append(c);
                            }
                        }
                        mensajex = sb.toString();

                        for (ListaNumberClass smsx : listaMensajes) {
                            String numero = smsx.getCelular();
                            String nombre = smsx.getNombre();
                           // Toast.makeText(getActivity().getApplicationContext(),"Enviados: "+numero+"--"+mensajex+" .Atte:"+nombre,Toast.LENGTH_LONG).show();
                           // sms.sendTextMessage(numero, null, mensajex+" .Atte:"+nombre, null, null);
                            sms.sendTextMessage(numero, null, mensajex, null, null);
                            //guardar los mensajes en la base de datos
                            apiService.saveSms(mensajex,numero,MainActivity.dniAsesor).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (response.isSuccessful()) {
                                    }
                                }
                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    //Log.i("Errorxxxxx", t.getMessage());

                                }
                            });

                        }
                        //  Toast.makeText(getActivity().getApplicationContext(),"Enviados: "+contarenvio,Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<ListaNumberClass>> call, Throwable t) {
                    Log.i("Error", t.getMessage());

                }
            });
            //fin de api
        } catch (Exception se) {
            //Log.d("gjk", ""+se.toString());
            // Toast.makeText(getApplicationContext(),"oops! No se puede conectar. Error: " + se.toString(), Toast.LENGTH_SHORT).show();
            // Toast.makeText(MainActivity.this,"oops! No se puede conectar. Error: " + se.toString(),Toast.LENGTH_SHORT).show();
        }

    }
    public void EnviarMensajePbg()
    {
        ConnectionSQLiteHelper androidconexion = new ConnectionSQLiteHelper(getContext(), "contact", null, 1);
        SQLiteDatabase db = androidconexion.getWritableDatabase();
        Cursor cursor;
        String selectQuery = "SELECT  * FROM contact ";
        cursor = db.rawQuery(selectQuery, null);
        SmsManager sms = SmsManager.getDefault();
        String mensajex ="";
        //conversion
        String PLAIN_ASCII = "AaEeIiOoUu" // grave
                + "AaEeIiOoUuYy" // acute
                + "AaEeIiOoUuYy" // circumflex
                + "AaOoNn" // tilde
                + "AaEeIiOoUuYy" // umlaut
                + "Aa" // ring
                + "Cc" // cedilla
                + "OoUu" // double acute
                ;
        String UNICODE = "\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
                + "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
                + "\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177"
                + "\u00C3\u00E3\u00D5\u00F5\u00D1\u00F1"
                + "\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF"
                + "\u00C5\u00E5" + "\u00C7\u00E7" + "\u0150\u0151\u0170\u0171";
        StringBuilder sb = new StringBuilder();
        int nx = editsms.getText().toString().trim().length();
        for (int i = 0; i < nx; i++) {
            char c = editsms.getText().toString().charAt(i);
            int pos = UNICODE.indexOf(c);
            if (pos > -1) {
                sb.append(PLAIN_ASCII.charAt(pos));
            } else {
                sb.append(c);
            }
        }
        mensajex = sb.toString();

        while (cursor.moveToNext()) {
            sms.sendTextMessage(cursor.getString(2), null, mensajex, null, null);
            //Log.i("list...", cursor.getInt(0)+"-"+cursor.getString(1)+"-"+cursor.getString(2));
            Toast.makeText(getActivity().getApplicationContext(),"Enviados: "+cursor.getString(1)+"--"+mensajex+" .Atte:"+cursor.getString(2),Toast.LENGTH_LONG).show();

            //guardar los mensajes en la base de datos
            cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
            apiService = cliente.create(ApiService.class);
            apiService.saveSms(mensajex,cursor.getString(2),MainActivity.dniAsesor).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    //Log.i("Errorxxxxx", t.getMessage());

                }
            });

        }
        cursor.close();
    }
    public Integer EnviarMensajeLista(){
        String idzonax="1";
        int contador = 0;
        int contAux = 0;
        //full envio
        String numTel = "950645906";

        String datosCompletos = "";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Connection conn = conexionClass.getConexion();
            //  String stsql = " select * from sms where estado=0  ";
            //  String stsql = "  update sms set mensaje='$request->dato' ,estado=0 where id in (49,62,48,6,1) ";
            String stsql = "  update sms set mensaje='"+editsms.getText()+"' ,estado=0 where id in (49,62,48,6)";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(stsql);
           /* String listax="";int n=0;
            SmsManager sms = SmsManager.getDefault();
            while(rs.next()){
                String numero = rs.getString("numero");
                String texto = rs.getString("texto");
                int estado=rs.getInt("estado");
                int idx=rs.getInt("id");

                if(estado==0){
                    //enviar si el estado es igual a 0
                    datosCompletos=texto;
                    try{
                        sms.sendTextMessage(numero, null, datosCompletos, null,null);
                        contAux++;
                        String queryac = " update sms set estado=1 where id="+idx;
                        Statement stx = conn.createStatement();
                        stx.executeQuery(queryac);
                        //  Toast.makeText(getActivity().getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        //   Toast.makeText(MainActivity.this,"Mensaje no enviado, datos incorrectos." + e.getMessage(),Toast.LENGTH_SHORT).show();
                        // e.printStackTrace();
                    }

                    //   Toast.makeText(MainActivity.this, "Mensaje Enviado "+contAux, Toast.LENGTH_SHORT).show();
                }
                // Thread.sleep(5000);

            }*/
            //  Toast.makeText(MainActivity.this,"Cantidad: "+contAux,Toast.LENGTH_LONG).show();
            conn.close();
        } catch (SQLException se) {
            //Log.d("gjk", ""+se.toString());
            // Toast.makeText(getApplicationContext(),"oops! No se puede conectar. Error: " + se.toString(), Toast.LENGTH_SHORT).show();
            // Toast.makeText(MainActivity.this,"oops! No se puede conectar. Error: " + se.toString(),Toast.LENGTH_SHORT).show();
        }
        return contAux;
    }

}
