package com.example.djnig.technoservetm;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.djnig.technoservetm.ApiWeb.ApiService;
import com.example.djnig.technoservetm.EntityClass.HistorialOdk;
import com.example.djnig.technoservetm.conexion.conexionClass;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.v4.content.ContextCompat.checkSelfPermission;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistorialFrag extends Fragment {

    private ListView lista;
    Spinner combo1;
    //para el api
    List<HistorialOdk> listaEntrenadoresx;
    Retrofit cliente;
    ApiService apiService;
    Calendar fecha = Calendar.getInstance();
    int anio = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH) + 1;
    int dia = fecha.get(Calendar.DAY_OF_MONTH);
    String fechaIniciox=""+anio+"-"+mes+"-01";
    String fechaFinx=""+anio+"-"+mes+"-"+dia;

    Dialog detalleHistorialDialog;
    Button btnCerrar;
    public HistorialFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historial, container, false);

        Button btnLanzarActivity = (Button) view.findViewById(R.id.buttonEnviarMensaje);
        btnLanzarActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert= new AlertDialog.Builder(getContext());
                alert.setMessage("¿Esta seguro de enviar?");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EnviarMensajeLista();
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
        //evento del combobox
        Spinner combozona = (Spinner) view.findViewById(R.id.zona);
        combozona.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int idCombo= (int) combo1.getSelectedItemId();
                if(idCombo==0){
                    obtenerLista("1");
                }else if(idCombo==1){
                    obtenerLista("2");
                }else if(idCombo==2){
                    obtenerLista("3");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //retorno
        return view;
      //  return inflater.inflate(R.layout.fragment_historial, container, false);
    }
    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
         combo1 = (Spinner) getView().findViewById(R.id.zona);
        lista = (ListView)getView().findViewById(R.id.list_usuario);
        //
        String[] zonas = new String[]{
                "Tingo Maria",
                "Tocache",
                "Moyobamba"
        };
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity().getApplicationContext(),R.layout.spinner_item,zonas
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        combo1.setAdapter(spinnerArrayAdapter);
        //
        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(), R.array.array_zona, android.R.layout.simple_spinner_item);
        combo1.setAdapter(adapter);*/
    }

   public void EnviarMensajeLista(){
        int idCombo= (int) combo1.getSelectedItemId();
        String idzonax="1";
        if(idCombo==0){
            idzonax="1";
        }else if(idCombo==1){
            idzonax="2";
        }else if(idCombo==2){
            idzonax="3";
        }
                    try {

                        cliente= new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
                        apiService=cliente.create(ApiService.class);
                        apiService.listaHistorial(idzonax,"","",fechaFinx,fechaIniciox,""+0).enqueue(new Callback<List<HistorialOdk>>() {
                            @Override
                            public void onResponse(Call<List<HistorialOdk>> call, Response<List<HistorialOdk>> response) {

                                if (response.isSuccessful()){
                                    //conectarse al android para obtener los usuarios registrados y comprobar si existen o no aun
                                   String datosCompletos="";
                                    String entrenador = "";int total=0;
                                    String celular = "";int contarenvio=0;
                                    SmsManager sms = SmsManager.getDefault();
                                    listaEntrenadoresx=response.body();
                                    for (HistorialOdk entrenadox:listaEntrenadoresx){
                                        //enviando mensajes
                                         entrenador = entrenadox.getNombreentrenador();
                                         celular = entrenadox.getCelular();
                                         total = Integer.parseInt(entrenadox.getTotal());
                                        if(total==0&&celular!=null){
                                            if(celular.length()>8) {
                                                datosCompletos = "Hola " + entrenador + " hasta la fecha no ha enviado registros" +
                                                        " ODK, enviar constantatemente - TNS ";
                                                try {
                                                    sms.sendTextMessage(celular, null, datosCompletos, null, null);
                                                    contarenvio++;
                                                    Toast.makeText(getActivity().getApplicationContext(), "Mensaje Enviado " + contarenvio, Toast.LENGTH_SHORT).show();

                                                } catch (Exception e) {
                                                    Toast.makeText(getActivity().getApplicationContext(), "Mensaje no enviado, datos incorrectos." + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                           }
                                        }
                                        //fin de envio de mensajes
                                    }
                                  //  Toast.makeText(getActivity().getApplicationContext(),"Enviados: "+contarenvio,Toast.LENGTH_LONG).show();

                                }
                            }
                            @Override
                            public void onFailure(Call<List<HistorialOdk>> call, Throwable t) {
                                Log.i("Error",t.getMessage());

                            }
                        });
                    } catch (Exception se) {
                        Toast.makeText(getActivity().getApplicationContext(),"oops! No se puede conectar. Error: " + se.toString(),Toast.LENGTH_SHORT).show();
                    }
    }

    public void obtenerLista(String idzona)
    {


        final ArrayList<HashMap<String, String>>  proList;
        proList = new ArrayList<HashMap<String, String>>();

            cliente= new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
            apiService=cliente.create(ApiService.class);
            apiService.listaHistorial(idzona,"","",fechaFinx,fechaIniciox,""+0).enqueue(new Callback<List<HistorialOdk>>() {
                @Override
                public void onResponse(Call<List<HistorialOdk>> call, Response<List<HistorialOdk>> response) {
                    //Log.i("Cliente","Cliente Android");
                    if (response.isSuccessful()){
                        listaEntrenadoresx=response.body();
                        for (HistorialOdk entrenadox:listaEntrenadoresx){
                           // Log.i("Entrenadox",entrenadox.getNombreentrenador());
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("id",""+entrenadox.getId());
                            map.put("campo_entrenador",entrenadox.getNombreentrenador());
                            map.put("campo_total",entrenadox.getTotal());
                            proList.add(map);
                        }
                        //para llenar al listview
                        if (proList.size() != 0) {
                            ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), proList,
                                    R.layout.formato_fila, new String[]{"id","campo_entrenador", "campo_total"},
                                    new int[]{R.id.txt_id, R.id.txt_entrenador,R.id.txt_total});
                            lista.setAdapter(adapter);
                            // acción cuando hacemos click en item para poder modificarlo o eliminarlo
                            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView parent, View view, int position, long id) {
                                    View viewx  = getActivity().getLayoutInflater().inflate(R.layout.detalle_historial, null);
                                    detalleHistorialDialog = new Dialog(getActivity());
                                    detalleHistorialDialog.setCancelable(false);

                                    detalleHistorialDialog.setContentView(viewx);
                                   // detalleHistorialDialog.setContentView(R.layout.detalle_historial);
                                    detalleHistorialDialog.setTitle("Detalle de envios");
                                    btnCerrar = (Button)viewx.findViewById(R.id.buttonCerrar);
                                    btnCerrar.setEnabled(true);

                                    btnCerrar.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            detalleHistorialDialog.cancel();
                                        }
                                    });

                                    String idx = parent.getItemAtPosition(position).toString();
                                    String entrenador = parent.getItemAtPosition(position).toString();

                                    //entrenador = entrenador.substring(entrenador.indexOf( "campo_entrenador=") + 17, entrenador.length()-1);
                                   // text = text.substring(text.indexOf( "campo_total=") + 12,(entrenador.length()-(entrenador.length()-14)));
                                    idx = idx.substring(idx.indexOf( "id=") + 3,idx.length()-1);

                                    Toast.makeText( getContext().getApplicationContext(), "id: "+idx, Toast.LENGTH_SHORT).show();
                                    //consultando al api de web service para obtener los detalles
                                   /* cliente= new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
                                    apiService=cliente.create(ApiService.class);
                                    apiService.listaHistorial("","","",fechaFinx,fechaIniciox,idx).enqueue(new Callback<List<HistorialOdk>>() {
                                        @Override
                                        public void onResponse(Call<List<HistorialOdk>> call, Response<List<HistorialOdk>> response) {
                                            if (response.isSuccessful()){
                                                listaEntrenadoresx=response.body();
                                                for (HistorialOdk entrenadox:listaEntrenadoresx){
                                                    ((TextView) viewx.findViewById(R.id.textDiaCampo)).setText(entrenadox.getDia_campo());
                                                    ((TextView) viewx.findViewById(R.id.textEntrenador)).setText("ENTRENADOR: "+entrenadox.getNombreentrenador());
                                                }
                                             }
                                        }
                                        @Override
                                        public void onFailure(Call<List<HistorialOdk>> call, Throwable t) {
                                            Log.i("Error",t.getMessage());

                                        }
                                    });
                                    //fin de consulta*/
                                    detalleHistorialDialog.show();

                                }
                            });
                        }
                        //fin de llenado
                    }
                }
                @Override
                public void onFailure(Call<List<HistorialOdk>> call, Throwable t) {
                    Log.i("Error",t.getMessage());

                }
            });

    }

}
