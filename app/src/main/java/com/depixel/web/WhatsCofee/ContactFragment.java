package com.depixel.web.WhatsCofee;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.depixel.web.WhatsCofee.ApiWeb.ApiService;
import com.depixel.web.WhatsCofee.EntityClass.ContactsClass;
import com.depixel.web.WhatsCofee.adapter.ContactsListViewAdapter;
import com.depixel.web.WhatsCofee.util.ConnectionSQLiteHelper;
import com.depixel.web.WhatsCofee.util.createTable;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ContactFragment extends Fragment {

    // TODO: Rename and change types of parameters

    EditText editsms;
    Spinner combo1;
    Retrofit cliente;
    ApiService apiService;
    List<ContactsClass> listaMensajes;
    ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper(getContext(), "contact", null, 1);
    private ListView lista;
    private ArrayList list_item = new ArrayList<>(); // for holding list item ids
    int checkedCount=0;

    Button ButtonEnviar;
    EditText editTextSmsWrite;
    List<ContactsClass> listContacts;
   public static ArrayList<Integer> pos = new ArrayList<Integer>();
    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       //
       View v=(View) inflater.inflate(R.layout.fragment_contact, container, false);
        lista = (ListView) v.findViewById(R.id.list_usuario);
        editTextSmsWrite=(EditText)v.findViewById(R.id.editTextSmsWrite);
        ButtonEnviar=(Button)v.findViewById(R.id.ButtonEnviar);
        ButtonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cont=0;
                for (int i=0;i<listContacts.size();i++){
                    if(((ContactsClass) lista.getItemAtPosition(i)).getEstado()){
                        cont=cont+1;
                        Log.d("n",""+((ContactsClass) lista.getItemAtPosition(i)).getCelular());
                       // EnviarMensajePbg(((ContactsClass) lista.getItemAtPosition(i)).getNumero());
                    }
                }
                Toast.makeText(getActivity().getApplicationContext(),"Seleccionados..."+cont,Toast.LENGTH_LONG).show();

            }
        });
        syncronizeContact();
        return v;
    }

    public void syncronizeContact(){
        try {
            //por api
            cliente = new Retrofit.Builder().baseUrl(ApiService.URL).addConverterFactory(GsonConverterFactory.create()).build();
            apiService = cliente.create(ApiService.class);
            apiService.getNumberPbgGenero(MainActivity.dniAsesor).enqueue(new Callback<List<ContactsClass>>() {
                @Override
                public void onResponse(Call<List<ContactsClass>> call, Response<List<ContactsClass>> response) {
                    if (response.isSuccessful()) {
                        //conectarse al android para obtener los usuarios registrados y comprobar si existen o no aun

                        listaMensajes = response.body();

                        ConnectionSQLiteHelper androidconexion = new ConnectionSQLiteHelper(getContext(), "contact", null, 1);
                        SQLiteDatabase db = androidconexion.getWritableDatabase();
                        Cursor cursor;
                        for (ContactsClass smsx : listaMensajes) {
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
                                db.insert(createTable.TABLE_CONTACTS,createTable.campo_name,values);
                            }
                            cursor.close();
                           // Toast.makeText(getActivity().getApplicationContext(),"Enviados: "+numero+"-- .Atte:"+nombre,Toast.LENGTH_SHORT).show();

                        }
                        obtenerLista();
                        //  Toast.makeText(getActivity().getApplicationContext(),"Enviados: "+contarenvio,Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<ContactsClass>> call, Throwable t) {


                }
            });
            //fin de api
        } catch (Exception se) {

            // Toast.makeText(getApplicationContext(),"oops! No se puede conectar. Error: " + se.toString(), Toast.LENGTH_SHORT).show();
            // Toast.makeText(MainActivity.this,"oops! No se puede conectar. Error: " + se.toString(),Toast.LENGTH_SHORT).show();
        }
        obtenerLista();
    }

    public void obtenerLista()
    {


        ConnectionSQLiteHelper androidconexion = new ConnectionSQLiteHelper(getContext(), "contact", null, 1);
        SQLiteDatabase db = androidconexion.getWritableDatabase();
        Cursor cursor;
        String selectQuery = "SELECT  * FROM contact ";
        cursor = db.rawQuery(selectQuery, null);


        listContacts=new ArrayList<>();
        ContactsClass micon;

        while (cursor.moveToNext()) {
            //Id, Company,Name,Price
            micon= new ContactsClass();
            micon.setId(""+cursor.getInt(0));
            micon.setNombre(cursor.getString(1));
            micon.setCelular(cursor.getString(2));
            listContacts.add(micon);
        }
        cursor.close();

        if (listContacts.size() != 0) {

            final ContactsListViewAdapter mLeadsAdapter = new ContactsListViewAdapter(getContext(),0,listContacts);
            lista.setAdapter(mLeadsAdapter);


            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

                   /* Toast.makeText( getContext(), "nombre: "+position+"-"+parent.getChildCount()+"-"+parent.getCount(), Toast.LENGTH_SHORT).show();
                    Log.e("Pos:",""+position+"-"+parent.getChildCount()+"-"+parent.getCount());
                    parent.getChildAt(position).setBackgroundColor(Color.BLUE);*/
                    ContactsClass listItem =(ContactsClass) lista.getItemAtPosition(position);
                    if(listItem.getEstado()){
                        ((ContactsClass) lista.getItemAtPosition(position)).setEstado(false);
                    }else{
                        ((ContactsClass) lista.getItemAtPosition(position)).setEstado(true);
                    }
                    if (!pos.contains(position)) {
                        pos.add(position); //add the position of the clicked row
                        Log.e("Pos:",""+position);
                    }else{
                       for (int i=0;i<pos.size();i++ )
                       {
                           if(pos.get(i).equals(position)){
                               pos.remove(i);
                           }
                       }
                    }
                    mLeadsAdapter.notifyDataSetChanged(); //notify the adapter of the change

                }
            });

        }
    }
}
