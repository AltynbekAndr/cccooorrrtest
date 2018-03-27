package com.cs_soft.courier;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class  HistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
        String login = null;
        String password = null;
        String fio = null;
        List<String> arr_group = null;
        List <String> arr_name = null;
        List <String>arr_tel = null;
        List <String>arr_status = null;
        List <String> arr_codeid = null;
        List <String> arr_cafe = null;
        ListView customListView = null;
        String date1 = null;
        String date2 = null;
        Date date = null;
        TextView nachalo = null;
        TextView konec = null;
        SwipeRefreshLayout  mSwipeRefreshLayout =null;
        DatePicker datePicker1 = null;
        DatePicker datePicker2 = null;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_history);
            if (savedInstanceState != null) {
                finish();
            }
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setColorScheme(R.color.gray, R.color.gray, R.color.gray, R.color.gray);

            date = new Date();

            Date date = new Date(); // your date
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH)+1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            String monthStr = String.valueOf(month);
            String dayStr = String.valueOf(day);
            String dayStr2 = String.valueOf(day);
            String dayStr3 = String.valueOf(day);
            if(monthStr.length()==1){
                monthStr = "0"+monthStr;
            }
            if(dayStr.length()==1){
                dayStr = "0"+dayStr;
            }
            if(dayStr2.length()==1){
                dayStr2 = "0"+dayStr2;
            }
            if(dayStr3.length()==1){
                dayStr3 = "0"+dayStr2;
            }
            date1 = year+"-"+monthStr+"-"+dayStr;
            date2 = year+"-"+monthStr+"-"+dayStr2;

            SharedPreferences sharedPreferences = getSharedPreferences("MYSETTINGS",MODE_PRIVATE);
            login = sharedPreferences.getString("login","");
            password = sharedPreferences.getString("password","");
            fio = sharedPreferences.getString("fio","");
            if(login.equals("")){
                login = getIntent().getStringExtra("login");
                password = getIntent().getStringExtra("password");
                fio = getIntent().getStringExtra("fio");
            }
            customListView = (ListView) findViewById(R.id.customListHistory);

            nachalo = (TextView) findViewById(R.id.nachalo);
            konec = (TextView) findViewById(R.id.konec);
            nachalo.setText(year+"-"+monthStr+"-"+dayStr3);
            konec.setText(date2);

        if(isOnline()){
            Log.e("date1",date1);
            Log.e("date2",date2);
            new Zakaz().execute();
        }else if(!isOnline()){
            Toast.makeText(HistoryActivity.this,"Нет связи с интернетом!",Toast.LENGTH_LONG).show();
        }
        Calendar calendar = Calendar.getInstance();
            DatePickerBuilder builder1 = new DatePickerBuilder(this,new OnSelectDateListener() {
                @Override
                public void onSelect(List<Calendar> calendars) {
                    int y = calendars.get(0).get(Calendar.YEAR);
                    int m = calendars.get(0).get(Calendar.MONTH)+1;
                    int d = calendars.get(0).get(Calendar.DAY_OF_MONTH);
                    String strM = String.valueOf(m);
                    String strD = String.valueOf(d);
                    if(strM.length()==1){
                        strM = "0"+m;
                    }
                    if(strD.length()==1){
                        strD = "0"+d;
                    }
                    nachalo.setText(y+"-"+strM+"-"+strD);
                    date1 = y+"-"+strM+"-"+strD;
                }
            })
                    .selectionColor(R.color.colorPrimary)
                    .selectionLabelColor(R.color.white)
                    .headerColor(R.color.colorPrimary)
                    .date(calendar)
                    .pickerType(CalendarView.ONE_DAY_PICKER);

            datePicker1 = builder1.build();

        DatePickerBuilder builder2 = new DatePickerBuilder(this,new OnSelectDateListener() {
            @Override
            public void onSelect(List<Calendar> calendars) {
                int y = calendars.get(0).get(Calendar.YEAR);
                int m = calendars.get(0).get(Calendar.MONTH)+1;
                int d = calendars.get(0).get(Calendar.DAY_OF_MONTH);
                String strM = String.valueOf(m);
                String strD = String.valueOf(d);
                if(strM.length()==1){
                    strM = "0"+m;
                }
                if(strD.length()==1){
                    strD = "0"+d;
                }
                konec.setText(y+"-"+strM+"-"+strD);
                date2 = y+"-"+strM+"-"+strD;
            }
        })
                .selectionColor(R.color.colorPrimary)
                .selectionLabelColor(R.color.white)
                .headerColor(R.color.colorPrimary)
                .date(calendar)
                .pickerType(CalendarView.ONE_DAY_PICKER);

        datePicker2 = builder2.build();
        }
        @Override
        public void onBackPressed() {
            super.onBackPressed();
            this.finish();
        }
        @Override
        public boolean onSupportNavigateUp() {
            onBackPressed();
            return true;
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

    @Override
    public void onRefresh() {
        if(isOnline()){
            new ZakazRefresh().execute();
        }else if(!isOnline()){
            Toast.makeText(HistoryActivity.this,"Нет связи с интернетом!",Toast.LENGTH_LONG).show();
        }
        // начинаем показывать прогресс
        mSwipeRefreshLayout.setRefreshing(true);

    }
    public void nachalo (View view){
        datePicker1.show();
    }
    public void konec (View view){
        datePicker2.show();
    }
    public void showListZakaz(View view){
        new Zakaz().execute();
    }
    class  Zakaz extends AsyncTask<Void,Void,Void> {
            Document document = null;
            CustomArrayAdapter customArrayAdapter = null;
            boolean boolFlag = true;
            private ProgressDialog dialog;
            @Override
            protected void onPreExecute() {
                arr_group = new ArrayList();
                arr_name = new ArrayList();
                arr_tel = new ArrayList();
                arr_status = new ArrayList();
                arr_codeid = new ArrayList();
                arr_cafe = new ArrayList();
                dialog = new ProgressDialog(HistoryActivity.this);
                dialog.setMessage("загрузка...");
                dialog.setCancelable(false);
                dialog.show();
            }


            //     <start>2018-02-08 00:00:00</start><end>2018-02-08 00:00:00</end><page>1</page>


            @Override
            protected Void doInBackground(Void... voids) {
                new Timestamp(new Date().getTime());
                RequestBody formBody = new FormBody.Builder()
                        .add("xml", "<xml><action>history</action><login>"+login+"</login><password>"+password+"</password><start>"+date1+"</start><end>"+date2+"</end><page>1</page></xml>")
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://kover-samolet.333.kg/xml.php?utf=1")
                        .post(formBody)
                        .build();


                try {
                    Response response = client.newCall(request).execute();
                    String stttr = response.body().string();
                    Log.e("str",stttr);
                    document = Jsoup.parse(stttr);
                    Elements elements = document.select("row");
                    for(Element e : elements){
                        Element codeid = e.child(0);
                        Element name = e.child(1);
                        Element phone = e.child(2);
                        Element group = e.child(3);
                        Element tag = e.child(5);
                        Element cafe = e.child(7);
                        if(group.text()!=null&&!group.text().equals("")&&group.text().length()!=0){
                            arr_group.add(group.text());
                            boolFlag = false;
                        }else{
                            arr_group.add("");
                        }
                        if(codeid.text()!=null&&!codeid.text().equals("")&&codeid.text().length()!=0){
                            arr_codeid.add(codeid.text());
                        }else{
                            arr_codeid.add("");
                        }
                        if(name.text()!=null&&!name.text().equals("")&&name.text().length()!=0){
                            arr_name.add(name.text());
                        }else{
                            arr_name.add("");
                        }
                        if(phone.text()!=null&&!phone.text().equals("")&&phone.text().length()!=0){
                            arr_tel.add(phone.text());
                        }else{
                            arr_tel.add("");
                        }
                        if(tag.text()!=null&&!tag.text().equals("")&&tag.text().length()!=0){
                            arr_status.add(tag.text());
                        }else{
                            arr_status.add("");
                        }
                        if(cafe.text()!=null&&!cafe.text().equals("")&&cafe.text().length()!=0){
                            arr_cafe.add(cafe.text());
                        }
                        else{
                            arr_cafe.add("");
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                customArrayAdapter = new CustomArrayAdapter(HistoryActivity.this,arr_cafe,arr_name,arr_tel,arr_status,"8458.ttf");
                customListView.setVisibility(customListView.VISIBLE);
                customListView.setAdapter(customArrayAdapter);
                customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(HistoryActivity.this,Main3Activity.class);
                        intent.putExtra("arr_group",arr_group.get(i));
                        intent.putExtra("codeid",arr_codeid.get(i));
                        startActivity(intent);
                    }
                });
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if(boolFlag){
                    Toast.makeText(HistoryActivity.this,"Нет данных...",Toast.LENGTH_LONG).show();
                }

            }

        }
    class  ZakazRefresh extends AsyncTask<Void,Void,Void> {
        Document document = null;
        CustomArrayAdapter customArrayAdapter = null;
        boolean boolFlag = true;
        @Override
        protected void onPreExecute() {
            arr_group = new ArrayList();
            arr_name = new ArrayList();
            arr_tel = new ArrayList();
            arr_status = new ArrayList();
            arr_codeid = new ArrayList();
            arr_cafe = new ArrayList();
        }




        @Override
        protected Void doInBackground(Void... voids) {
            new Timestamp(new Date().getTime());
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>history</action><login>"+login+"</login><password>"+password+"</password><start>"+date1+"</start><end>"+date2+"</end><page>1</page></xml>")
                    .build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://kover-samolet.333.kg/xml.php?utf=1")
                    .post(formBody)
                    .build();


            try {
                Response response = client.newCall(request).execute();
                String stttr = response.body().string();
                document = Jsoup.parse(stttr);
                Elements elements = document.select("row");
                for(Element e : elements){
                    Element codeid = e.child(0);
                    Element name = e.child(1);
                    Element phone = e.child(2);
                    Element group = e.child(3);
                    Element tag = e.child(5);
                    Element cafe = e.child(7);
                    if(group.text()!=null&&!group.text().equals("")&&group.text().length()!=0){
                        arr_group.add(group.text());
                        boolFlag = false;
                    }else{
                        arr_group.add("");
                    }
                    if(name.text()!=null&&!name.text().equals("")&&name.text().length()!=0){
                        arr_name.add(name.text());
                    }else{
                        arr_name.add("");
                    }
                    if(phone.text()!=null&&!phone.text().equals("")&&phone.text().length()!=0){
                        arr_tel.add(phone.text());
                    }else{
                        arr_tel.add("");
                    }
                    if(tag.text()!=null&&!tag.text().equals("")&&tag.text().length()!=0){
                        arr_status.add(tag.text());
                    }else{
                        arr_status.add("");
                    }
                    if(codeid.text()!=null&&!codeid.text().equals("")&&codeid.text().length()!=0){
                        arr_codeid.add(codeid.text());
                    }
                    else{
                        arr_codeid.add("");
                    }
                    if(cafe.text()!=null&&!cafe.text().equals("")&&cafe.text().length()!=0){
                        arr_cafe.add(cafe.text());
                    }
                    else{
                        arr_cafe.add("");
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            customArrayAdapter = new CustomArrayAdapter(HistoryActivity.this,arr_cafe,arr_name,arr_tel,arr_status,"8458.ttf");
            customListView.setVisibility(customListView.VISIBLE);
            customListView.setAdapter(customArrayAdapter);
            customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(HistoryActivity.this,Main3Activity.class);
                    intent.putExtra("arr_group",arr_group.get(i));
                    intent.putExtra("codeid",arr_codeid.get(i));
                    startActivity(intent);
                }
            });
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 3000);
            if(boolFlag){
                Toast.makeText(HistoryActivity.this,"Нет данных...",Toast.LENGTH_LONG).show();
            }

        }

    }
}













