package com.example.djnig.technoservetm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.djnig.technoservetm.ApiWeb.ApiService;
import com.example.djnig.technoservetm.EntityClass.ListaNumberClass;
import com.example.djnig.technoservetm.util.ConnectionSQLiteHelper;
import com.example.djnig.technoservetm.util.createTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    List<ListaNumberClass> listaMensajes;
    ConnectionSQLiteHelper conn = new ConnectionSQLiteHelper(getContext(), "contact", null, 1);
    private ListView lista;
    private ArrayList list_item = new ArrayList<>(); // for holding list item ids
    int checkedCount=0;

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
        syncronizeContact();

        return v;
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
                        listaMensajes = response.body();

                        ConnectionSQLiteHelper androidconexion = new ConnectionSQLiteHelper(getContext(), "contact", null, 1);
                        SQLiteDatabase db = androidconexion.getWritableDatabase();
                        Cursor cursor;
                        for (ListaNumberClass smsx : listaMensajes) {
                            String numero = smsx.getCelular();
                            String nombre = smsx.getNombre();

                            //consultando si el nombre ya esta en android
                            String selectQuery = "SELECT  * FROM contact where numero="+numero+"";
                            cursor = db.rawQuery(selectQuery, null);
                            if (cursor.moveToFirst()) {
                               // Log.i("nooo...", numero+"-"+nombre);
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
                        obtenerLista();
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
            Log.d("falloxxxx", ""+se.toString());
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

        ArrayList<HashMap<String, String>> proList;
        proList = new ArrayList<HashMap<String, String>>();

        while (cursor.moveToNext()) {
            //Id, Company,Name,Price
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("campo_id", "" + cursor.getInt(0));
            map.put("campo_nombre", cursor.getString(1));
            map.put("campo_numero", cursor.getString(2));
            proList.add(map);
            // Log.i("list...", cursor.getInt(0)+"-"+cursor.getString(1)+"-"+cursor.getString(2));
        }
        cursor.close();

        if (proList.size() != 0) {
            ListAdapter adapter = new SimpleAdapter(getContext(), proList,
                    R.layout.contact_desing, new String[]{"campo_nombre", "campo_numero"},
                    new int[]{R.id.txt_name, R.id.txt_numero});
            lista.setAdapter(adapter);
            lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL); // Important
            // acción cuando hacemos click en item para poder modificarlo o eliminarlo
            lista.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    final int checkedCount = lista.getCheckedItemCount();
                    switch (checkedCount) {
                        case 0:
                            mode.setSubtitle(null);
                            break;
                        case 1:
                            mode.setSubtitle("One item selected");
                            break;
                        default:
                            mode.setSubtitle("" + checkedCount + " items selected");
                            break;
                    }
                    //capture total checked items
                    /*checkedCount = lista.getCheckedItemCount();
                    //setting CAB title
                    mode.setTitle(checkedCount + " Selected");
                    //list_item.add(id);
                    if(checked){
                        list_item.add(id);     // Add to list when checked ==  true
                    }else {
                        list_item.remove(id);
                    }*/

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    //Inflate the CAB
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.menu_multi_select, menu);
                    mode.setTitle("Select Items");
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.share:
                            Toast.makeText(getContext(), "Shared " + lista.getCheckedItemCount() +
                                    " items", Toast.LENGTH_SHORT).show();
                            mode.finish();
                            break;
                        default:
                            Toast.makeText(getContext(), "Clicked " + item.getTitle(),
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;
                    // Toast.makeText(context,deleteSize+" Items deleted",Toast.LENGTH_SHORT).show();

                }
                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    // refresh list after deletion
                    // displayDataList();
                }
            });

        }
    }
}
