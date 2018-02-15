package com.cs_soft.kurer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LaunchActivity extends AppCompatActivity {
    EditText login = null;
    EditText password = null;
    String loginStr = null;
    String passwordStr = null;
    SharedPreferences sharedPreferences = null;
    AlertDialog.Builder builderCustom_alert_windov = null;
    AlertDialog alert = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        builderCustom_alert_windov = new AlertDialog.Builder(LaunchActivity.this);
        builderCustom_alert_windov.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.dismiss();
            }
        });
        sharedPreferences = getSharedPreferences("MYSETTINGS",MODE_PRIVATE);
        String slog = sharedPreferences.getString("login","");
        String spass = sharedPreferences.getString("password","");
        if(slog!=null&&!slog.equals("")&&slog.length()!=0&&spass!=null&&!spass.equals("")&&spass.length()!=0){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        Button btn_signin = (Button) findViewById(R.id.btn_signin);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login.getText().toString()!=null&&login.getText().toString()!=""&&login.getText().toString().length()!=0){
                    loginStr = login.getText().toString();
                }else{
                    Toast.makeText(LaunchActivity.this,"Введите логин",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.getText().toString()!=null&&password.getText().toString()!=""&&password.getText().toString().length()!=0){
                    passwordStr = password.getText().toString();
                }else{
                    Toast.makeText(LaunchActivity.this,"Введите пароль",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isOnline()){
                   new Login().execute();
                 }
                 else{
                    builderCustom_alert_windov.setTitle("Нет связи с интернетом!")
                            .setMessage("Включите моб.передачу данных или wi-fi");
                    alert = builderCustom_alert_windov.create();
                    alert.show();
                }
            }
        });
    }
    class  Login extends AsyncTask<Void,Void,Void>{
        Document d = null;

        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>login</action><login>"+loginStr+"</login><password>"+passwordStr+"</password></xml>")
                    .build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://kover-samolet.333.kg/xml.php?utf=1")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.e("LOGIN_response",response.toString());
                String stttr = response.body().string();
                Log.e("login",stttr);
                d = Jsoup.parseBodyFragment(stttr);
                Elements elements = d.select("result");
                Elements elements2 = d.select("version");
                Elements elements3 = d.select("fio");
                if(elements.size()!=0||elements.text().equals("-1")){
                    publishProgress();
                    Log.e("(result) :",elements.toString());
                    return null;
                }else if(elements2!=null&&elements2.text().equals("1")){

                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("login",loginStr);
                    editor.putString("password",passwordStr);
                    editor.putString("fio",elements3.eq(0).text());
                    editor.apply();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            builderCustom_alert_windov.setTitle("Не верные данные!")
                    .setMessage("Не верный логин или пароль");
            alert = builderCustom_alert_windov.create();
            alert.show();
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    /*public void login(View view){
        new Login().execute();
    }
    public void zakaz(View view){
        new Zakaz().execute();
    }
    public void history(View view){
        new history().execute();
    }
    class  Login extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>login</action><login>5899</login><password>64143</password></xml>")
                    .build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://kover-samolet.333.kg/xml.php?utf=1")
                    .post(formBody)
                    .build();


            try {
                Response response = client.newCall(request).execute();
                Log.e("LOGINresponseIZ",response.toString());
                String stttr = response.body().string();
                Log.e("login",stttr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    class  Zakaz extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>zakaz</action><login>5899</login><password>64143</password><code>86690</code></xml>")
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
                Log.e("zakaz1000",stttr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    class  history extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>history</action><login>user5648</login><password>325411</password></xml>")
                    .build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://kover-samolet.333.kg/xml.php?utf=1")
                    .post(formBody)
                    .build();


            try {
                Response response = client.newCall(request).execute();
                Log.e("ZHISTORYresponse",response.toString());
                String stttr = response.body().string();
                Log.e("history",stttr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }*/
}
