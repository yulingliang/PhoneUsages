package com.yulingliang.app.android.PhoneUsages;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String TAG = "PhoneUsages.PlaceholderFragment";
        private View mRootView;
        private SharedPreferences mPref;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mRootView = inflater.inflate(R.layout.fragment_main, container, false);
            mPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            // TODO: This is problematic since I am updating shared prefs via the background
            // service which means the activity might be killed already. So fragment will throw
            // exceptions saying it can't attach to activity. A better way to do soe might be do
            // the listener on the Activity class instead.
            mPref.registerOnSharedPreferenceChangeListener(
                    new SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                              String key) {
                            Log.d(TAG, "SharedPref has changed for key: " + key);
                            updateStats();
                        }
                    });

            // If the app is started for the first time, add the current time as last unlock time.
            // So that usage calculation can happen right away.
            long lastUnlockTime = mPref.getLong(Constants.PREF_LAST_UNLOCK_TIME_MS, 0);
            if(lastUnlockTime == 0) {
                Time time = new Time(Time.getCurrentTimezone());
                time.setToNow();
                mPref.edit().putLong(Constants.PREF_LAST_UNLOCK_TIME_MS, time.toMillis(false));
            }

            Button startButton = (Button) mRootView.findViewById(R.id.start_button);
            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent service = new Intent(getActivity(), ScreenStatusListenerService.class);
                    getActivity().startService(service);
                }
            });

            Button stopButton = (Button) mRootView.findViewById(R.id.stop_button);
            stopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent service = new Intent(getActivity(), ScreenStatusListenerService.class);
                    getActivity().stopService(service);
                }
            });

            Button resetButton = (Button) mRootView.findViewById(R.id.reset_stat_button);
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPref.edit()
                            .putInt(Constants.PREF_NUM_SCREEN_ON, 0)
                            .putInt(Constants.PREF_NUM_UNLOCK, 0)
                            .putLong(Constants.PREF_LONGEST_USE_TIME_MS, 0)
                            .commit();
                    updateStats();
                }
            });

            updateStats();

            return mRootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            updateStats();
        }

        private void updateStats() {
            int numScreenOn = mPref.getInt(Constants.PREF_NUM_SCREEN_ON, 0);
            int numUnlock = mPref.getInt(Constants.PREF_NUM_UNLOCK, 0);
            long longestUsageTime = mPref.getLong(Constants.PREF_LONGEST_USE_TIME_MS, 0);

            TextView numScreenOnView = (TextView) mRootView.findViewById(R.id.num_screen_on);
            numScreenOnView.setText(getString(R.string.num_screen_on, numScreenOn));

            TextView numUnlockView = (TextView) mRootView.findViewById(R.id.num_actual_use);
            numUnlockView.setText(getString(R.string.num_unlock, numUnlock));

            TextView longestUsageView = (TextView) mRootView.findViewById(R.id.longest_usage_time);
            longestUsageView.setText(getString(R.string.longest_usage, longestUsageTime / 1000));

            TextView serviceStatus = (TextView) mRootView.findViewById(R.id.service_status);
            if(isListenerServiceRunning()) {
                serviceStatus.setTextColor(Color.GREEN);
                serviceStatus.setText(getString(R.string.service_running));
            } else {
                serviceStatus.setTextColor(Color.RED);
                serviceStatus.setText(getString(R.string.service_stopped));
            }
        }

        private boolean isListenerServiceRunning() {
            ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context
                    .ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager
                    .getRunningServices(Integer.MAX_VALUE)) {
                if (ScreenStatusListenerService.class.getName()
                        .equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        }
    }

}
