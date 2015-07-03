package com.sqbnet.expressassistant.Location;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.sqbnet.expressassistant.MyApplication;
import com.sqbnet.expressassistant.utils.UtilHelper;

/**
 * Created by virgil on 7/2/15.
 */
public class GPSLocation {

    private static GPSLocation sInst;

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

    public boolean openGEPSettings(){
        LocationManager locationManager = (LocationManager) MyApplication.getInst().getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            return true;
        }
        return false;
    }

    public void getLoation(){
        LocationManager locationManager = (LocationManager) MyApplication.getInst().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        Log.i("virgil", "Lat:" + location.getLatitude() + ", Lng:" + location.getLongitude());
        locationManager.requestLocationUpdates(provider, 1*1000, 0, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("virgil", "Lat:" + location.getLatitude() + ", Lng:" + location.getLongitude());
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
}
