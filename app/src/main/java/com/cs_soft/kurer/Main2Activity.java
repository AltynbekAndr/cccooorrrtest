package com.cs_soft.kurer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.morphingbutton.MorphingButton;
import com.dd.morphingbutton.impl.LinearProgressButton;

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
public class Main2Activity extends AppCompatActivity {
    SharedPreferences sharedPreferences = null;
    String login = null;
    String password = null;
    String codeid = null;
    TextView kurerName = null;
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
    LinearProgressButton btnMorph = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        builderCustom_alert_windov = new AlertDialog.Builder(Main2Activity.this);
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
        kurerName = (TextView) findViewById(R.id.kurer);
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
        btnMorph = (LinearProgressButton) findViewById(R.id.btnMorph1);

        MorphingButton.Params circle = MorphingButton.Params.create()
                .duration(integer(R.integer.mb_animation))
                .cornerRadius(dimen(R.dimen.mb_height_56))
                .width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_56))
                .color(color(R.color.color_gray))
                .colorPressed(color(R.color.color_gray2))
                .icon(R.drawable.ic_call_black_24dp);
        btnMorph.morph(circle);
        if(arrgroup.equals("Доставка")&&isOnline()){
            new Zakaz0().execute();
        }else if(isOnline()){
            new Zakaz().execute();
        }else{
            builderCustom_alert_windov.setMessage("Нет связи с интернетом! Включите мобильную передачу данных или wi-fi");
            alert = builderCustom_alert_windov.create();
            alert.show();
        }

    }
    public void sendStatus0(){
        if(isOnline()){
            new ActionTag0().execute();
        }else{
            builderCustom_alert_windov.setMessage("Нет связи с интернетом! Включите мобильную передачу данных или wi-fi");
            alert = builderCustom_alert_windov.create();
            alert.show();
        }
    }
    public void sendStatus1(){
        if(isOnline()){
            new ActionTag1().execute();
        }else{
            builderCustom_alert_windov.setMessage("Нет связи с интернетом! Включите мобильную передачу данных или wi-fi");
            alert = builderCustom_alert_windov.create();
            alert.show();
        }
    }
    public void sendStatus2(){
        if(isOnline()){
            new ActionTag2().execute();
        }else{
            builderCustom_alert_windov.setMessage("Нет связи с интернетом! Включите мобильную передачу данных или wi-fi");
            alert = builderCustom_alert_windov.create();
            alert.show();
        }
    }
    public void sendStatus3(){
        if(isOnline()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
            builder.setTitle("Подтверждение")
                    .setMessage("Действительно доствлено?")
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new ActionTag3().execute();
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alert.dismiss();
                            Toast.makeText(Main2Activity.this, "Действие отменено", Toast.LENGTH_SHORT).show();
                        }
                    });
            alert2 = builder.create();
            alert2.setCancelable(false);
            alert2.show();
        }else{
            builderCustom_alert_windov.setMessage("Нет связи с интернетом! Включите мобильную передачу данных или wi-fi");
            alert = builderCustom_alert_windov.create();
            alert.show();
        }
    }

    class ActionTag0 extends AsyncTask<Void,Void,Void>{
        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Main2Activity.this);
            dialog.setMessage("отправка данных...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>tag</action><login>"+login+"</login><password>"+password+"</password><code>"+codeid+"</code><status>0</status></xml>")
                    .build();
            Log.e("formBody",formBody.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://kover-samolet.333.kg/xml.php?utf=1")
                    .post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Log.e("ZAKAZresponse", response.toString());
                String stttr = response.body().string();
                Log.e("zakazStttr", stttr);
                Document document = Jsoup.parse(stttr);
                Elements result = document.select("result");
                for(Element e :result){
                    if(e.text().equals("1")){
                        publishProgress();
                    }
                }
            }catch (Exception e){}

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            statusDost.setText("Еще не обработан");
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    class ActionTag1 extends AsyncTask<Void,Void,Void>{
        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Main2Activity.this);
            dialog.setMessage("отправка данных...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>tag</action><login>"+login+"</login><password>"+password+"</password><code>"+codeid+"</code><status>1</status></xml>")
                    .build();
            Log.e("formBody",formBody.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://kover-samolet.333.kg/xml.php?utf=1")
                    .post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Log.e("ZAKAZresponse", response.toString());
                String stttr = response.body().string();
                Log.e("zakazStttr", stttr);
                Document document = Jsoup.parse(stttr);
                Elements result = document.select("result");
                for(Element e :result){
                    if(e.text().equals("1")){
                        publishProgress();
                    }
                }
            }catch (Exception e){}

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            statusDost.setText("Принят курьером");
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    class ActionTag2 extends AsyncTask<Void,Void,Void>{
        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Main2Activity.this);
            dialog.setMessage("отправка данных...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>tag</action><login>"+login+"</login><password>"+password+"</password><code>"+codeid+"</code><status>2</status></xml>")
                    .build();
            Log.e("formBody",formBody.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://kover-samolet.333.kg/xml.php?utf=1")
                    .post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Log.e("ZAKAZresponse", response.toString());
                String stttr = response.body().string();
                Log.e("zakazStttr", stttr);
                Document document = Jsoup.parse(stttr);
                Elements result = document.select("result");
                for(Element e :result){
                    if(e.text().equals("1")){
                        publishProgress();
                    }
                }
            }catch (Exception e){}

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            statusDost.setText("Заказан");
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }
    class ActionTag3 extends AsyncTask<Void,Void,Void>{
        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Main2Activity.this);
            dialog.setMessage("отправка данных...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestBody formBody = new FormBody.Builder()
                    .add("xml", "<xml><action>tag</action><login>"+login+"</login><password>"+password+"</password><code>"+codeid+"</code><status>3</status></xml>")
                    .build();
            Log.e("formBody",formBody.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://kover-samolet.333.kg/xml.php?utf=1")
                    .post(formBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Log.e("ZAKAZresponse", response.toString());
                String stttr = response.body().string();
                Log.e("zakazStttr", stttr);
                Document document = Jsoup.parse(stttr);
                Elements result = document.select("result");
                for(Element e :result){
                    if(e.text().equals("1")){
                        publishProgress();
                    }
                }
            }catch (Exception e){}

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            statusDost.setText("Доставлен");
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        Intent intent = null;
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.off:
                    sendStatus0();
                    return true;
                case R.id.navigation_home:
                    sendStatus1();
                    return true;
                case R.id.navigation_dashboard:
                    sendStatus2();
                    return true;
                case R.id.navigation_notifications:
                    sendStatus3();
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
        TableRow tableRow = null;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Main2Activity.this);
            dialog.setMessage("загрузка...");
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            for(Element e :fio){
                kurerName.setText(e.text());
            }
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
                priceAll.setText(e.text());
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
                tableRow = new TableRow(Main2Activity.this);
                nameTovar = new TextView(Main2Activity.this);
                priceTovar = new TextView(Main2Activity.this);
                nameTovar.setTextSize(15);
                priceTovar.setTextSize(15);
                priceTovar.setGravity(Gravity.CENTER);
                if(crntNameTovar[k].length()>35){
                    String tovnamestr = crntNameTovar[k].substring(0,33);
                    nameTovar.setText(tovnamestr+"...");
                }else{
                    nameTovar.setText(crntNameTovar[k]);
                }
                priceTovar.setText(crntPriceTovar[k]);
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
        TableRow tableRow = null;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Main2Activity.this);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            for(Element e :fio){
                kurerName.setText(e.text());
            }
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
                priceAll.setText(e.text());
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
                tableRow = new TableRow(Main2Activity.this);
                nameTovar = new TextView(Main2Activity.this);
                priceTovar = new TextView(Main2Activity.this);
                nameTovar.setTextSize(15);
                priceTovar.setTextSize(15);
                priceTovar.setGravity(Gravity.CENTER);
                if(crntNameTovar[k].length()>35){
                    String tovnamestr = crntNameTovar[k].substring(0,33);
                    nameTovar.setText(tovnamestr+"...");
                }else{
                    nameTovar.setText(crntNameTovar[k]);
                }
                priceTovar.setText(crntPriceTovar[k]);
                tableRow.addView(nameTovar);
                tableRow.addView(priceTovar);
                tableLayout.addView(tableRow);
            }




            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public void callPhone(View view){
        String phone = clientTel.getText().toString();
        try{
            int telDig = Integer.valueOf(phone);
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        }catch (Exception box){
            Log.e("BOXING","oshibka privideniya");
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
