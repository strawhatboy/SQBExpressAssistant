package com.sqbnet.expressassistant.external.map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.sqbnet.expressassistant.MyApplication;
import com.sqbnet.expressassistant.utils.CustomConstants;

/**
 * Created by Andy on 7/12/2015.
 */
public class GaodeMapProvider extends AbstractMapProvider {
    @Override
    public void startMapByLocation(Activity currentActivity, double latitude, double longitude, String title, String content) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("androidamap://viewMap?sourceApplication=" + MyApplication.getInst().getApplicationInfo().packageName +
                    "&poiname=" + title + " " + content + "&lat=" + latitude + "&lon=" + longitude + "&dev=0"));
            intent.setPackage(CustomConstants.APP_GAODE_DITU);
            currentActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startMapByAddress(Activity currentActivity, String address) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("androidamap://viewGeo?sourceApplication=" + MyApplication.getInst().getApplicationInfo().packageName + "&addr=" + address));
            intent.setPackage(CustomConstants.APP_GAODE_DITU);
            currentActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPackageName() {
        return CustomConstants.APP_GAODE_DITU;
    }
}
