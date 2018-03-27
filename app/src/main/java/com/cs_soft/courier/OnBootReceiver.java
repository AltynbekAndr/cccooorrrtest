package com.cs_soft.courier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OnBootReceiver extends BroadcastReceiver {

    final String LOG_TAG = "Logs: ";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MyIntentService.class);
        context.startService(serviceIntent);
    }
}