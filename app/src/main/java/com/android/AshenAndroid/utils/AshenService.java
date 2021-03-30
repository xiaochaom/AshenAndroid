package com.android.AshenAndroid.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.AshenAndroid.server.impl.AshenHTTPServer;


public class AshenService extends Service {
    public AshenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AshenHTTPServer.getInstance().start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AshenHTTPServer.getInstance().stop();
    }
}
