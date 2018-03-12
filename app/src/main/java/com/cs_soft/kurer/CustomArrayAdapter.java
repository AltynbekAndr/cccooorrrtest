package com.cs_soft.kurer;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.design.widget.CoordinatorLayout;
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
    private String typeFace;
    //Typeface tf;
    Context context;
    public CustomArrayAdapter(Context context,List list,List list1,List list2,List list3,String typeFace) {
        this.list = list;
        this.list1 = list1;
        this.list2 = list2;
        this.list3 = list3;
        this.typeFace = typeFace;
        this.context = context;
        //tf = Typeface.createFromAsset(context.getAssets(), "fonts/"+typeFace);
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
            holder.coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinator);


            v.setTag(holder);
        }
        holder = (ViewHolder) v.getTag();
        holder.txt1.setText(""+list.get(position));
        //holder.txt1.setTypeface(tf);
        //holder.txt1.setTextSize(context.getResources().getDimension(R.dimen.textsize2));
        holder.txt2.setText(""+list1.get(position));
        //holder.txt2.setTypeface(tf);
        //holder.txt2.setTextSize(context.getResources().getDimension(R.dimen.textsize2));
        holder.txt3.setText(""+list2.get(position));
        //holder.txt3.setTypeface(tf);
        //holder.txt3.setTextSize(context.getResources().getDimension(R.dimen.textsize2));

        holder.txt4.setText(""+list3.get(position));

        if(list3.get(position).equals("0")){
            holder.coordinatorLayout.setBackgroundColor(color(R.color.colorPrimary));
        }else if(list3.get(position).equals("1")){
            holder.coordinatorLayout.setBackgroundColor(color(R.color.fbutton_color_midnight_blue));
        }else if(list3.get(position).equals("2")){
            holder.coordinatorLayout.setBackgroundColor(color(R.color.fbutton_color_pomegranate));
        }else{
            holder.coordinatorLayout.setBackgroundColor(color(R.color.color_gray2));
        }
        //holder.txt4.setTypeface(tf);
        //holder.txt4.setTextSize(context.getResources().getDimension(R.dimen.textsize2));
        return v;

    }
    private static class ViewHolder {
        private TextView txt1;
        private TextView txt2;
        private TextView txt3;
        private TextView txt4;
        private CoordinatorLayout coordinatorLayout;
    }
    @Override
    public boolean isEnabled(int position)
    {
        return true;
    }
    public int color(@ColorRes int resId) {
        return context.getResources().getColor(resId);
    }
}