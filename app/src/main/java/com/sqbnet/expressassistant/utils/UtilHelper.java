package com.sqbnet.expressassistant.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by virgil on 6/29/15.
 */
public class UtilHelper {

    public static boolean isMobileNO(String mobiles){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static String getSharedUserId(Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.USER_INFO, Activity.MODE_PRIVATE);
        String userId = sharedPreferences.getString(Constants.USER_ID, null);
        return  userId;
    }

    public static void setSharedUserId(String userId, Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.USER_INFO, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.USER_ID, userId);
        editor.commit();
    }
}
