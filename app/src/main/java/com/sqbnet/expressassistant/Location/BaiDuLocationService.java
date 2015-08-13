package com.sqbnet.expressassistant.Location;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.sqbnet.expressassistant.MyApplication;
import com.sqbnet.expressassistant.mode.MyLocation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Andy on 7/6/2015.
 */
public class BaiDuLocationService {

    private static BaiDuLocationService sInst;
    private LocationClient mLocationClient;
    private GeofenceClient mGeofenceClient;
    private GeoCoder mSearch;
    private MyLocation locationGotFromBaidu;
    private Map<String, ILocateCallback> callbacks;
    private Map<String, Boolean> isOneTimeCallback;

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
        callbacks = new HashMap<String, ILocateCallback>();
        isOneTimeCallback = new HashMap<String, Boolean>();
        Context context = MyApplication.getInst().getApplicationContext();
        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation || bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                    Log.i("BDLocation", "Got location: latitude: " + bdLocation.getLatitude() + ", longtitue: " +
                            bdLocation.getLongitude() + ", time: " + bdLocation.getTime() + ", type: " + bdLocation.getLocType());
                    locationGotFromBaidu = new MyLocation(bdLocation.getLatitude(), bdLocation.getLongitude());
                    Iterator<Map.Entry<String, ILocateCallback>> iterator = callbacks.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, ILocateCallback> entry = iterator.next();
                        String key = entry.getKey();
                        entry.getValue().handleLocationGot(bdLocation.getLatitude(), bdLocation.getLongitude());
                        if (isOneTimeCallback.containsKey(key)) {
                            if (isOneTimeCallback.get(key)) {
                                callbacks.remove(key);
                                isOneTimeCallback.remove(key);
                            }
                        }
                    }
                }
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

    public void getLocationByAddress(String address, final IGeoEncoderCallback callback) {
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Log.w("BaiduLocation", "not found in onGetGeoCodeResult");
                    callback.handleLocationGot(-1, -1);
                    return;
                }
                callback.handleLocationGot(result.getLocation().latitude, result.getLocation().longitude);
            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Log.w("BaiduLocation", "not found in onGetReverseGeoCodeResult");
                    callback.handleAddressGot("");
                    return;
                }
                callback.handleAddressGot(result.getAddress());
            }
        });
        mSearch.geocode(new GeoCodeOption().city("").address(address));

    }

    public double getDistanceBetweenLocations(double latitude1, double longitude1, double latitude2, double longitude2) {
        try {
            return DistanceUtil.getDistance(new LatLng(latitude1, longitude1), new LatLng(latitude2, longitude2));
        }catch (Exception e){
            return 0;
        }
    }

    public interface IGeoEncoderCallback extends ILocateCallback  {
        void handleAddressGot(String address);
    }

    public interface ILocateCallback {
        void handleLocationGot(double latitude, double longitude);
    }

    public MyLocation getLocationGotFromBaidu() {
        return locationGotFromBaidu;
    }

    public void registerLocationListener(String id, ILocateCallback callback) {
        registerLocationListener(id, false, callback);
    }

    public void registerLocationListener(String id, boolean isOneTime, ILocateCallback callback) {
        unregisterLocationListener(id);
        callbacks.put(id, callback);
        isOneTimeCallback.put(id, isOneTime);
    }

    public void unregisterLocationListener(String id) {
        if (callbacks.containsKey(id)) {
            callbacks.remove(id);
            if (isOneTimeCallback.containsKey(id)) {
                isOneTimeCallback.remove(id);
            }
        }
    }
}
