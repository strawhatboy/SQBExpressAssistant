package com.sqbnet.expressassistant.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.sqbnet.expressassistant.Location.GPSLocation;

/**
 * Created by virgil on 7/2/15.
 */
public class SQBLocationService extends Service {
    private static final String TAG = "SQBLocationService";
    public static final String ACTION = "com.sqbnet.expressassistant.service.SQBLocationService";

    private IBinder iBinder = new SQBLocationServiceBinder();
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("virgil", "SQBLocationService bind");
        return iBinder;
    }

    @Override
    public void onCreate() {
        Log.i("virgil", "SQBLocationService create");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i("virgil", "SQBLocationService start");
        super.onStart(intent, startId);

        GPSLocation.getInst().getLoation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("virgil", "SQBLocationService start command");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("virgil", "SQBLocationService destroy");
        super.onDestroy();
    }

    public class SQBLocationServiceBinder extends Binder {
        SQBLocationService getService(){
            return SQBLocationService.this;
        }
    }
}
