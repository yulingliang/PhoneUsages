package com.yulingliang.app.android.PhoneUsages;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ScreenStatusListenerService extends Service {
    public ScreenStatusListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
