package com.example.djnig.technoservetm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.djnig.technoservetm.ApiWeb.ApiService;
import com.example.djnig.technoservetm.EntityClass.UsuarioApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    EditText txtmiusuario;
    EditText txtcontrasenia;
    //
    //para el api
    List<UsuarioApi> listx;
    Retrofit cliente;
    ApiService apiService;
    private ProgressDialog progreso;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        Button btningreso = (Button) findViewById(R.id.buttonIngreso);
         txtmiusuario=(EditText)findViewById(R.id.txtUsuario);
         txtcontrasenia=(EditText)findViewById(R.id.textContrasenia);

        btningreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtmiusuario.getText().toString().trim().equals("")&&txtcontrasenia.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"Ingrese datos",Toast.LENGTH_SHORT).show();
                    return;
                }
                ingresar();
            }
        });


    }
    public void ingresar(){
        progreso = new ProgressDialog(this);
        progreso.setMessage("Iniciando...");
        progreso.show();
        //
        try {

            cliente= new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
            apiService=cliente.create(ApiService.class);
            apiService.validarLogin(txtmiusuario.getText().toString().toLowerCase(),txtcontrasenia.getText().toString()).enqueue(new Callback<List<UsuarioApi>>() {
                @Override
                public void onResponse(Call<List<UsuarioApi>> call, Response<List<UsuarioApi>> response) {
                    int iduser=0;
                    String nombresx="";
                    if (response.isSuccessful()){

                        listx=response.body();
                        for (UsuarioApi datax:listx){
                            iduser = datax.getId();
                            nombresx=datax.getNombres();
                        }
                        //  Toast.makeText(getActivity().getApplicationContext(),"Enviados: "+contarenvio,Toast.LENGTH_LONG).show();
                    }
                    if(iduser!=0){
                        //ingresar
                         progreso.dismiss();
                        Intent miintent= new Intent(getApplicationContext(),MainActivity.class);
                        miintent.putExtra("nombres",nombresx);
                      //  miintent.putExtra("nombres",nombresx);
                        startActivity(miintent);
                        finish();
                    }else{
                        progreso.dismiss();
                        Toast.makeText(getApplicationContext(),"Usuario o contraseña incorrecto",Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<List<UsuarioApi>> call, Throwable t) {
                    Log.i("Error",t.getMessage());

                }
            });
        } catch (Exception se) {
            Toast.makeText(getApplicationContext(),"oops! No se puede conectar. Error: " + se.toString(),Toast.LENGTH_SHORT).show();
        }
    }
}
