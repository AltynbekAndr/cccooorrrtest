package com.cs_soft.courier;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
            //holder.circleImage = (CircleImageView) v.findViewById(R.id.circleImage);
            holder.imageView = (ImageView) v.findViewById(R.id.imageView);
            holder.coordinatorLayout = (LinearLayout) v.findViewById(R.id.coordinator);
            holder.imageView2 = (ImageView) v.findViewById(R.id.image_view);


            v.setTag(holder);
        }
        holder = (ViewHolder) v.getTag();
        holder.txt1.setText(""+list.get(position));
        if(list.get(position).equals("Доставка")){
            com.cs_soft.courier.TextDrawable drawable = com.cs_soft.courier.TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4,"#FFFFFF") /* thickness in px */
                    .textColor(color(R.color.white))
                    .endConfig()
                    .buildRound("Д", color(R.color.transparent));
            //holder.circleImage.setImageResource(R.drawable.borsok);
            holder.imageView2.setImageDrawable(drawable);
        }else{
            com.cs_soft.courier.TextDrawable drawable = com.cs_soft.courier.TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4,"#FFFFFF") /* thickness in px */
                    .textColor(color(R.color.white))
                    .endConfig()
                    .buildRound("З", color(R.color.transparent));
            //holder.circleImage.setImageResource(R.drawable.archive);
            holder.imageView2.setImageDrawable(drawable);
        }/*else if(list.get(position).equals("Европейская кухня")){
            holder.circleImage.setImageResource(R.drawable.euro);
        }else if(list.get(position).equals("Кондитерские изделия")){
            holder.circleImage.setImageResource(R.drawable.konditer);
        }else if(list.get(position).equals("Корейская кухня")){
            holder.circleImage.setImageResource(R.drawable.koreiskaya);
        }else if(list.get(position).equals("Кухни мира")){
            holder.circleImage.setImageResource(R.drawable.kuhni_mira);
        }else if(list.get(position).equals("Национальная кухня")){
            holder.circleImage.setImageResource(R.drawable.nacional);
        }else if(list.get(position).equals("Пиццерия и Фастфуд")){
            holder.circleImage.setImageResource(R.drawable.fast_food_picca);
        }else if(list.get(position).equals("Фаиза")){
            holder.circleImage.setImageResource(R.drawable.faiza);
        }else if(list.get(position).equals("Японская кухня")){
            holder.circleImage.setImageResource(R.drawable.yaponskaya);
        }*/

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
            holder.coordinatorLayout.setBackgroundColor(color(R.color.color_gray3));
            holder.imageView.setImageResource(R.drawable.ic_chevron_right_black_24dp);
        }else if(list3.get(position).equals("1")){
            holder.coordinatorLayout.setBackgroundColor(color(R.color.c2));
            holder.imageView.setImageResource(R.drawable.ic_check_black_24dp);
        }else if(list3.get(position).equals("2")){
            holder.coordinatorLayout.setBackgroundColor(color(R.color.c3));
            holder.imageView.setImageResource(R.drawable.ic_done_all_black_24dp);
        }else if(list3.get(position).equals("3")){
            holder.coordinatorLayout.setBackgroundColor(color(R.color.c4));
            holder.imageView.setImageResource(R.drawable.ic_playlist_add_check_black_24dp);
        }else{
            holder.coordinatorLayout.setBackgroundColor(color(R.color.color_blue));
            holder.imageView.setImageResource(R.drawable.ic_chevron_right_black_24dp);
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
        //private CircleImageView circleImage;
        private ImageView imageView;
        private ImageView imageView2;
        private LinearLayout coordinatorLayout;

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