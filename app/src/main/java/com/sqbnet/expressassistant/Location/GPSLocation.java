package com.sqbnet.expressassistant.Location;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.sqbnet.expressassistant.MyApplication;
import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.SQBResponse;
import com.sqbnet.expressassistant.mode.SQBResponseListener;
import com.sqbnet.expressassistant.utils.UtilHelper;

import java.security.Provider;
import java.util.concurrent.CancellationException;

/**
 * Created by virgil on 7/2/15.
 */
public class GPSLocation {

    private static GPSLocation sInst;
    private LocationManager locationManager;

    public static GPSLocation getInst(){
        if(sInst == null){
            synchronized (GPSLocation.class){
                if(sInst == null){
                    sInst = new GPSLocation();
                }
            }
        }
        return  sInst;
    }

    public interface GPSProviderStatusChanged{
        public void onStatusChanged(boolean isEnabled);
    }

    public GPSProviderStatusChanged GPSProviderStatusChanged;

    public boolean openGEPSettings(){
        locationManager = (LocationManager) MyApplication.getInst().getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
           return true;
        }
        return false;
    }

    public void start(){
        Log.i("virgil", "GPS update start");
        locationManager = (LocationManager) MyApplication.getInst().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1*1000, 0, locationListener);
    }

    public void stop(){
        Log.i("virgil", "GPS update stop");
        locationManager.removeUpdates(locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            Log.i("virgil", "Lat:" + latitude + ", Lng:" + longitude);
            String useId = UtilHelper.getSharedUserId(MyApplication.getInst().currentActivity());
            SQBProvider.getInst().updatePosition(useId, latitude, longitude, new SQBResponseListener(){
                @Override
                public void onResponse(SQBResponse response) {
                    Log.i("virgil", response.getCode());
                    Log.i("virgil", response.getMsg());
                    Log.i("virgil", response.getData().toString());
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.i("virgil", "onStatusChanged:" + s);
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.i("virgil", "onProviderEnabled:" + s);
            if(GPSProviderStatusChanged!=null){
                GPSProviderStatusChanged.onStatusChanged(true);
            }
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.i("virgil", "onProviderDisabled:" + s);
            if(GPSProviderStatusChanged!=null){
                GPSProviderStatusChanged.onStatusChanged(false);
            }
        }
    };
}
