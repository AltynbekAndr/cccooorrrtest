package com.cs_soft.kurer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
    ListView customListView = null;
    private BoomMenuButton bmb;
    Intent intent = null;
    View header = null;
    BitmapFactory.Options options = null;
    Bitmap bitmap = null;
    ImageView imgHeader = null;
    SwipeRefreshLayout  mSwipeRefreshLayout =null;
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor  editor = null;
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
        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        assert bmb != null;
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
        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            if(i==0){
                HamButton.Builder builder = new HamButton.Builder()
                        .normalImageRes(R.drawable.shop)
                        .normalTextRes( R.string.locationMagaz)
                        .subNormalTextRes( R.string.locationMagaz_little)
                        .listener(new OnBMClickListener() {
                            @Override
                            public void onBoomButtonClick(int index) {
                                Toast.makeText(MainActivity.this, R.string.locationMagaz, Toast.LENGTH_SHORT).show();
                            }
                        });
                bmb.addBuilder(builder);
            }
            if(i==1){
                HamButton.Builder builder = new HamButton.Builder()
                        .normalImageRes(R.drawable.ic_search)
                        .normalTextRes(R.string.search)
                        .subNormalTextRes(R.string.search)
                        .listener(new OnBMClickListener() {
                            @Override
                            public void onBoomButtonClick(int index) {
                                Toast.makeText(MainActivity.this, R.string.search, Toast.LENGTH_SHORT).show();
                            }
                        });
                bmb.addBuilder(builder);
            }
            if(i==2){
                HamButton.Builder builder = new HamButton.Builder()
                        .normalImageRes(R.drawable.models)
                        .normalTextRes(R.string.models)
                        .subNormalTextRes(R.string.models_little)
                        .listener(new OnBMClickListener() {
                            @Override
                            public void onBoomButtonClick(int index) {
                                Toast.makeText(MainActivity.this, R.string.models, Toast.LENGTH_SHORT).show();
                            }
                        });
                bmb.addBuilder(builder);
            }
            if(i==3){
                HamButton.Builder builder = new HamButton.Builder()
                        .normalImageRes(R.drawable.ic_mood_bad_black_24dp)
                        .normalTextRes(R.string.black_list)
                        .subNormalTextRes(R.string.black_list_little)
                        .listener(new OnBMClickListener() {
                            @Override
                            public void onBoomButtonClick(int index) {
                                Toast.makeText(MainActivity.this, R.string.black_list, Toast.LENGTH_SHORT).show();
                            }
                        });
                bmb.addBuilder(builder);
            }
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()){
                    new Zakaz().execute();
                }else if(!isOnline()){
                    Toast.makeText(MainActivity.this,"Нет связи с интернетом!",Toast.LENGTH_LONG).show();
                }

            }
        });
        if (isOnline()) {
            new ZakazStart().execute();
        } else if (!isOnline()) {
            Toast.makeText(MainActivity.this,"Нет связи с интернетом!",Toast.LENGTH_LONG).show();
        }
        options = new BitmapFactory.Options();
        imgHeader = (ImageView) header.findViewById(R.id.profile_image3);
        try {
            InputStream inputStream = new FileInputStream(getFilesDir() + "/my_img_for_icon/img_user.png");
            Bitmap bitm = BitmapFactory.decodeStream(inputStream);
            imgHeader.setImageBitmap(bitm);
        }catch (Exception e){}
        startService(new Intent(MainActivity.this,MyIntentService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(MainActivity.this,MyIntentService.class));
        new ZakazStart().execute();
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
        //inflater.inflate(R.menu.main2, menu);
        //MenuItem searchItem = menu.findItem(R.id.action_search);
        // SearchView searchView = (SearchView) MenuItemCompat
        //    .getActionView(searchItem);
        /*SearchView searchView = (SearchView) searchItem.getActionView();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.history) {
            intent = new Intent(MainActivity.this,HistoryActivity.class);
            intent.putExtra("login",login);
            intent.putExtra("password",password);
            startActivity(intent);
        }/*else if(id == R.id.location){
            intent = new Intent(MainActivity.this,MyLocation.class);
            startActivity(intent);
        }*/
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
        startService(new Intent(MainActivity.this,MyIntentService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(MainActivity.this,MyIntentService.class));
    }

    class  Zakaz extends AsyncTask<Void,Void,Void> {
        Document document = null;
        CustomArrayAdapter customArrayAdapter = null;
        @Override
        protected void onPreExecute() {
            arr_group = new ArrayList();
            arr_name = new ArrayList();
            arr_tel = new ArrayList();
            arr_status = new ArrayList();
            arr_codeid = new ArrayList();
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
                Log.e("stttr",stttr);
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
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            customArrayAdapter = new CustomArrayAdapter(MainActivity.this,arr_group,arr_name,arr_tel,arr_status,"8458.ttf");
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
            }, 3000);
            if(arr_codeid!=null&&arr_codeid.size()!=0&&arr_codeid.get(0).toString().equals("---------")){
                Toast.makeText(MainActivity.this,"Нет данных...",Toast.LENGTH_LONG).show();
            }
        }

    }
    class  ZakazStart extends AsyncTask<Void,Void,Void> {
        Document document = null;
        CustomArrayAdapter customArrayAdapter = null;
        private ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            arr_group = new ArrayList();
            arr_name = new ArrayList();
            arr_tel = new ArrayList();
            arr_status = new ArrayList();
            arr_codeid = new ArrayList();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("загрузка...");
            dialog.setCancelable(false);
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
                String stttr = response.body().string();
                Log.e("stttr",stttr);
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
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            customArrayAdapter = new CustomArrayAdapter(MainActivity.this,arr_group,arr_name,arr_tel,arr_status,"8458.ttf");
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
            }, 3000);
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
}
