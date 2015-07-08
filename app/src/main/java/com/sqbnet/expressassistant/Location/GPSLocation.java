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
import java.util.Date;
import java.util.concurrent.CancellationException;

/**
 * Created by virgil on 7/2/15.
 */
public class GPSLocation {

    private static GPSLocation sInst;
    private LocationManager locationManager;
    private boolean isGPSReady;
    private long lastSendTime;

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

    public  GPSLocation(){
        locationManager = (LocationManager) MyApplication.getInst().getSystemService(Context.LOCATION_SERVICE);
        isGPSReady = false;
    }

    public interface GPSProviderStatusChanged{
        public void onStatusChanged(boolean isEnabled);
    }

    public GPSProviderStatusChanged GPSProviderStatusChanged;

    public boolean openGEPSettings(){
        //locationManager = (LocationManager) MyApplication.getInst().getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.i("virgil", "GPS is on");
           return true;
        }
        Log.i("virgil", "GPS is off");
        return false;
    }

    public boolean openNetworkSettings(){
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            Log.i("virgil", "network is on");
            return true;
        }
        Log.i("virgil", "network is off");
        return false;
    }

    public void start(){
        Log.i("virgil", "GPS update start");
        getCurrentLocation();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,   180*1000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 180*1000, 0, networkLocationListener);
        locationManager.addGpsStatusListener(new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int i) {
                switch (i){
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        isGPSReady = true;
                        Log.i("virgil", "GPS first location");
                        break;
                    case GpsStatus.GPS_EVENT_STARTED:
                        Log.i("virgil", "GPS location start");
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        Log.i("virgil", "GPS location stop");
                        break;
                }
            }
        });
    }

    public Location getCurrentLocation(){
        if(openGEPSettings()) {
            Log.i("virgil", "get GPS location");
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                Log.i("virgil", "get network location");
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            return location;
        }
        return null;
    }

    public void stop(){
        Log.i("virgil", "GPS update stop");
        if(locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void sendLocationToServer(Location location){
        try {
            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());
            Log.i("virgil", "Lat:" + latitude + ", Lng:" + longitude);
            String useId = UtilHelper.getSharedUserId();
            if(useId == null)
                return;
            SQBProvider.getInst().updatePosition(useId, latitude, longitude, new SQBResponseListener() {
                @Override
                public void onResponse(SQBResponse response) {
                    if(response == null)
                        return;
                    Log.i("virgil", response.getCode());
                    Log.i("virgil", response.getMsg());
                    Log.i("virgil", response.getData().toString());
                }
            });
        }catch (Exception e){
            Log.e("GPSLocation", "virgil", e);
            e.printStackTrace();
        }
    }

    private final  LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("virgil", "network onLocationChanged");
            Long now = System.currentTimeMillis();
            Long minutes = (now - lastSendTime)/6000;
            if(!isGPSReady || minutes > 5) {
                Log.i("virgil", "network onLocationChanged send location");
                sendLocationToServer(location);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("virgil", "GPS onLocationChanged send location");
            isGPSReady = true;
            lastSendTime = System.currentTimeMillis();
            sendLocationToServer(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            Log.i("virgil", "GPS onStatusChanged:" + s);
        }

        @Override
        public void onProviderEnabled(String s) {
            Log.i("virgil", "GPS onProviderEnabled:" + s);
            if(GPSProviderStatusChanged!=null){
                GPSProviderStatusChanged.onStatusChanged(true);
            }
        }

        @Override
        public void onProviderDisabled(String s) {
            Log.i("virgil", "GPS onProviderDisabled:" + s);
            if(GPSProviderStatusChanged!=null){
                GPSProviderStatusChanged.onStatusChanged(false);
            }
        }
    };

}
