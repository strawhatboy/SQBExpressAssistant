package com.sqbnet.expressassistant.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.sqbnet.expressassistant.Location.GPSLocation;
import com.tencent.android.tpush.logging.TLog;

/**
 * Created by virgil on 7/14/15.
 */
public class LocalService extends Service {

    private static final String TAG = "LOCALSERVICE";
    private IBinder binder = new LocalService.LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        TLog.i("virgil", "LocalService create");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        TLog.i("virgil", "LocalService start");
        GPSLocation.getInst().start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        TLog.i("virgil", "LocalService onDestroy");
        GPSLocation.getInst().stop();
        super.onDestroy();
    }

    public class LocalBinder extends Binder {

        LocalService getService(){
            return LocalService.this;
        }
    }
}
