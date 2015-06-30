package com.sqbnet.expressassistant.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
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

    public static String getDateString(long timeStamp) {
        return getDateString(timeStamp, "yyyy.MM.dd HH:mm:ss");
    }

    public static String getDateString(long timeStamp, String format){
        Calendar calendar = Calendar.getInstance();
        DateFormat objFormatter = new SimpleDateFormat(format);
        TimeZone timeZone = calendar.getTimeZone();
        objFormatter.setTimeZone(timeZone);
        String result = objFormatter.format(getDate(timeStamp));
        calendar.clear();
        return result;
    }

    public static Date getDate(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);//edit
        return calendar.getTime();
    }
}
