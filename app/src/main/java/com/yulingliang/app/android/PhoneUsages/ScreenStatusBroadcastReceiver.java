package com.yulingliang.app.android.PhoneUsages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;

public class ScreenStatusBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneUsages.ScreenStatusBroadcastReceiver";
    private Time mTime;
    private String mTimeFormat = "%k:%M:%S";
    public ScreenStatusBroadcastReceiver() {
        mTime = new Time(Time.getCurrentTimezone());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            mTime.setToNow();
            Log.d(TAG, mTime.format(mTimeFormat) + ": Screen is ON");
        } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            mTime.setToNow();
            Log.d(TAG, mTime.format(mTimeFormat) + ": Screen is OFF");
        }
    }
}
