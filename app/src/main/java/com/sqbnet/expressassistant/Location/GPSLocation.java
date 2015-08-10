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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.sqbnet.expressassistant.MyApplication;
import com.sqbnet.expressassistant.Provider.SQBProvider;
import com.sqbnet.expressassistant.mode.MyLocation;
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

    public static boolean isSendingLocation = false;
    private static GPSLocation sInst;
    private static String FROM_GPS = "GPS";
    private static String FROM_NETWORK = "network";
    private static String FROM_BAIDU = "baidu";
    private LocationManager locationManager;
    private static long lastSendTime;

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
    }

    public interface GPSProviderStatusChanged{
        public void onStatusChanged(boolean isEnabled);
    }

    public GPSProviderStatusChanged GPSProviderStatusChanged;

    public boolean openGEPSettings(){
        locationManager = (LocationManager) MyApplication.getInst().getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.v("virgil", "GPS is on");
           return true;
        }
        Log.v("virgil", "GPS is off");
        return false;
    }

    public void start(){
        Log.v("virgil", "GPS update start");
        getCurrentLocation();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60 * 1000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 70 * 1000, 0, networkLocationListener);
        locationManager.addGpsStatusListener(new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int i) {
                switch (i) {
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
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
       startBaiduLocation();
    }

    private void startBaiduLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd0911");
        option.setAddrType("all");
        option.setScanSpan(80 * 1000);
        BaiDuLocationService.getInst().getLocationClient().setLocOption(option);
        BaiDuLocationService.getInst().getLocationClient().registerLocationListener(bdLocationListener);
        BaiDuLocationService.getInst().getLocationClient().start();
    }

    public MyLocation getCurrentLocation(){
        if(openGEPSettings()) {
            Log.v("virgil", "get GPS location");
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                Log.v("virgil", "get network location");
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(location != null) {
                return new MyLocation(location.getLatitude(), location.getLongitude());
            }else {
                BDLocation bdLocation = BaiDuLocationService.getInst().getLocationClient().getLastKnownLocation();
                if(bdLocation != null){
                    return new MyLocation(bdLocation.getLatitude(), bdLocation.getLongitude());
                }
            }
        }
        return null;
    }

    public void stop(){
        Log.i("virgil", "GPS update stop");
        if(locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        if(networkLocationListener != null){
            locationManager.removeUpdates(networkLocationListener);
        }
        BaiDuLocationService.getInst().getLocationClient().unRegisterLocationListener(bdLocationListener);
    }

    private void sendLocationToServer(final String latitude, final String longitude, String from){
        try {
            Log.i("virgil", "Lat:" + latitude + ", Lng:" + longitude + ", from:" + from);
            if(!isSendingLocation){
                Log.i("virgil", "isSendingLocation false");
                return;
            }

            final String useId = UtilHelper.getSharedUserId();
            if(useId == null)
                return;
            Long now = System.currentTimeMillis();
            Log.i("virgil", String.valueOf(now - lastSendTime));
            Long minutes = (now - lastSendTime)/(60*1000);
            Log.i("virgil", "minutes:" + minutes);
            if(minutes < 1)
                return;
            lastSendTime = System.currentTimeMillis();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        SQBProvider.getInst().updatePosition(useId, latitude, longitude, new SQBResponseListener() {
                            @Override
                            public void onResponse(SQBResponse response) {
                                if(response == null)
                                    return;
                                Log.i("virgil", "Location update success");
                                Log.i("virgil", response.getCode());
                                Log.i("virgil", response.getMsg());
                                Log.i("virgil", response.getData().toString());
                            }
                        });
                    }catch (Exception e){
                        Log.i("virgil", "-----sendLocationToServer Error in thread------");
                        Log.e("GPSLocation", "virgil", e);
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }catch (Exception e){
            Log.i("virgil", "-----sendLocationToServer Error------");
            Log.e("GPSLocation", "virgil", e);
            e.printStackTrace();
        }
    }

    private final  LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("virgil", "network onLocationChanged");
            sendLocationToServer(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), FROM_NETWORK);
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

    private final BDLocationListener bdLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.i("BDLocation", "Got location: latitude: " + bdLocation.getLatitude() + ", longtitue: " +
                    bdLocation.getLongitude() + ", time: " + bdLocation.getTime() + ", type: " + bdLocation.getLocType());
            sendLocationToServer(String.valueOf(bdLocation.getLatitude()), String.valueOf(bdLocation.getLongitude()), FROM_BAIDU);
        }
    };

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("virgil", "GPS onLocationChanged send location");
            sendLocationToServer(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), FROM_GPS);
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
