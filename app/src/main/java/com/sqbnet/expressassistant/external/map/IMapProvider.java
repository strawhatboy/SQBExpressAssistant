package com.sqbnet.expressassistant.external.map;

import android.app.Activity;

/**
 * Created by Andy on 7/12/2015.
 */
public interface IMapProvider {
    void startMapByLocation(Activity currentActivity, double latitude, double longitude, String title, String content);
    void startMapByAddress(Activity currentActivity, String address);
    String getPackageName();
    String getPackageDisplayName();
}
