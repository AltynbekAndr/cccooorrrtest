package com.cs_soft.kurer;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;


public class MyIntentService extends IntentService {
    private static final String ACTION_FOO = "com.cs_soft.kurer.action.FOO";
    private static final String ACTION_BAZ = "com.cs_soft.kurer.action.BAZ";
    SharedPreferences sharedPreferences = null;
    SharedPreferences.Editor editor = null;
    private static final String EXTRA_PARAM1 = "com.cs_soft.kurer.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.cs_soft.kurer.extra.PARAM2";
    public static final int NOTIFY_ID= 2;
    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }
    String login =null;
    String password = null;
    Intent intent = null;
    int flug;
    int startId;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        flug = flags;
        this.startId = startId;
        sharedPreferences = getSharedPreferences("MYSETTINGS",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Log.e("","");
        Log.e("","\t\t\t\t\t\t\t\t\t\tservice started");
        Log.e("","");
        login = sharedPreferences.getString("login2","");
        password = sharedPreferences.getString("password2","");
        Log.e("login2",login);
        Log.e("password2",password);
        if(!thread.isAlive()&&!login.equals("")&&!password.equals("")){
            thread.start();
        }
        //return super.onStartCommand(intent,flags,startId);
        return START_REDELIVER_INTENT;
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 2000, restartServicePI);
    }
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onStartCommand(intent,flug,startId);
    }

    Thread thread = new Thread(new Runnable() {
        String line = null;
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
            try(
                    Socket socket = new Socket("217.29.18.146",5552);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")))
            ){
                out.println("<xml><action>login</action><login>"+login+"</login><password>"+password+"</password></xml>");
                Log.e("MyIntentService","К серверу отправлена запись");
                Log.e("MyIntentService login",login+"====="+password);
                String tmpStr = null;
                while((line = in.readLine())!=null) {
                    tmpStr = sharedPreferences.getString("element","");
                    if(!tmpStr.equals(line)){
                        editor.putString("element",line);
                        editor.apply();
                        NotificationExtrNews();
                    }

                }
                Log.e("MyIntentService","Связь потеряно");
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

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
                .setContentTitle("Заказ")
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText("Поступил новый заказ") // Текст уведомления
                // необязательные настройки
                .setPriority(2)
                .setChannelId("namaz")
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher_foreground)) // большая
                // картинка
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker("Поступил новый заказ")
                .setSound(Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.notification_sound)) ; // автоматически закрыть уведомление после нажатия

        /*NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/
        // Альтернативный вариант
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(NOTIFY_ID, builder.build());
    }
}
