package com.sqbnet.expressassistant.external.map;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.sqbnet.expressassistant.MyApplication;

/**
 * Created by Andy on 7/12/2015.
 */
public abstract class AbstractMapProvider implements IMapProvider {
    private static PackageManager pkgManager;
    private String pkgDisplayName;

    public AbstractMapProvider() {
        if (pkgManager == null) {
            pkgManager = MyApplication.getInst().getPackageManager();
        }
    }

    @Override
    public String getPackageDisplayName() {
        if (pkgDisplayName == null) {
            try {
                ApplicationInfo pkgInfo = pkgManager.getApplicationInfo(getPackageName(), 0);
                pkgDisplayName = pkgManager.getApplicationLabel(pkgInfo).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return pkgDisplayName;
    }
}
