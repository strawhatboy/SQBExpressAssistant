package com.sqbnet.expressassistant.external.map;

import android.util.Log;

import com.sqbnet.expressassistant.utils.CustomConstants;
import com.sqbnet.expressassistant.utils.UtilHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andy on 7/12/2015.
 */
public class MapProviderFactory {

    private static MapProviderFactory sInst;

    private List<IMapProvider> mapProviders;

    private MapProviderFactory() {

    }

    public static MapProviderFactory getInst(){
        if(sInst == null){
            synchronized (MapProviderFactory.class){
                if(sInst == null){
                    sInst = new MapProviderFactory();
                }
            }
        }
        return sInst;
    }

    public List<IMapProvider> getAvailableMapProviders() {
        if (mapProviders == null) {
            mapProviders = new ArrayList<IMapProvider>();

            if (UtilHelper.isAppInstalled(CustomConstants.APP_BAIDU_DITU)) {
                mapProviders.add(new BaiduMapProvider());
            }

            if (UtilHelper.isAppInstalled(CustomConstants.APP_GAODE_DITU)) {
                mapProviders.add(new GaodeMapProvider());
            }

            if (mapProviders.size() == 0) {
                // error
                Log.e("Map", "no map app found in system");
                mapProviders = null;
            }
        }
        return mapProviders;
    }
}
