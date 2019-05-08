package com.example.djnig.technoservetm.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.djnig.technoservetm.util.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MASTER on 19/08/2017.
 */
public class ConnectionSQLiteHelper extends SQLiteOpenHelper {



    public ConnectionSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    db.execSQL(createTable.CREATE_TABLE_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {
        db.execSQL("drop table if exists contact");
        onCreate(db);
    }
    public void eliminarDataBase()
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("drop table if exists contact");
        database.execSQL(createTable.CREATE_TABLE_CONTACTS);
    }
    public void eliminarInfoTabla()
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("delete from contact");
        database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'contact' ");
    }
    public ArrayList<HashMap<String, String>> getAllUsers(Integer id) {
        ArrayList<HashMap<String, String>> proList;
        proList = new ArrayList<HashMap<String, String>>();
        String selectQuery="";
        if(id==0) {
             selectQuery = "SELECT  id,nombre FROM contact limit 100";
        }else{
             selectQuery = "SELECT  id,nombre,numero FROM contact where id="+id;
        }
        SQLiteDatabase database =  this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
        {
            do {
                //Id, Company,Name,Price
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("campo_id", "" + cursor.getInt(0));
                map.put("campo_nombre", cursor.getString(1));
                map.put("campo_numero", cursor.getString(2));

                proList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return proList;
    }
    public ArrayList<HashMap<String, String>> getAllUsersByDatos(String datos) {
        ArrayList<HashMap<String, String>> proList;
        proList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT  * FROM contact WHERE nombre LIKE '%"+datos+"%' limit 100";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst())
        {
            do {
                //Id, Company,Name,Price
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("campo_id", ""+cursor.getInt(0));
                map.put("campo_nombre", cursor.getString(1));
                proList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return proList;
    }
    public Cursor readContacts(){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT id , nombre FROM contact;", null);
    }
}
