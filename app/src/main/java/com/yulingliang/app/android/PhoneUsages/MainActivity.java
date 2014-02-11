package com.yulingliang.app.android.PhoneUsages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
        private View mRootView;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            mRootView = inflater.inflate(R.layout.fragment_main, container, false);

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


            updateStats(mRootView);

            return mRootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            updateStats(mRootView);
        }

        private void updateStats(View rootView) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int numScreenOn = pref.getInt(Constants.PREF_NUM_SCREEN_ON, 0);
            int numUnlock = pref.getInt(Constants.PREF_NUM_UNLOCK, 0);

            TextView numScreenOnView = (TextView) rootView.findViewById(R.id.num_screen_on);
            numScreenOnView.setText(getString(R.string.num_screen_on, numScreenOn));

            TextView numUnlockView = (TextView) rootView.findViewById(R.id.num_actual_use);
            numUnlockView.setText(getString(R.string.num_unlock, numUnlock));
        }


    }

}
