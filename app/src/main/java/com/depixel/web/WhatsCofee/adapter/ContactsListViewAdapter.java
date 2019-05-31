package com.depixel.web.WhatsCofee.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.depixel.web.WhatsCofee.EntityClass.ContactsClass;
import com.depixel.web.WhatsCofee.R;

import java.util.List;

import static com.depixel.web.WhatsCofee.ContactFragment.pos;

public class ContactsListViewAdapter extends ArrayAdapter<ContactsClass> {

    Context myContext;
    List<ContactsClass> DataList;
    private SparseBooleanArray mSelectedItemsIds;
    // Constructor for get Context and  list

    public  ContactsListViewAdapter(Context context, int resourceId,  List<ContactsClass> lists) {

        super(context,  resourceId, lists);
        //super(context,0, objects);
        mSelectedItemsIds = new  SparseBooleanArray();
        myContext = context;
        DataList = lists;

    }

    // Container Class for item



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Obtener inflater.
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        // Â¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(
                    R.layout.contact_desing,
                    parent,
                    false);
        }
        convertView.setBackgroundColor(Color.WHITE); //or whatever is your default color
        //if the position exists in that list the you must set the background to BLUE
        if(pos!=null){
            if (pos.contains(position)) {
                convertView.setBackgroundColor(Color.BLUE);
            }
        }

        // Referencias UI.7
        TextView txtId;
        TextView txtNumero;
        TextView txtNombre;
        ImageView myImg;

        txtNombre = (TextView)  convertView.findViewById(R.id.txt_name);
        txtNumero = (TextView)  convertView.findViewById(R.id.txt_numero);

        // Setup.
        txtNombre.setText(DataList.get(position).getNombre());
        txtNumero.setText(DataList.get(position).getCelular());


        return convertView;
    }

    @Override
    public void remove(ContactsClass  object) {
        DataList.remove(object);
        notifyDataSetChanged();
    }

    // get List after update or delete

    public  List<ContactsClass> getMyList() {
        return DataList;
    }

    public void  toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }
    // Remove selection after unchecked

    public void  removeSelection() {
        mSelectedItemsIds = new  SparseBooleanArray();
        notifyDataSetChanged();
    }

    // Item checked on selection

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position,  value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();

    }
    // Get number of selected item

    public int  getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public  SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }


}
