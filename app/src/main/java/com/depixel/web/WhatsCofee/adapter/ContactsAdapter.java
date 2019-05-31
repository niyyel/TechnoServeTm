package com.depixel.web.WhatsCofee.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

import static com.depixel.web.WhatsCofee.ContactFragment.pos;

public class ContactsAdapter extends SimpleAdapter {
   // ArrayList<Integer> pos = new ArrayList<Integer>();
    public ContactsAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView,   parent);
        v.setBackgroundColor(Color.WHITE); //or whatever is your default color
        //if the position exists in that list the you must set the background to BLUE
        if(pos!=null){
            if (pos.contains(position)) {
                v.setBackgroundColor(Color.BLUE);
            }
        }
        return v;
    }
}
