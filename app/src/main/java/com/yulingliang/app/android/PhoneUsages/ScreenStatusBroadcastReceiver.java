package com.yulingliang.app.android.PhoneUsages;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
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
                    long longestUsageMinutes = usageTime / (60 * 1000);
                    Log.d(TAG, "new longest usageTime: " + longestUsageMinutes);
                    Notification note = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(context.getString(R.string.app_name))
                            .setContentText(context.getString(R.string.longest_usage, longestUsageMinutes))
                            .build();
                    NotificationManager manager = (NotificationManager) context.getSystemService
                            (Context.NOTIFICATION_SERVICE);
                    manager.notify(Constants.FOREGROUND_NOTIFICATION_ID, note);

                    pref.edit().putLong(Constants.PREF_LONGEST_USE_TIME_MS, usageTime).commit();
                }
            }
        }
    }
}
