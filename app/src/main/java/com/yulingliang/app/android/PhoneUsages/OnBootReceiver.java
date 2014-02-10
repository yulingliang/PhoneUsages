package com.yulingliang.app.android.PhoneUsages;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneUsages.OnBootReceiver";
    public OnBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Phone booted, start listener service");
        Intent service = new Intent(context, ScreenStatusListenerService.class);
        context.startService(service);
    }
}
