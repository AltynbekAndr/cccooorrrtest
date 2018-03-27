package com.cs_soft.courier;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    final int picture=5;
    String login = null;
    String password = null;
    String fio = null;
    List <String> arr_group = null;
    List <String> arr_name = null;
    List <String>arr_tel = null;
    List <String>arr_status = null;
    List <String> arr_codeid = null;
    List <String> statusList = null;
    ListView customListView = null;
    Intent intent = null;
    View header = null;
    BitmapFactory.Options options = null;
    Bitmap bitmap = null;
    ImageView imgHeader = null;
    SwipeRefreshLayout  mSwipeRefreshLayout =null;
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor  editor = null;
    CustomArrayAdapter customArrayAdapter = null;
    AlertDialog.Builder builderCustom_alert_windov = null;
    AlertDialog alert = null;
    Document document = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            finish();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        sharedPreferences = getSharedPreferences("MYSETTINGS",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        // делаем повеселее
        mSwipeRefreshLayout.setColorScheme(R.color.gray, R.color.gray, R.color.gray, R.color.gray);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        header = navigationView.getHeaderView(0);
        SharedPreferences sharedPreferences = getSharedPreferences("MYSETTINGS", MODE_PRIVATE);
        login = sharedPreferences.getString("login2", "");
        password = sharedPreferences.getString("password2", "");
        fio = sharedPreferences.getString("fio", "");
        if(login.equals("")||password.equals("")){
            login = getIntent().getStringExtra("login");
            password = getIntent().getStringExtra("password");
            fio = getIntent().getStringExtra("fio");
        }
        getSupportActionBar().setTitle(fio);
        customListView = (ListView) findViewById(R.id.customListView);


        options = new BitmapFactory.Options();
        imgHeader = (ImageView) header.findViewById(R.id.profile_image3);
        try {
            InputStream inputStream = new FileInputStream(getFilesDir() + "/my_img_for_icon/img_user.png");
            Bitmap bitm = BitmapFactory.decodeStream(inputStream);
            imgHeader.setImageBitmap(bitm);
        }catch (Exception e){}
        myThread.start();
        builderCustom_alert_windov = new AlertDialog.Builder(MainActivity.this);
        builderCustom_alert_windov.setMessage("выйти ?")
                .setPositiveButton("да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences settings = getSharedPreferences("MYSETTINGS", Context.MODE_PRIVATE);
                        settings.edit().clear().commit();
                        System.exit(1);
                    }
                })
                .setNegativeButton("отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                });
        alert  = builderCustom_alert_windov.create();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isOnline()) {
            new ZakazCreate().execute();
        } else if (!isOnline()) {
            Toast.makeText(MainActivity.this,"Нет связи с интернетом!",Toast.LENGTH_LONG).show();
        }
    }

    public void selectImageUser(View view){
        imgHeader = (ImageView) header.findViewById(R.id.profile_image3);
        Intent actionPick  = new Intent(Intent.ACTION_PICK);
        actionPick.setType("image/*");
        startActivityForResult(actionPick, picture);
        actionPick = null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case picture:
                if(resultCode == RESULT_OK){
                    Uri uri = data.getData();
                    try {
                        InputStream input = getContentResolver().openInputStream(uri);
                        options.inSampleSize = 4;
                        bitmap =  BitmapFactory.decodeStream(input, null,options);
                        File imageFile2 = new File(getFilesDir()+"/my_img_for_icon");
                        imageFile2.mkdir();
                        Thread thread = new Thread(new Runnable() {
                            FileOutputStream out = null;
                            @Override
                            public void run() {
                                try {
                                    out = new FileOutputStream(getFilesDir()+"/my_img_for_icon/img_user.png");
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (out != null) {
                                            out.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                        thread.start();
                        imgHeader.setImageBitmap(bitmap);
                        input.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main2, menu);
        /*MenuItem searchItem = menu.findItem(R.id.action_search);
          SearchView searchView = (SearchView) MenuItemCompat
          .getActionView(searchItem);
        SearchView searchView = (SearchView) searchItem.getActionView()
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    // do something with s, the entered string
                    String query = s;
                    Toast.makeText(getApplicationContext(),
                            "String entered is " + s, Toast.LENGTH_SHORT).show();
                    return true;
                }
                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
        }*/
        return super.onCreateOptionsMenu(menu);
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                if(isOnline()){
                    if(!myThread.isAlive()){
                        myThread.start();
                    }else{
                        Toast.makeText(MainActivity.this,"Связь есть!",Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this,"Включите wi-fi или мобильную передачу данных!",Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.history) {
            intent = new Intent(MainActivity.this,HistoryActivity.class);
            intent.putExtra("login",login);
            intent.putExtra("password",password);
            startActivity(intent);
        }else if(id == R.id.exit){
            alert.show();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh() {
        if(isOnline()){
            new Zakaz().execute();
        }else if(!isOnline()){
            Toast.makeText(MainActivity.this,"Нет связи с интернетом!",Toast.LENGTH_LONG).show();
        }
        // начинаем показывать прогресс
        mSwipeRefreshLayout.setRefreshing(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class  Zakaz extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            arr_group = new ArrayList();
            arr_name = new ArrayList();
            arr_tel = new ArrayList();
            arr_status = new ArrayList();
            arr_codeid = new ArrayList();
            statusList = new ArrayList();
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
                String stttr = response.body().string();
                document = Jsoup.parse(stttr);
                Elements elements = document.select("row");
                for(Element e : elements){
                    Element codeid = e.child(0);
                    Element name = e.child(1);
                    Element phone = e.child(2);
                    Element group = e.child(3);
                    Element status = e.child(4);
                    Element tag = e.child(5);

                    Log.e("status",status.text());
                    if(group.text()!=null&&!group.text().equals("")&&group.text().length()!=0){
                        arr_group.add(group.text());
                    }else{
                        arr_group.add("---------");
                    }
                    if(name.text()!=null&&!name.text().equals("")&&name.text().length()!=0){
                        arr_name.add(name.text());
                    }else{
                        arr_name.add("---------");
                    }
                    if(phone.text()!=null&&!phone.text().equals("")&&phone.text().length()!=0){
                        arr_tel.add(phone.text());
                    }else{
                        arr_tel.add("---------");
                    }
                    if(tag.text()!=null&&!tag.text().equals("")&&tag.text().length()!=0){
                        arr_status.add(tag.text());
                    }else{
                        arr_status.add("---------");
                    }
                    if(codeid.text()!=null&&!codeid.text().equals("")&&codeid.text().length()!=0){
                        arr_codeid.add(codeid.text());
                    }
                    else{
                        arr_codeid.add("---------");
                    }
                    if(status.text()!=null&&!status.text().equals("")&&status.text().length()!=0){
                        statusList.add(status.text());
                    }
                    else{
                        statusList.add("---------");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            customArrayAdapter = new CustomArrayAdapter(MainActivity.this,arr_status,arr_name,arr_tel,statusList,"8458.ttf");
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
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 4000);
            if(arr_codeid!=null&&arr_codeid.size()!=0&&arr_codeid.get(0).toString().equals("---------")){
                Toast.makeText(MainActivity.this,"Нет данных...",Toast.LENGTH_LONG).show();
            }
        }

    }
    class  ZakazCreate extends AsyncTask<Void,Void,Void> {
        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("загрузка...");
            dialog.setCancelable(false);
            dialog.show();
            arr_group = new ArrayList();
            arr_name = new ArrayList();
            arr_tel = new ArrayList();
            arr_status = new ArrayList();
            arr_codeid = new ArrayList();
            statusList = new ArrayList();
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
                String stttr = response.body().string();
                document = Jsoup.parse(stttr);
                Elements elements = document.select("row");
                for(Element e : elements){
                    Element codeid = e.child(0);
                    Element name = e.child(1);
                    Element phone = e.child(2);
                    Element group = e.child(3);
                    Element status = e.child(4);
                    Element tag = e.child(5);
                    if(group.text()!=null&&!group.text().equals("")&&group.text().length()!=0){
                        arr_group.add(group.text());
                    }else{
                        arr_group.add("---------");
                    }
                    if(name.text()!=null&&!name.text().equals("")&&name.text().length()!=0){
                        arr_name.add(name.text());
                    }else{
                        arr_name.add("---------");
                    }
                    if(phone.text()!=null&&!phone.text().equals("")&&phone.text().length()!=0){
                        arr_tel.add(phone.text());
                    }else{
                        arr_tel.add("---------");
                    }
                    if(tag.text()!=null&&!tag.text().equals("")&&tag.text().length()!=0){
                        arr_status.add(tag.text());
                    }else{
                        arr_status.add("---------");
                    }
                    if(codeid.text()!=null&&!codeid.text().equals("")&&codeid.text().length()!=0){
                        arr_codeid.add(codeid.text());
                    }
                    else{
                        arr_codeid.add("---------");
                    }
                    if(status.text()!=null&&!status.text().equals("")&&status.text().length()!=0){
                        statusList.add(status.text());
                    }
                    else{
                        statusList.add("---------");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            customArrayAdapter = new CustomArrayAdapter(MainActivity.this,arr_status,arr_name,arr_tel,statusList,"8458.ttf");
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
            if(arr_codeid!=null&&arr_codeid.size()!=0&&arr_codeid.get(0).toString().equals("---------")){
                Toast.makeText(MainActivity.this,"Нет данных...",Toast.LENGTH_LONG).show();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }
    Thread myThread = new Thread(new Runnable() {
        String line = null;
        String line2 = null;
        boolean bool = true;
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            try(
                    Socket socket = new Socket("217.29.18.146",5555);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("cp1251")))
            ){
                out.println(login);
                line2 = in.readLine();
                out.println("<xml><action>login</action><login>"+login+"</login><password>"+password+"</password></xml>");
                Log.e("MainActiSocket","К серверу отправлена запись");
                Log.e("MainActiSocket login",login+"====="+password);
                line = in.readLine();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this,"Соединение установлено",Toast.LENGTH_SHORT).show();
                    }
                });
                while(bool) {
                    TimeUnit.SECONDS.sleep(3);
                    out.println("e");
                    line = in.readLine();
                    if(line.equals("1")){
                        arr_group = new ArrayList<>();
                        arr_name = new ArrayList<>();
                        arr_tel = new ArrayList<>();
                        arr_status = new ArrayList<>();
                        statusList = new ArrayList<>();
                        arr_codeid = new ArrayList<>();
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
                            String stttr = response.body().string();
                            document = Jsoup.parse(stttr);
                            Elements elements = document.select("row");

                            for(Element e : elements){
                                Element codeid = e.child(0);
                                Element name = e.child(1);
                                Element phone = e.child(2);
                                Element group = e.child(3);
                                Element status = e.child(4);
                                Element tag = e.child(5);
                                if(group.text()!=null&&!group.text().equals("")&&group.text().length()!=0){
                                    arr_group.add(group.text());
                                }else{
                                    arr_group.add("---------");
                                }
                                if(name.text()!=null&&!name.text().equals("")&&name.text().length()!=0){
                                    arr_name.add(name.text());
                                    statusStr2 = name.text();
                                }else{
                                    arr_name.add("---------");
                                }
                                if(phone.text()!=null&&!phone.text().equals("")&&phone.text().length()!=0){
                                    arr_tel.add(phone.text());
                                }else{
                                    arr_tel.add("---------");
                                }
                                if(tag.text()!=null&&!tag.text().equals("")&&tag.text().length()!=0){
                                    arr_status.add(tag.text());
                                    statusStr1 = tag.text();
                                }else{
                                    arr_status.add("---------");
                                }
                                if(codeid.text()!=null&&!codeid.text().equals("")&&codeid.text().length()!=0){
                                    arr_codeid.add(codeid.text());
                                }
                                else{
                                    arr_codeid.add("---------");
                                }
                                if(status.text()!=null&&!status.text().equals("")&&status.text().length()!=0){
                                    statusList.add(status.text());
                                }
                                else{
                                    statusList.add("---------");
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                customArrayAdapter = new CustomArrayAdapter(MainActivity.this,arr_status,arr_name,arr_tel,statusList,"8458.ttf");
                                customListView.setAdapter(customArrayAdapter);
                                //s.play(idd, 1, 1, 0, 0, 2);
                                NotificationExtrNews();
                            }
                        });
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MainActivity.this,"Связи нет либо потеряна!",Toast.LENGTH_SHORT).show();
                }
            });
        }
    });
    public static final int NOTIFY_ID = 1;
    String statusStr1 = null;
    String statusStr2 = null;
    public void NotificationExtrNews() {


        Intent notificationIntent = new Intent(this, MainActivity.class);
        /*notificationIntent.putExtra("title",title);
        notificationIntent.putExtra("text",text);
        notificationIntent.putExtra("url",url);
        notificationIntent.putExtra("telNumber",telNumber);
        notificationIntent.putExtra("id",id);*/

        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = this.getResources();

        // до версии Android 8.0 API 26
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

        builder.setContentIntent(contentIntent)
                // обязательные настройки
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle(statusStr1)
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText(statusStr2) // Текст уведомления
                // необязательные настройки
                .setPriority(2)
                .setChannelId("statusStr")
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher_foreground)) // большая
                // картинка
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker(statusStr2)
                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notification_sound)) ; // автоматически закрыть уведомление после нажатия

        /*NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/
        // Альтернативный вариант
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(NOTIFY_ID, builder.build());
    }
}































