package com.sqbnet.expressassistant.external.map;

import android.app.Activity;
import android.content.Intent;

import com.sqbnet.expressassistant.MyApplication;
import com.sqbnet.expressassistant.utils.CustomConstants;

/**
 * Created by Andy on 7/12/2015.
 */
public class BaiduMapProvider extends AbstractMapProvider {
    @Override
    public void startMapByLocation(Activity currentActivity, double latitude, double longitude, String title, String content) {
        try {
            Intent intent = Intent.getIntent("intent://map/marker?location=" + latitude + "," + longitude + "" +
                    "&title=" + title + "&content=" + content + "&src=" + MyApplication.getInst().getApplicationInfo().packageName + "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            currentActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startMapByAddress(Activity currentActivity, String address) {
        try {
            Intent intent = Intent.getIntent("intent://map/geocoder?address=" + address +
                    "&src=" + MyApplication.getInst().getApplicationInfo().packageName + "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            currentActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPackageName() {
        return CustomConstants.APP_BAIDU_DITU;
    }
}
