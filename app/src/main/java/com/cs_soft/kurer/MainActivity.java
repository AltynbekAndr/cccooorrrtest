package com.cs_soft.kurer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    String login = null;
    String password = null;
    String fio = null;
    TextView textViewHeader = null;
    List <String> arr_group = null;
    List <String> arr_name = null;
    List <String>arr_tel = null;
    List <String>arr_status = null;
    List <String> arr_codeid = null;
    ListView customListView = null;
    AlertDialog.Builder builderCustom_alert_windov = null;
    AlertDialog alert = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            finish();
        }

        builderCustom_alert_windov = new AlertDialog.Builder(MainActivity.this);
        builderCustom_alert_windov.setTitle("Нет данных!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                });
        alert = builderCustom_alert_windov.create();
        alert.setCancelable(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        SharedPreferences sharedPreferences = getSharedPreferences("MYSETTINGS", MODE_PRIVATE);
        login = sharedPreferences.getString("login", "");
        password = sharedPreferences.getString("password", "");
        fio = sharedPreferences.getString("fio", "");
        getSupportActionBar().setTitle(fio);
        customListView = (ListView) findViewById(R.id.customListView);
        if (isOnline()) {
            new Zakaz().execute();
        } else if (!isOnline()) {
            builderCustom_alert_windov.setMessage("Нет связи с интернетом!");
            alert = builderCustom_alert_windov.create();
            alert.show();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Intent intent = null;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.zakazy:
                    if(isOnline()){
                        new Zakaz().execute();
                    }else if(!isOnline()){
                        builderCustom_alert_windov.setMessage("Нет связи с интернетом!");
                        alert = builderCustom_alert_windov.create();
                        alert.show();
                    }
                    return true;
                case R.id.history:
                    intent = new Intent(MainActivity.this,HistoryActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.location:
                    intent = new Intent(MainActivity.this,MyLocation.class);
                    startActivity(intent);
                    return true;
                case R.id.refresh:
                    if(isOnline()){
                        new Zakaz().execute();
                    }else if(!isOnline()){
                        builderCustom_alert_windov.setMessage("Нет связи с интернетом!");
                        alert = builderCustom_alert_windov.create();
                        alert.show();
                    }
                    return true;
            }
            return false;
        }
    };

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    class  Zakaz extends AsyncTask<Void,Void,Void> {
        Document document = null;
        CustomArrayAdapter customArrayAdapter = null;
        private ProgressDialog dialog;
        boolean boolFlag = true;
        @Override
        protected void onPreExecute() {
            arr_group = new ArrayList();
            arr_name = new ArrayList();
            arr_tel = new ArrayList();
            arr_status = new ArrayList();
            arr_codeid = new ArrayList();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("загрузка...");
            dialog.show();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>login</action><login>"+login+"</login><password>"+password+"</password></xml>")
                    .build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://kover-samolet.333.kg/xml.php?utf=1")
                    .post(formBody)
                    .build();


            try {
                Response response = client.newCall(request).execute();
                Log.e("ZAKAZresponse",response.toString());
                String stttr = response.body().string();
                Log.e("zakaz",stttr);
                document = Jsoup.parse(stttr);
                Elements elements = document.select("row");
                for(Element e : elements){
                    Element codeid = e.child(0);
                    Element name = e.child(1);
                    Element phone = e.child(2);
                    Element group = e.child(3);
                    Element tag = e.child(4);
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
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            customArrayAdapter = new CustomArrayAdapter(MainActivity.this,arr_group,arr_name,arr_tel,arr_status);
            customListView.setAdapter(customArrayAdapter);
            customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                    intent.putExtra("arr_group",arr_group.get(i));
                    intent.putExtra("codeid",arr_codeid.get(i));
                    startActivity(intent);
                }
            });
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(boolFlag){
                builderCustom_alert_windov.setMessage("Пусто");
                alert = builderCustom_alert_windov.create();
                alert.show();
            }
        }

    }
    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }


}
