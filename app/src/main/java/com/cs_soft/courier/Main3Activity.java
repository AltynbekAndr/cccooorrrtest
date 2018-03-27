package com.cs_soft.courier;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Main3Activity extends AppCompatActivity {
    SharedPreferences sharedPreferences = null;
    String login = null;
    String password = null;
    String codeid = null;
    TextView adres = null;
    TextView adres2 = null;
    TextView clientName = null;
    TextView clientTel = null;
    TextView priceAll = null;
    TextView priceDost = null;
    TextView priceOffice = null;
    TextView statusDost = null;
    TextView comments = null;
    TableLayout tableLayout = null;
    TextView nameTovar = null;
    TextView priceTovar = null;
    AlertDialog.Builder builderCustom_alert_windov = null;
    AlertDialog alert = null;
    AlertDialog alert2 = null;
    TextView txt1 = null;
    TextView txt2 = null;
    TextView txt3 = null;
    TextView txt8 = null;
    TextView txt9 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        builderCustom_alert_windov = new AlertDialog.Builder(Main3Activity.this);
        builderCustom_alert_windov.setTitle("Нет данных")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                });
        alert = builderCustom_alert_windov.create();
        alert.setCancelable(false);
        codeid = getIntent().getStringExtra("codeid");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        sharedPreferences = getSharedPreferences("MYSETTINGS",MODE_PRIVATE);
        login = sharedPreferences.getString("login","");
        password = sharedPreferences.getString("password","");
        adres = (TextView) findViewById(R.id.adres);
        adres2 = (TextView) findViewById(R.id.adres2);
        clientName = (TextView) findViewById(R.id.name);
        clientTel = (TextView) findViewById(R.id.tel);
        priceAll = (TextView) findViewById(R.id.priceall);
        priceDost = (TextView) findViewById(R.id.pricedostavka);
        priceOffice = (TextView) findViewById(R.id.priceoffice);
        statusDost = (TextView) findViewById(R.id.status);
        comments = (TextView) findViewById(R.id.comment);
        tableLayout = (TableLayout) findViewById(R.id.myTable);
        tableLayout.setColumnStretchable(0,true);
        tableLayout.setColumnStretchable(1,true);
        String arrgroup = getIntent().getStringExtra("arr_group");
        txt1 = (TextView) findViewById(R.id.txxxx0);
        txt2 = (TextView) findViewById(R.id.txxxx1);
        txt3 = (TextView) findViewById(R.id.txxxx2);
        txt8 = (TextView) findViewById(R.id.txxxx7);
        txt9 = (TextView) findViewById(R.id.txxxx8);

        /*kurerName
                adres
        adres2
                clientName
        clientTel
                priceAll
        priceDost
                priceOffice
        statusDost
                comments
        typefacen2*/




        txt1.setTextSize(getResources().getDimension(R.dimen.textsize4));

        txt2.setTextSize(getResources().getDimension(R.dimen.textsize4));

        txt3.setTextSize(getResources().getDimension(R.dimen.textsize4));


        txt8.setTextSize(getResources().getDimension(R.dimen.textsize4));

        txt9.setTextSize(getResources().getDimension(R.dimen.textsize4));
        if(arrgroup.equals("Доставка")&&isOnline()){
            new Main3Activity.Zakaz0().execute();
        }else if(isOnline()){
            new Main3Activity.Zakaz().execute();
        }else{
            builderCustom_alert_windov.setMessage("Нет связи с интернетом! Включите мобильную передачу данных или wi-fi");
            alert = builderCustom_alert_windov.create();
            alert.show();
        }
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
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Intent intent = null;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.off:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };
    class  Zakaz extends AsyncTask<Void,Void,Void> {
        private ProgressDialog dialog;
        Document document = null;
        Elements fio = null;
        Elements sum_zakaz =  null;
        Elements name =  null;
        Elements phone =  null;
        Elements address =  null;
        Elements address2 =  null;
        Elements comm =  null;
        Elements tag =  null;
        Elements dostavka =  null;
        Elements office = null;
        Elements name_service = null;
        Elements price = null;
        Elements cost = null;
        TableRow tableRow = null;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Main3Activity.this);
            dialog.setMessage("загрузка...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>zakaz</action><login>"+login+"</login><password>"+password+"</password><code>"+codeid+"</code></xml>")
                    .build();
            Log.e("formBody",formBody.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://kover-samolet.333.kg/xml.php?utf=1")
                    .post(formBody)
                    .build();

            Log.e("request",request.toString());
            try {
                Response response = client.newCall(request).execute();
                Log.e("ZAKAZresponse",response.toString());
                String stttr = response.body().string();
                Log.e("zakazStttr",stttr);
                document = Jsoup.parse(stttr);
                fio = document.select("fio");
                sum_zakaz = document.select("sum_zakaz");
                name = document.select("name");
                phone = document.select("phone");
                address = document.select("address");
                address2 = document.select("doc_address");
                comm = document.select("comments");
                tag = document.select("tag");
                dostavka = document.select("dostavka");
                office = document.select("office");
                name_service = document.select("name_service");
                price = document.select("price");
                cost = document.select("cost");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            for(Element e :address){
                adres.setText(e.text());
            }
            for(Element e :address2){
                adres2.setText(e.text());
            }
            for(Element e :name){
                clientName.setText(e.text());
            }
            for(Element e :phone){
                clientTel.setText(e.text());
            }
            for(Element e :sum_zakaz){
                priceAll.setText(" "+e.text()+" ");
            }
            for(Element e :dostavka){
                priceDost.setText(e.text());
            }
            for(Element e :office){
                priceOffice.setText(e.text());
            }
            for(Element e :comm){
                comments.setText(e.text());
            }
            for(Element e :tag){
                switch (e.text()){
                    case "0":
                        statusDost.setText("Еще не обработан");
                        break;
                    case "1":
                        statusDost.setText("Принят курьером");
                        break;
                    case "2":
                        statusDost.setText("Заказан");
                        break;
                    case "3":
                        statusDost.setText("Доставлено");
                        break;
                    default:
                        statusDost.setText("Статус не известен");
                        break;
                }

            }
            String crntNameTovar[] = null;
            String crntPriceTovar[] = null;
            if(name_service!=null){
                crntNameTovar = new String[name_service.size()];
                crntPriceTovar = new String[price.size()];
            }
            int i = 0;
            for(Element e :name_service){
                crntNameTovar [i] = e.text();
                i++;
            }
            i = 0;
            for(Element e :price){
                crntPriceTovar [i] = e.text();
                i++;
            }
            for(int k=0;k<crntNameTovar.length;k++){
                tableRow = new TableRow(Main3Activity.this);
                nameTovar = new TextView(Main3Activity.this);
                priceTovar = new TextView(Main3Activity.this);
                nameTovar.setTextSize(15);
                nameTovar.setTextColor(color(R.color.color_gray));
                priceTovar.setTextSize(15);
                priceTovar.setTextColor(color(R.color.color_gray));
                priceTovar.setGravity(Gravity.CENTER);
                if(crntNameTovar[k].length()>35){
                    String tovnamestr = crntNameTovar[k].substring(0,33);
                    nameTovar.setText(tovnamestr+"...");
                }else{
                    nameTovar.setText(crntNameTovar[k]);
                }
                String prc = "1";
                try{
                    float pricce = Float.valueOf(crntPriceTovar[k]);
                    float costt = Float.valueOf(cost.get(k).text());
                    pricce = pricce/costt;
                    int endPrc = (int) pricce;
                    prc = endPrc+"";
                }catch (ArithmeticException e){}

                priceTovar.setText(prc+" - "+crntPriceTovar[k]);
                tableRow.addView(nameTovar);
                tableRow.addView(priceTovar);
                tableLayout.addView(tableRow);
            }



            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    class  Zakaz0 extends AsyncTask<Void,Void,Void> {
        private ProgressDialog dialog;
        Document document = null;
        Elements fio = null;
        Elements sum_zakaz =  null;
        Elements name =  null;
        Elements phone =  null;
        Elements address =  null;
        Elements comm =  null;
        Elements tag =  null;
        Elements dostavka =  null;
        Elements office = null;
        Elements name_service = null;
        Elements price = null;
        Elements cost = null;
        TableRow tableRow = null;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Main3Activity.this);
            dialog.setMessage("загрузка...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>zakaz0</action><login>"+login+"</login><password>"+password+"</password><code>"+codeid+"</code></xml>")
                    .build();
            Log.e("formBody",formBody.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://kover-samolet.333.kg/xml.php?utf=1")
                    .post(formBody)
                    .build();

            Log.e("request",request.toString());
            try {
                Response response = client.newCall(request).execute();
                Log.e("ZAKAZresponse",response.toString());
                String stttr = response.body().string();
                Log.e("zakazStttr",stttr);
                document = Jsoup.parse(stttr);
                fio = document.select("fio");
                sum_zakaz = document.select("sum_zakaz");
                name = document.select("name");
                phone = document.select("phone");
                address = document.select("address");
                comm = document.select("comments");
                tag = document.select("tag");
                dostavka = document.select("dostavka");
                office = document.select("office");
                name_service = document.select("name_service");
                price = document.select("price");
                cost = document.select("cost");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            for(Element e :address){
                adres.setText(e.text());
            }
            for(Element e :name){
                clientName.setText(e.text());
            }
            for(Element e :phone){
                clientTel.setText(e.text());
            }
            for(Element e :sum_zakaz){
                priceAll.setText("  "+e.text()+"  ");
            }
            for(Element e :dostavka){
                priceDost.setText(e.text());
            }
            for(Element e :office){
                priceOffice.setText(e.text());
            }
            for(Element e :comm){
                comments.setText(e.text());
            }
            for(Element e :tag){
                switch (e.text()){
                    case "0":
                        statusDost.setText("Еще не обработан");
                        break;
                    case "1":
                        statusDost.setText("Принят курьером");
                        break;
                    case "2":
                        statusDost.setText("Заказан");
                        break;
                    case "3":
                        statusDost.setText("Доставлено");
                        break;
                    default:
                        statusDost.setText("Статус не известен");
                        break;
                }

            }
            String crntNameTovar[] = null;
            String crntPriceTovar[] = null;
            if(name_service!=null){
                crntNameTovar = new String[name_service.size()];
                crntPriceTovar = new String[price.size()];
            }
            int i = 0;
            for(Element e :name_service){
                crntNameTovar [i] = e.text();
                i++;
            }
            i = 0;
            for(Element e :price){
                crntPriceTovar [i] = e.text();
                i++;
            }
            for(int k=0;k<crntNameTovar.length;k++){
                tableRow = new TableRow(Main3Activity.this);
                nameTovar = new TextView(Main3Activity.this);
                priceTovar = new TextView(Main3Activity.this);
                priceTovar = new TextView(Main3Activity.this);
                nameTovar.setTextSize(15);
                priceTovar.setTextSize(15);
                nameTovar.setTextColor(color(R.color.color_gray));
                priceTovar.setTextColor(color(R.color.color_gray));
                priceTovar.setTextSize(15);
                priceTovar.setTextColor(color(R.color.color_gray));
                priceTovar.setGravity(Gravity.CENTER);
                if(crntNameTovar[k].length()>35){
                    String tovnamestr = crntNameTovar[k].substring(0,33);
                    nameTovar.setText(tovnamestr+"...");
                }else{
                    nameTovar.setText(crntNameTovar[k]);
                }

                String prc = "1";
                try{
                    float pricce = Float.valueOf(crntPriceTovar[k]);
                    float costt = Float.valueOf(cost.get(k).text());
                    pricce = pricce/costt;
                    int endPrc = (int) pricce;
                    prc = endPrc+"";
                }catch (ArithmeticException e){}

                priceTovar.setText(prc+" - "+crntPriceTovar[k]);
                tableRow.addView(nameTovar);
                tableRow.addView(priceTovar);
                tableLayout.addView(tableRow);
            }




            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }
    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }
    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }
}
