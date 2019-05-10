package com.example.djnig.technoservetm;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.djnig.technoservetm.ApiWeb.ApiService;
import com.example.djnig.technoservetm.EntityClass.ListaNumberClass;
import com.example.djnig.technoservetm.EntityClass.SmsApi;
import com.example.djnig.technoservetm.conexion.conexionClass;
import com.example.djnig.technoservetm.util.ConnectionSQLiteHelper;
import com.example.djnig.technoservetm.util.createTable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final int REQUEST_CODE_ASK_PERMISSIONS_SEND = 1000;
    private final int REQUEST_CODE_ASK_PERMISSIONS_PHONE = 2000;
    List<ListaNumberClass> listaMensajesClase;
    //para el api
    List<SmsApi> listaMensajes;
    Retrofit cliente;
    ApiService apiService;
    public static String dniAsesor = "";
    private String nombres;
    Dialog numberDialog;
    Button btnRegister;
    EditText numberx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Mensajes", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(1));
        // navigationView.setCheckedItem(R.id.nav_camera);
        checkPermission();
        comprobarRegistroNumero();

        //para obtener los parametros
       /* View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        try{
            Bundle bundle = getIntent().getExtras();
            nombres = bundle.getString("nombres");
          //  nombres = bundle.getParcelable("nombres");para clases de objetos

            if(bundle!=null){
                ((TextView) header.findViewById(R.id.nombrest)).setText(nombres);
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
        //fin de obtener los parametros

        timex ti = new timex();
        ti.execute();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("nigel","nigel2",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAGG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d("Tockennn", token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void comprobarRegistroNumero(){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        String number = sharedPreferences.getString("dni", "");
        if (number == "") {
            // No saved number, ask user to enter it and save it
            dialogNumber();
            Toast.makeText(this, "Ingrese su DNI", Toast.LENGTH_SHORT).show();
            //return false;
        }
        else {
            dniAsesor=number;
            Toast.makeText(this, "TU DNI ES "+number, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            fragmentManager.beginTransaction().replace(R.id.contenedor, new HistorialFrag()).commit();
        } else if (id == R.id.nav_gallery) {
            getSupportActionBar().setTitle("Mensaje grupal");
            fragmentManager.beginTransaction().replace(R.id.contenedor, new server_sms()).commit();
        } else if (id == R.id.nav_slideshow) {
            fragmentManager.beginTransaction().replace(R.id.contenedor, new ContactFragment()).commit();
            getSupportActionBar().setTitle("Contactos");

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void ejecutar() {
        timex ti = new timex();
        ti.execute();
    }

    public void espera() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class timex extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            Integer num = EnviarMensajeLista();
            espera();
            return num;
        }

        @Override
        protected void onPostExecute(Integer cant) {
            ejecutar();
            if (cant > 0) {
                Toast.makeText(MainActivity.this, "Cantidad: " + cant, Toast.LENGTH_LONG).show();
            }
        }
    }

    //para consultar los mensajes y enviar
    public Integer EnviarMensajeLista() {

        int contAux = 0;
        if (dniAsesor != "") {
            //  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            //  StrictMode.setThreadPolicy(policy);
            try {
                //por api
                cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
                apiService = cliente.create(ApiService.class);
                apiService.consultarSms().enqueue(new Callback<List<SmsApi>>() {
                    @Override
                    public void onResponse(Call<List<SmsApi>> call, Response<List<SmsApi>> response) {
                        if (response.isSuccessful()) {
                            //conectarse al android para obtener los usuarios registrados y comprobar si existen o no aun
                            SmsManager sms = SmsManager.getDefault();
                            listaMensajes = response.body();
                            for (SmsApi smsx : listaMensajes) {
                                String numero = smsx.getNumero();
                                String mensajex = smsx.getMensaje();
                                int estado = smsx.getEstado();
                                int idx = smsx.getId();
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
                                int nx = mensajex.trim().length();
                                for (int i = 0; i < nx; i++) {
                                    char c = mensajex.charAt(i);
                                    int pos = UNICODE.indexOf(c);
                                    if (pos > -1) {
                                        sb.append(PLAIN_ASCII.charAt(pos));
                                    } else {
                                        sb.append(c);
                                    }
                                }
                                mensajex = sb.toString();
                                if (estado == 0) {
                                    //enviar si el estado es igual a 0
                                    try {
                                        if (numero != null) {
                                            if (numero.length() > 8 && mensajex.length() > 0) {
                                                sms.sendTextMessage(numero, null, mensajex, null, null);
                                                apiService.updateSms(idx).enqueue(new Callback<String>() {
                                                    @Override
                                                    public void onResponse(Call<String> call, Response<String> response) {
                                                        if (response.isSuccessful()) {
                                                          }
                                                    }
                                                    @Override
                                                    public void onFailure(Call<String> call, Throwable t) {
                                                        Log.i("Errorxxxxx", t.getMessage());

                                                    }
                                                });
                                            }
                                        }
                                    } catch (Exception e) {
                                        // Log.d("Mensaje no enviadoxxxx",e.getMessage());
                                        //   Toast.makeText(MainActivity.this,"Mensaje no enviado" + e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                }
                                //fin de envio de mensajes
                            }
                            //  Toast.makeText(getActivity().getApplicationContext(),"Enviados: "+contarenvio,Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SmsApi>> call, Throwable t) {
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
        return contAux;
    }

    //verificar permisos
    private void checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
          //  Toast.makeText(this,"This version is not Android 6 or later " + Build.VERSION.SDK_INT,Toast.LENGTH_SHORT).show();

        } else {
            int hasWritesendsms = checkSelfPermission(Manifest.permission.SEND_SMS);
            //permiso para enviar
            if (hasWritesendsms != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.SEND_SMS},
                        REQUEST_CODE_ASK_PERMISSIONS_SEND);
                //Toast.makeText(this,"Requiere permisos",Toast.LENGTH_SHORT).show();
            }else if (hasWritesendsms == PackageManager.PERMISSION_GRANTED){
               // Toast.makeText(this, "Los permisos fueron agregdos ", Toast.LENGTH_LONG).show();
                // openCamera();
            }
            //permiso de estado de telefono
           /*  hasWritesendsms = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            if (hasWritesendsms != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE_ASK_PERMISSIONS_PHONE);
            }else if (hasWritesendsms == PackageManager.PERMISSION_GRANTED){
                // Toast.makeText(this, "Los permisos fueron agregdos ", Toast.LENGTH_LONG).show();
                // openCamera();
            }*/
        }
        return;
    }
    //para registrar los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //para enviar
        if(REQUEST_CODE_ASK_PERMISSIONS_SEND == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              //  Toast.makeText(this, "OK Permisos agregados enviar mensaje ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
                // openCamera();
               // EnviarMensajeLista();
            } else {
               // Toast.makeText(this, "Permisos no agregados ! " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        //PARA ESTADO DE TELEFONO
        /*if(REQUEST_CODE_ASK_PERMISSIONS_PHONE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }*/
    }

    /* Stores phone number in the default shared prefs. */
    private void savePhoneNumber(String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dni", value);
        dniAsesor=value;
        editor.commit();
    }
    //PARA EL DIALOGO
    public void dialogNumber(){

        //el dialo
        View viewx  = this.getLayoutInflater().inflate(R.layout.dialog_number, null);

        //dialogo
        numberDialog = new Dialog(this);
        numberDialog.setContentView(viewx);
        numberDialog.setTitle("Ingrese DNI");
        numberDialog.setCancelable(false);
        btnRegister = (Button)viewx.findViewById(R.id.buttonRegister);
        numberx=(EditText) viewx.findViewById(R.id.editTextNumber);
        btnRegister.setEnabled(true);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //guardar el numero
                if(numberx.getText().toString().length()!=8){
                    Toast.makeText( MainActivity.this, "Ingrese DNI válido", Toast.LENGTH_SHORT).show();
                }else{
                    savePhoneNumber(numberx.getText().toString());
                    syncronizeContact();
                    numberDialog.cancel();
                }

            }
        });
        //  Toast.makeText( this.getApplicationContext(), "id: ", Toast.LENGTH_SHORT).show();
        numberDialog.show();
    }
    public void syncronizeContact(){
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
                        listaMensajesClase = response.body();

                        ConnectionSQLiteHelper androidconexion = new ConnectionSQLiteHelper(MainActivity.this, "contact", null, 1);
                        SQLiteDatabase db = androidconexion.getWritableDatabase();
                        Cursor cursor;
                        for (ListaNumberClass smsx : listaMensajesClase) {
                            String numero = smsx.getCelular();
                            String nombre = smsx.getNombre();

                            //consultando si el nombre ya esta en android
                            String selectQuery = "SELECT  * FROM contact where numero="+numero+"";
                            cursor = db.rawQuery(selectQuery, null);
                            if (cursor.moveToFirst()) {
                                // Toast.makeText(getApplicationContext(),"si hay", Toast.LENGTH_SHORT).show();
                            }else{
                                // Toast.makeText(getApplicationContext(),"no hay", Toast.LENGTH_SHORT).show();
                                // numAgregado++;
                                ContentValues values = new ContentValues();
                                values.put(createTable.campo_name, nombre);
                                values.put(createTable.campo_number, numero);
                                // Log.i("regis...", numero+"-"+nombre);
                                db.insert(createTable.TABLE_CONTACTS,createTable.campo_name,values);
                            }
                            cursor.close();
                            // Toast.makeText(getActivity().getApplicationContext(),"Enviados: "+numero+"-- .Atte:"+nombre,Toast.LENGTH_SHORT).show();
                            // Log.i("espere...", numero+"-"+nombre);
                        }
                        //  Toast.makeText(getActivity().getApplicationContext(),"Enviados: "+contarenvio,Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<ListaNumberClass>> call, Throwable t) {
                    // Log.i("Error", t.getMessage());

                }
            });
            //fin de api
        } catch (Exception se) {
          //  Log.d("falloxxxx", ""+se.toString());
            // Toast.makeText(getApplicationContext(),"oops! No se puede conectar. Error: " + se.toString(), Toast.LENGTH_SHORT).show();
            // Toast.makeText(MainActivity.this,"oops! No se puede conectar. Error: " + se.toString(),Toast.LENGTH_SHORT).show();
        }
    }



}
