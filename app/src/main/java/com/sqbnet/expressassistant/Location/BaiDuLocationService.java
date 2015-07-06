package com.sqbnet.expressassistant.Location;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.sqbnet.expressassistant.MyApplication;

/**
 * Created by Andy on 7/6/2015.
 */
public class BaiDuLocationService {

    private static BaiDuLocationService sInst;
    private LocationClient mLocationClient;
    private GeofenceClient mGeofenceClient;

    public static BaiDuLocationService getInst(){
        if(sInst == null){
            synchronized (GPSLocation.class){
                if(sInst == null){
                    sInst = new BaiDuLocationService();
                }
            }
        }
        return  sInst;
    }

    private BaiDuLocationService() {
        Context context = MyApplication.getInst().getApplicationContext();
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                Log.i("BDLocation", "Got location: latitude: " + bdLocation.getLatitude() + ", longtitue: " +
                bdLocation.getLongitude() + ", time: " + bdLocation.getTime() + ", type: " + bdLocation.getLocType());
            }
        });
        mGeofenceClient = new GeofenceClient(context);
    }

    public LocationClient getLocationClient() {
        return mLocationClient;
    }

    public GeofenceClient getGeofenceClient() {
        return mGeofenceClient;
    }
}
