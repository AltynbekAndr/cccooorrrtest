package com.cs_soft.kurer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomArrayAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private List list;
    private List list1;
    private List list2;
    private List list3;
    public CustomArrayAdapter(Context context,List list,List list1,List list2,List list3) {
        this.list = list;
        this.list1 = list1;
        this.list2 = list2;
        this.list3 = list3;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v = convertView;
        if ( v == null){
            holder = new ViewHolder();
            v = inflater.inflate(R.layout.custom_list, parent, false);

            holder.txt1 = (TextView) v.findViewById(R.id.txt1);
            holder.txt2 = (TextView) v.findViewById(R.id.txt2);
            holder.txt3 = (TextView) v.findViewById(R.id.txt3);
            holder.txt4 = (TextView) v.findViewById(R.id.txt4);


            v.setTag(holder);
        }
        holder = (ViewHolder) v.getTag();
        holder.txt1.setText("Доставка : "+list.get(position));
        holder.txt2.setText("Имя : "+list1.get(position));
        holder.txt3.setText("Телефон : "+list2.get(position));
        holder.txt4.setText("Статус : "+list3.get(position));

        return v;

    }
    private static class ViewHolder {
        private TextView txt1;
        private TextView txt2;
        private TextView txt3;
        private TextView txt4;
    }
    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }
}