package com.yulingliang.app.android.PhoneUsages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
        mTime.setToNow();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        if(Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
            Log.d(TAG, mTime.format(mTimeFormat) + ": Screen is UNLOCK");
            int numUnlock = pref.getInt(Constants.PREF_NUM_UNLOCK, 0);
            pref.edit()
                    .putInt(Constants.PREF_NUM_UNLOCK, ++numUnlock)
                    .putLong(Constants.PREF_LAST_UNLOCK_TIME_MS, mTime.toMillis(false))
                    .commit();
        } else if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Log.d(TAG, mTime.format(mTimeFormat) + ": Screen is ON");
            int numScreenOn = pref.getInt(Constants.PREF_NUM_SCREEN_ON, 0);
            pref.edit().putInt(Constants.PREF_NUM_SCREEN_ON, ++numScreenOn).commit();
        } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            Log.d(TAG, mTime.format(mTimeFormat) + ": Screen is OFF");

            long lastTime = pref.getLong(Constants.PREF_LAST_UNLOCK_TIME_MS, 0);
            if(lastTime == 0) {
                Log.d(TAG, "Last unlock time is 0, it is likely it is unset.");
            } else {
                long usageTime = mTime.toMillis(false) - lastTime;
                long prevUsageTime = pref.getLong(Constants.PREF_LONGEST_USE_TIME_MS, 0);
                if (usageTime > prevUsageTime) {
                    pref.edit().putLong(Constants.PREF_LONGEST_USE_TIME_MS, usageTime).commit();
                }
            }
        }
    }
}
